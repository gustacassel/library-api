package com.infnet.libraryapi.controller;

import com.infnet.libraryapi.model.Loan;
import com.infnet.libraryapi.model.LoanStatus;
import com.infnet.libraryapi.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public final class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> getAll() {
        return loanService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return loanService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<Loan> getByStatus(@PathVariable LoanStatus status) {
        return loanService.findByStatus(status);
    }

    @GetMapping("/student/{studentId}")
    public List<Loan> getByStudent(@PathVariable Long studentId) {
        return loanService.findByStudent(studentId);
    }

    @GetMapping("/book/{bookId}")
    public List<Loan> getByBook(@PathVariable Long bookId) {
        return loanService.findByBook(bookId);
    }

    @GetMapping("/overdue")
    public List<Loan> getOverdue() {
        return loanService.findOverdue();
    }

    @PostMapping
    public ResponseEntity<Loan> create(@RequestBody Loan loan) {
        return loanService.create(loan)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
