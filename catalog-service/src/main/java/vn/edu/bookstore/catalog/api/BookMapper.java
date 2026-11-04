package vn.edu.bookstore.catalog.api;

import java.util.List;
import org.springframework.data.domain.Page;
import vn.edu.bookstore.catalog.api.model.*;
import vn.edu.bookstore.catalog.domain.BookEntity;

public class BookMapper {

    public static Book toResponse(BookEntity entity) {
        if (entity == null) return null;
        Book book = new Book();
        book.setIsbn(entity.getIsbn());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setAuthorId(entity.getAuthorId());
        book.setPublishedYear(entity.getPublishedYear());

        Money price = new Money();
        price.setAmount(entity.getPriceAmount());
        if (entity.getPriceCurrency() != null) {
            price.setCurrency(CurrencyCode.fromValue(entity.getPriceCurrency()));
        }
        book.setPrice(price);
        return book;
    }

    public static BookPage toPagedResponse(Page<BookEntity> page) {
        BookPage paged = new BookPage();
        List<Book> content = page.getContent().stream().map(BookMapper::toResponse).toList();
        paged.setContent(content);

        PageMetadata meta = new PageMetadata();
        meta.setNumber(page.getNumber());
        meta.setSize(page.getSize());
        meta.setTotalElements(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        paged.setPage(meta);
        return paged;
    }

    public static BookEntity toEntity(BookCreateRequest request) {
        if (request == null) return null;
        return new BookEntity(
                request.getIsbn(),
                request.getTitle(),
                request.getAuthor(),
                null,
                request.getPublishedYear(),
                request.getPrice() != null ? request.getPrice().getAmount() : null,
                (request.getPrice() != null && request.getPrice().getCurrency() != null) 
                        ? request.getPrice().getCurrency().getValue() : "VND"
        );
    }

    public static BookEntity toEntity(BookUpdateRequest request) {
        if (request == null) return null;
        return new BookEntity(
                null,
                request.getTitle(),
                request.getAuthor(),
                null,
                request.getPublishedYear(),
                request.getPrice() != null ? request.getPrice().getAmount() : null,
                (request.getPrice() != null && request.getPrice().getCurrency() != null) 
                        ? request.getPrice().getCurrency().getValue() : "VND"
        );
    }
}
