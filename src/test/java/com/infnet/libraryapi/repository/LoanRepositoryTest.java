package com.infnet.libraryapi.repository;

import com.infnet.libraryapi.model.Book;
import com.infnet.libraryapi.model.Loan;
import com.infnet.libraryapi.model.LoanStatus;
import com.infnet.libraryapi.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Book book;
    private Student student;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        entityManager.persist(book);

        student = new Student();
        student.setName("Maria Silva");
        student.setEmail("maria@email.com");
        student.setEnrollmentNumber("2026001");
        entityManager.persist(student);
    }

    private Loan newLoan(LoanStatus status, LocalDate loanDate, LocalDate dueDate) {
        var loan = new Loan();
        loan.setBook(book);
        loan.setStudent(student);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus(status);
        return loan;
    }

    @Test
    void shouldSaveLoanWithBookAndStudentRelationships() {
        var saved = repository.save(newLoan(LoanStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusDays(14)));

        var found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getBook().getTitle()).isEqualTo("Clean Code");
        assertThat(found.get().getStudent().getName()).isEqualTo("Maria Silva");
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByStatus() {
        repository.save(newLoan(LoanStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusDays(14)));
        repository.save(newLoan(LoanStatus.RETURNED, LocalDate.now().minusDays(30), LocalDate.now().minusDays(16)));

        var active = repository.findByStatus(LoanStatus.ACTIVE);
        var returned = repository.findByStatus(LoanStatus.RETURNED);

        assertThat(active).hasSize(1);
        assertThat(returned).hasSize(1);
    }

    @Test
    void shouldFindByStudentIdAndBookId() {
        repository.save(newLoan(LoanStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusDays(14)));

        assertThat(repository.findByStudentId(student.getId())).hasSize(1);
        assertThat(repository.findByBookId(book.getId())).hasSize(1);
        assertThat(repository.findByStudentId(999L)).isEmpty();
    }

    @Test
    void shouldFindOverdueLoansWithCustomQuery() {
        // ativo e vencido ha 5 dias -> deve aparecer
        repository.save(newLoan(LoanStatus.ACTIVE, LocalDate.now().minusDays(20), LocalDate.now().minusDays(5)));
        // ativo mas dentro do prazo -> nao deve aparecer
        repository.save(newLoan(LoanStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusDays(14)));
        // vencido porem ja devolvido -> nao deve aparecer
        repository.save(newLoan(LoanStatus.RETURNED, LocalDate.now().minusDays(30), LocalDate.now().minusDays(10)));

        var overdue = repository.findOverdue(LocalDate.now());

        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).getDueDate()).isBefore(LocalDate.now());
        assertThat(overdue.get(0).getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }
}
