package com.infnet.libraryapi.service;

import com.infnet.libraryapi.model.AuditAction;
import com.infnet.libraryapi.model.Student;
import com.infnet.libraryapi.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {
    private static final String ENTITY_NAME = "STUDENT";

    private final StudentRepository repository;
    private final AuditService auditService;

    public StudentService(StudentRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Student> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Student> findByEnrollmentNumber(String enrollmentNumber) {
        return repository.findByEnrollmentNumber(enrollmentNumber);
    }

    public List<Student> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Student save(Student student) {
        var saved = repository.save(student);
        auditService.record(ENTITY_NAME, saved.getId(), AuditAction.CREATE,
                "Aluno cadastrado: '%s' (matricula %s)".formatted(saved.getName(), saved.getEnrollmentNumber()));
        return saved;
    }

    @Transactional
    public Optional<Student> update(Long id, Student studentDetails) {
        var student = repository.findById(id);

        if (student.isEmpty()) {
            return Optional.empty();
        }

        var updatedStudent = student.get();
        var changes = new StringBuilder();
        appendChange(changes, "name", updatedStudent.getName(), studentDetails.getName());
        appendChange(changes, "email", updatedStudent.getEmail(), studentDetails.getEmail());
        appendChange(changes, "enrollmentNumber", updatedStudent.getEnrollmentNumber(), studentDetails.getEnrollmentNumber());
        appendChange(changes, "birthDate", updatedStudent.getBirthDate(), studentDetails.getBirthDate());

        updatedStudent.setName(studentDetails.getName());
        updatedStudent.setEmail(studentDetails.getEmail());
        updatedStudent.setEnrollmentNumber(studentDetails.getEnrollmentNumber());
        updatedStudent.setBirthDate(studentDetails.getBirthDate());

        var saved = repository.save(updatedStudent);
        auditService.record(ENTITY_NAME, saved.getId(), AuditAction.UPDATE,
                changes.isEmpty() ? "Nenhum campo alterado" : changes.toString());
        return Optional.of(saved);
    }

    @Transactional
    public boolean delete(Long id) {
        var student = repository.findById(id);
        if (student.isEmpty()) {
            return false;
        }

        repository.delete(student.get());
        auditService.record(ENTITY_NAME, id, AuditAction.DELETE,
                "Aluno removido: '%s'".formatted(student.get().getName()));
        return true;
    }

    private void appendChange(StringBuilder changes, String field, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            if (!changes.isEmpty()) {
                changes.append("; ");
            }
            changes.append("%s: '%s' -> '%s'".formatted(field, oldValue, newValue));
        }
    }
}
