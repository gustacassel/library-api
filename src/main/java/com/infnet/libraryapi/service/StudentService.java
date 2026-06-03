package com.infnet.libraryapi.service;

import com.infnet.libraryapi.model.Student;
import com.infnet.libraryapi.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }

    public Student save(Student student) {
        return repository.save(student);
    }

    public Optional<Student> update(Long id, Student studentDetails) {
        var student = repository.findById(id);

        if (student.isEmpty()) {
            return Optional.empty();
        }

        var updatedStudent = student.get();
        updatedStudent.setName(studentDetails.getName());
        updatedStudent.setEmail(studentDetails.getEmail());
        updatedStudent.setEnrollmentNumber(studentDetails.getEnrollmentNumber());
        updatedStudent.setBirthDate(studentDetails.getBirthDate());

        return Optional.of(repository.save(updatedStudent));
    }

    public boolean delete(Long id) {
        var student = repository.findById(id);
        if (student.isEmpty()) {
            return false;
        }

        repository.delete(student.get());
        return true;
    }
}
