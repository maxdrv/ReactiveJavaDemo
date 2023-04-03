package com.example.reactive.core.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Помимо ReactiveCrudRepository, существует также расширение ReactiveSortingRepository,
 * которое предоставляет дополнительные методы для извлечения отсортированных сущностей.
 */
public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {

    Flux<Student> findByName(String name);

}
