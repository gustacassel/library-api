package com.infnet.libraryapi.repository;

import com.infnet.libraryapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
