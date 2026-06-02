package com.infnet.libraryapi.service;

import com.infnet.libraryapi.model.Book;
import com.infnet.libraryapi.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    public Book save(Book book) {
        return repository.save(book);
    }

    public Optional<Book> update(Long id, Book bookDetails) {
        var book = repository.findById(id);

        if (book.isEmpty()) {
            return Optional.empty();
        }

        var updatedBook = book.get();
        updatedBook.setTitle(bookDetails.getTitle());
        updatedBook.setAuthor(bookDetails.getAuthor());

        return Optional.of(repository.save(updatedBook));
    }

    public boolean delete(Long id) {
        var book = repository.findById(id);
        if (book.isEmpty()) {
            return false;
        }

        repository.delete(book.get());
        return true;
    }
}
