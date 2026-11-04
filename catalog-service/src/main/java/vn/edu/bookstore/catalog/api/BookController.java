package vn.edu.bookstore.catalog.api;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.bookstore.catalog.api.generated.BooksApi;
import vn.edu.bookstore.catalog.api.model.Book;
import vn.edu.bookstore.catalog.api.model.BookCreateRequest;
import vn.edu.bookstore.catalog.api.model.BookPage;
import vn.edu.bookstore.catalog.api.model.BookUpdateRequest;
import vn.edu.bookstore.catalog.domain.BookEntity;
import vn.edu.bookstore.catalog.service.BookService;
import vn.edu.bookstore.catalog.service.PreconditionRequiredException;

@RestController
public class BookController implements BooksApi {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<BookPage> listBooks(String q, Integer page, Integer size, String sort) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        if (pageSize > 100) {
            throw new IllegalArgumentException("Size vượt giới hạn");
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, toSort(sort));
        Page<BookEntity> result = service.list(q, pageable);
        return ResponseEntity.ok(BookMapper.toPagedResponse(result));
    }

    @Override
    public ResponseEntity<Book> getBook(String isbn, String ifNoneMatch) {
        BookEntity book = service.get(isbn);
        String etag = "\"" + book.getVersion() + "\"";
        if (ifNoneMatch != null && (ifNoneMatch.equals(etag) || ifNoneMatch.equals(String.valueOf(book.getVersion())))) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(BookMapper.toResponse(book));
    }

    @Override
    public ResponseEntity<Book> createBook(BookCreateRequest request) {
        BookEntity book = service.create(BookMapper.toEntity(request));
        String etag = "\"" + book.getVersion() + "\"";
        URI location = URI.create("/api/v1/books/" + book.getIsbn());
        return ResponseEntity.created(location).eTag(etag).body(BookMapper.toResponse(book));
    }

    @Override
    public ResponseEntity<Book> updateBook(String isbn, BookUpdateRequest request, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new PreconditionRequiredException("Thiếu header If-Match");
        }
        long version = parseEtag(ifMatch);
        BookEntity updated = service.update(isbn, BookMapper.toEntity(request), version);
        String etag = "\"" + updated.getVersion() + "\"";
        return ResponseEntity.ok().eTag(etag).body(BookMapper.toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteBook(String isbn) {
        service.delete(isbn);
        return ResponseEntity.noContent().build();
    }

    private static long parseEtag(String etag) {
        String clean = etag.replace("\"", "").trim();
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static Sort toSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdDate");
        }
        String[] parts = sortStr.split(",");
        String prop = parts[0].trim();
        if ("price".equals(prop)) prop = "priceAmount";
        Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }
}
