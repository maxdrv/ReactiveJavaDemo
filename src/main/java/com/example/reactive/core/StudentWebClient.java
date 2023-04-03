package com.example.reactive.core;

import com.example.reactive.core.repository.Student;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StudentWebClient {
    WebClient client = WebClient.create("http://localhost:8080");

    public Mono<Student> get(long id) {
        return client
                .get()
                .uri("/students/" + id)
                .headers(headers -> headers.setBasicAuth("user", "userpwd"))
                .retrieve()
                .bodyToMono(Student.class);
    }

    public Flux<Student> getAll() {
        return client.get()
                .uri("/students")
                .headers(headers -> headers.setBasicAuth("user", "userpwd"))
                .retrieve()
                .bodyToFlux(Student.class);
    }

    public Flux<Student> findByName(String name) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/students")
                        .queryParam("name", name)
                        .build())
                .headers(headers -> headers.setBasicAuth("user", "userpwd"))
                .retrieve()
                .bodyToFlux(Student.class);
    }

    public Mono<Student> create(Student s)  {
        return client.post()
                .uri("/students")
                .headers(headers -> headers.setBasicAuth("admin", "adminpwd"))
                .body(Mono.just(s), Student.class)
                .retrieve()
                .bodyToMono(Student.class);
    }

    public Mono<Student> update(Student student)  {
        return client
                .put()
                .uri("/students/" + student.getId())
                .headers(headers -> headers.setBasicAuth("admin", "adminpwd"))
                .body(Mono.just(student), Student.class)
                .retrieve()
                .bodyToMono(Student.class);
    }

    public Mono<Void> delete(long id) {
        return client
                .delete()
                .uri("/students/" + id)
                .headers(headers -> headers.setBasicAuth("admin", "adminpwd"))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
