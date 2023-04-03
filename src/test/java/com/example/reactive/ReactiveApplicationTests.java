package com.example.reactive;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class ReactiveApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * nothing will emit until no one subscribes
     */
    @Nested
    class ReactorCorePrimitives {

        @Test
        void simpleFluxExample() {
            Flux<String> fluxColors = Flux.just("red", "green", "blue");  // 1 to N elements
            fluxColors.subscribe(e -> System.out.println(e));
        }

        @Test
        void simpleMonoExample() {
            Mono<String> monoColor = Mono.just("white");  // 0 or 1 elements
            monoColor.subscribe(e -> System.out.println(e));
        }

        @Test
        void fluxLogging() {
            Flux<String> fluxColors = Flux.just("red", "green", "blue");
            fluxColors.log().subscribe(e -> System.out.println(e));
        }

        @Test
        void fluxNothingEmittedWithoutSubscribers() {
            Flux<String> fluxColors = Flux.just("red", "green", "blue");
            fluxColors.log();  // subscription is absent
        }

    }

    @Nested
    class Operations {

        @Test
        void mapExample() {
            Flux<String> fluxColors = Flux.just("red", "green", "blue");
            fluxColors.map(color -> color.charAt(0)).subscribe(System.out::println);
        }

        @Test
        void zipExample() {
            Flux<String> fluxFruits = Flux.just("apple", "orange");
            Flux<String> fluxColors = Flux.just("red", "green", "blue");
            Flux<Integer> fluxInts = Flux.just(10, 20, 30);

            // we will see only 2 tuples of elements, because we need 3rd element in fluxFruits to zip with others
            // until it appears there will be only 2 tuples
            Flux.zip(fluxFruits, fluxColors, fluxInts).subscribe(e -> System.out.println(e));
        }
    }

    @Nested
    class ErrorHandling {

        @Test
        void onErrorExample() {
            Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                    .map(i -> "10/" + i + "=" + (10 / i));

            // there will be custom print on error
            fluxCalc.subscribe(
                    e -> System.out.println("Next: " + e),
                    err -> System.err.println("Error: " + err)
            );
        }

        @Test
        void errorWithoutHandling() {
            Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                    .map(i -> "10/" + i + "=" + (10 / i));

            // there will be stack trace
            fluxCalc.subscribe(
                    e -> System.out.println("Next: " + e)
            );
        }

        @Test
        void onErrorReturnExample() {
            Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                    .map(i -> "10/" + i + "=" + (10 / i))
                    .onErrorReturn(ArithmeticException.class, "Division by zero not allowed");

            // there will be no error, but only two elements will be shown and termination occur
            fluxCalc.subscribe(
                    e -> System.out.println("Next: " + e),
                    err -> System.err.println("Error: " + err)
            );
        }

    }

    @Nested
    class Testing {

        @Test
        void stepVerifierExample() {
            Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                    .map(i -> "10/" + i + "=" + (10 / i));

            StepVerifier.create(fluxCalc)
                    .expectNextCount(1)
                    .expectError(ArithmeticException.class)
                    .verify();
        }
    }

    @Nested
    class ModelOfParallelism {

        @Test
        void publishSubscribeExample() {
            Scheduler schedulerA = Schedulers.newParallel("Scheduler A");
            Scheduler schedulerB = Schedulers.newParallel("Scheduler B");
            Scheduler schedulerC = Schedulers.newParallel("Scheduler C");

            // first and second operations executes on scheduler A, because first subscribeOn impact whole chain
            Flux.just(1)
                    .map(i -> {
                        System.out.println("First map: " + Thread.currentThread().getName());
                        return i;
                    })
                    .subscribeOn(schedulerA)
                    .map(i -> {
                        System.out.println("Second map: " + Thread.currentThread().getName());
                        return i;
                    })
                    .publishOn(schedulerB)  // switch execution to scheduler B
                    .map(i -> {
                        System.out.println("Third map: " + Thread.currentThread().getName());
                        return i;
                    })
                    .subscribeOn(schedulerC)  // has no effect, why?
                    .map(i -> {
                        System.out.println("Fourth map: " + Thread.currentThread().getName());
                        return i;
                    })
                    .publishOn(schedulerA)  // switch execution to scheduler C
                    .map(i -> {
                        System.out.println("Fifth map: " + Thread.currentThread().getName());
                        return i;
                    })
                    .blockLast();

        }

    }

    @Nested
    class Backpressure {

        @Test
        void backpressureExample() {
            // we can control client, so client only send certain amount of events at a time

            Flux.range(1, 5)
                    .subscribe(new Subscriber<Integer>() {
                        private Subscription s;
                        int counter;

                        @Override
                        public void onSubscribe(Subscription s) {
                            System.out.println("onSubscribe");
                            this.s = s;
                            System.out.println("Requesting 2 emissions");
                            s.request(2);
                        }

                        @Override
                        public void onNext(Integer i) {
                            System.out.println("onNext " + i);
                            counter++;
                            if (counter % 2 == 0) {
                                System.out.println("Requesting 2 emissions");
                                s.request(2);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            System.err.println("onError");
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("onComplete");
                        }
                    });
        }
    }

    @Nested
    class Publishers {

        @Test
        void coldPublisherExample() throws InterruptedException {
            Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1));
            Thread.sleep(2000);
            intervalFlux.subscribe(i -> System.out.println("Subscriber A, value " + i));  // 0 1 2 3 4
            Thread.sleep(2000);
            intervalFlux.subscribe(i -> System.out.println("Subscriber B, value " + i));  // 0 1 2
            Thread.sleep(3000);

            // оба потока начнут получать значения с 0 и будут работать до конца программы
        }

        @Test
        void hotPublisherExample() throws InterruptedException {
            Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1));
            ConnectableFlux<Long> intervalCF = intervalFlux.publish();
            intervalCF.connect();
            Thread.sleep(2000);
            intervalCF.subscribe(i -> System.out.println("Subscriber A, value " + i));  // 2 3 4 5 6
            Thread.sleep(2000);
            intervalCF.subscribe(i -> System.out.println("Subscriber B, value " + i));  // 4 5 6
            Thread.sleep(3000);

            // публикация начнется до подписки
            // новые подписанты будут получать новые значения, а не значения с начала
        }
    }

    @Nested
    class OtherStaff {

        @Test
        void executeSynchronousBlockingCall() {
            Mono<String> blockingWrapper = Mono.fromCallable(() -> {
                /* make a remote synchronous call */
                Thread.sleep(1000);
                return "executed";
            });

            // используя Schedulers.boundedElastic() мы гарантируем, что каждая подписка выполняется на выделенном
            // однопоточном работнике, не влияя на другую не блокирующую обработку
            blockingWrapper = blockingWrapper.subscribeOn(Schedulers.boundedElastic());
            blockingWrapper.log().subscribe(i -> System.out.println(i));
        }

        @Test
        void contextTest() {
            // Аналог ThreadLocal для императивного стиля.
            // Представляет из себя что-то проде Map, которая доступна по всему конвейеру

            String key = "key";
            Mono<String> mono = Mono.just("anything")
                    .transformDeferredContextual((s, ctx) -> s.map(v -> v + " for " + ctx.getOrDefault(key, "NO VALUE")))
                    .contextWrite(ctx -> ctx.put(key, "MyValue"));

            StepVerifier.create(mono)
                    .expectNext("anything for MyValue")
                    .verifyComplete();
        }
    }
}
