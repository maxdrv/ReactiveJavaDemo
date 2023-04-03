package com.example.reactive.core.repository;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RepositoryWithDatabaseClient {

    private final ConnectionFactory connectionFactory;

    public Flux<Student> findAll() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        return client.sql("select * from student")
                .map(row -> {
                    var id = row.get("id", Long.class);
                    var name = row.get("name", String.class);
                    return new Student(id, name);
                })
                .all();
    }

}
