package com.example.reactive.core.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RepositoryWithEntityTemplate {

    @Autowired
    private R2dbcEntityTemplate template;

    public Flux<Student> findAll() {
        return template.select(Student.class).all();
    }

    public Mono<Void> delete(Student student) {
        return template.delete(student).then();
    }

}
