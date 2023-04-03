package com.example.reactive.core;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
public class StudentService {

    private final Map<Long, Student> students;
    private final AtomicLong seq = new AtomicLong(1);

    public StudentService() {
        this.students = new ConcurrentHashMap<>();
        var stud1 = new Student(seq.getAndIncrement(), "name-1");
        var stud2 = new Student(seq.getAndIncrement(), "name-2");
        this.students.put(stud1.getId(), stud1);
        this.students.put(stud2.getId(), stud2);
    }

    public Mono<Student> findStudentById(Long id) {
        return Mono.justOrEmpty(students.get(id));
    }

    public Flux<Student> findStudentsByName(@Nullable String name) {
        Stream<Student> stream = students.values().stream();
        if (name != null) {
            stream = stream.filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()));
        }
        return Flux.fromStream(stream);
    }

    public Mono<Student> addNewStudent(Student student) {
        Long id = seq.getAndIncrement();
        if (student.getId() == null) {
            student.setId(id);
        }
        students.put(student.getId(), student);
        return Mono.just(student);
    }

    public Mono<Student> updateStudent(Long id, Student student) {
        Student founded = students.get(id);
        if (founded == null) {
            return Mono.empty();
        }
        students.put(id, student);
        return Mono.just(student);
    }

    public Mono<Student> deleteStudent(Student student) {
        Student founded = students.get(student.getId());
        if (founded == null) {
            return Mono.empty();
        }
        return Mono.just(students.remove(student.getId()));
    }

}
