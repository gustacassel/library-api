package com.infnet.libraryapi.repository;

import com.infnet.libraryapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);

    List<Student> findByNameContainingIgnoreCase(String name);
}
