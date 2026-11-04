package vn.edu.bookstore.catalog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.bookstore.catalog.domain.BookEntity;
import vn.edu.bookstore.catalog.domain.BookRepository;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Page<BookEntity> list(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, pageable);
        }
        return repository.findAll(pageable);
    }

    public BookEntity get(String isbn) {
        return repository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Transactional
    public BookEntity create(BookEntity book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateIsbnException(book.getIsbn());
        }
        return repository.save(book);
    }

    @Transactional
    public BookEntity update(String isbn, BookEntity update, long expectedVersion) {
        BookEntity existing = get(isbn);
        if (existing.getVersion() != expectedVersion) {
            throw new PreconditionFailedException("Phiên bản ETag không khớp");
        }
        existing.setTitle(update.getTitle());
        existing.setAuthor(update.getAuthor());
        if (update.getPublishedYear() != null) {
            existing.setPublishedYear(update.getPublishedYear());
        }
        existing.setPriceAmount(update.getPriceAmount());
        existing.setPriceCurrency(update.getPriceCurrency());
        
        return repository.save(existing);
    }

    @Transactional
    public void delete(String isbn) {
        BookEntity book = get(isbn);
        repository.delete(book);
    }
}
