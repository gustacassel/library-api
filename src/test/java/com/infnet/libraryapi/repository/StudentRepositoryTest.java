package com.infnet.libraryapi.repository;

import com.infnet.libraryapi.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    private Student newStudent(String name, String email, String enrollmentNumber) {
        var student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setEnrollmentNumber(enrollmentNumber);
        student.setBirthDate(LocalDate.of(2000, 1, 15));
        return student;
    }

    @Test
    void shouldSaveAndFindStudentById() {
        var saved = repository.save(newStudent("Maria Silva", "maria@email.com", "2026001"));

        var found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Maria Silva");
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByEmail() {
        repository.save(newStudent("Maria Silva", "maria@email.com", "2026001"));

        var found = repository.findByEmail("maria@email.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEnrollmentNumber()).isEqualTo("2026001");
    }

    @Test
    void shouldFindByEnrollmentNumber() {
        repository.save(newStudent("Joao Souza", "joao@email.com", "2026002"));

        var found = repository.findByEnrollmentNumber("2026002");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Joao Souza");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        repository.save(newStudent("Maria Silva", "maria@email.com", "2026001"));
        repository.save(newStudent("Ana Maria Souza", "ana@email.com", "2026003"));
        repository.save(newStudent("Pedro Santos", "pedro@email.com", "2026004"));

        var found = repository.findByNameContainingIgnoreCase("maria");

        assertThat(found).hasSize(2);
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        repository.saveAndFlush(newStudent("Maria Silva", "maria@email.com", "2026001"));

        assertThatThrownBy(() ->
                repository.saveAndFlush(newStudent("Outra Maria", "maria@email.com", "2026099")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
