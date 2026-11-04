package vn.edu.bookstore.catalog.domain;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private Long authorId;

    private Integer publishedYear;

    @Column(nullable = false)
    private BigDecimal priceAmount;

    @Column(nullable = false)
    private String priceCurrency;

    @Version
    private Long version;

    private Instant updatedAt;

    public BookEntity() {
    }

    public BookEntity(String isbn, String title, String author, Long authorId, Integer publishedYear, BigDecimal priceAmount, String priceCurrency) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.publishedYear = publishedYear;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Long getAuthorId() { return authorId; }
    public Integer getPublishedYear() { return publishedYear; }
    public BigDecimal getPriceAmount() { return priceAmount; }
    public String getPriceCurrency() { return priceCurrency; }
    public Long getVersion() { return version; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public void setPriceAmount(BigDecimal priceAmount) { this.priceAmount = priceAmount; }
    public void setPriceCurrency(String priceCurrency) { this.priceCurrency = priceCurrency; }
}
