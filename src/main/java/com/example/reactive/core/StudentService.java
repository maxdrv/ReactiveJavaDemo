package com.example.reactive.core;

import com.example.reactive.core.repository.Student;
import com.example.reactive.core.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Mono<Student> findStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Flux<Student> findStudentsByName(@Nullable String name) {
        return name != null ? studentRepository.findByName(name) : studentRepository.findAll();
    }

    public Mono<Student> addNewStudent(Student student) {
        return studentRepository.save(student);
    }

    public Mono<Student> updateStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .flatMap(s -> {
                    student.setId(s.getId());
                    return studentRepository.save(student);
                });
    }

    public Mono<Void> deleteStudent(Student student) {
        return studentRepository.delete(student);
    }

}
