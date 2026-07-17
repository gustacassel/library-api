package com.infnet.libraryapi.service;

import com.infnet.libraryapi.model.AuditAction;
import com.infnet.libraryapi.model.Loan;
import com.infnet.libraryapi.model.LoanStatus;
import com.infnet.libraryapi.repository.BookRepository;
import com.infnet.libraryapi.repository.LoanRepository;
import com.infnet.libraryapi.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private static final String ENTITY_NAME = "LOAN";
    private static final int DEFAULT_LOAN_DAYS = 14;

    private final LoanRepository repository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    public LoanService(LoanRepository repository,
                       BookRepository bookRepository,
                       StudentRepository studentRepository,
                       AuditService auditService) {
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.auditService = auditService;
    }

    public List<Loan> findAll() {
        return repository.findAll();
    }

    public Optional<Loan> findById(Long id) {
        return repository.findById(id);
    }

    public List<Loan> findByStatus(LoanStatus status) {
        return repository.findByStatus(status);
    }

    public List<Loan> findByStudent(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<Loan> findByBook(Long bookId) {
        return repository.findByBookId(bookId);
    }

    public List<Loan> findOverdue() {
        return repository.findOverdue(LocalDate.now());
    }

    @Transactional
    public Optional<Loan> create(Loan loan) {
        if (loan.getBook() == null || loan.getBook().getId() == null
                || loan.getStudent() == null || loan.getStudent().getId() == null) {
            return Optional.empty();
        }

        var book = bookRepository.findById(loan.getBook().getId());
        var student = studentRepository.findById(loan.getStudent().getId());
        if (book.isEmpty() || student.isEmpty()) {
            return Optional.empty();
        }

        loan.setBook(book.get());
        loan.setStudent(student.get());
        if (loan.getLoanDate() == null) {
            loan.setLoanDate(LocalDate.now());
        }
        if (loan.getDueDate() == null) {
            loan.setDueDate(loan.getLoanDate().plusDays(DEFAULT_LOAN_DAYS));
        }
        loan.setStatus(LoanStatus.ACTIVE);

        var saved = repository.save(loan);
        auditService.record(ENTITY_NAME, saved.getId(), AuditAction.CREATE,
                "Emprestimo criado: livro '%s' para aluno '%s', devolucao ate %s"
                        .formatted(saved.getBook().getTitle(), saved.getStudent().getName(), saved.getDueDate()));
        return Optional.of(saved);
    }

    @Transactional
    public Optional<Loan> returnLoan(Long id) {
        var loan = repository.findById(id);
        if (loan.isEmpty() || loan.get().getStatus() == LoanStatus.RETURNED) {
            return Optional.empty();
        }

        var returnedLoan = loan.get();
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(LoanStatus.RETURNED);

        var saved = repository.save(returnedLoan);
        auditService.record(ENTITY_NAME, saved.getId(), AuditAction.UPDATE,
                "Emprestimo devolvido em %s: livro '%s' (aluno '%s')"
                        .formatted(saved.getReturnDate(), saved.getBook().getTitle(), saved.getStudent().getName()));
        return Optional.of(saved);
    }

    @Transactional
    public boolean delete(Long id) {
        var loan = repository.findById(id);
        if (loan.isEmpty()) {
            return false;
        }

        repository.delete(loan.get());
        auditService.record(ENTITY_NAME, id, AuditAction.DELETE,
                "Emprestimo removido: livro '%s' (aluno '%s')"
                        .formatted(loan.get().getBook().getTitle(), loan.get().getStudent().getName()));
        return true;
    }
}
