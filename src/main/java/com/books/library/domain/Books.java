package com.books.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Books.
 */
@Document(collection = "books")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "books")
public class Books implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("book_name")
    private String bookName;

    @Field("genre")
    private String genre;

    @Field("price")
    private Integer price;

    @Field("date")
    private ZonedDateTime date;

    @Field("writer_name")
    private String writerName;

    @Field("edition")
    private Integer edition;

    @Field("description")
    private String description;

    @DBRef
    @Field("stores")
    @JsonIgnore
    private Set<Store> stores = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public Books bookName(String bookName) {
        this.bookName = bookName;
        return this;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getGenre() {
        return genre;
    }

    public Books genre(String genre) {
        this.genre = genre;
        return this;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getPrice() {
        return price;
    }

    public Books price(Integer price) {
        this.price = price;
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Books date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getWriterName() {
        return writerName;
    }

    public Books writerName(String writerName) {
        this.writerName = writerName;
        return this;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public Integer getEdition() {
        return edition;
    }

    public Books edition(Integer edition) {
        this.edition = edition;
        return this;
    }

    public void setEdition(Integer edition) {
        this.edition = edition;
    }

    public String getDescription() {
        return description;
    }

    public Books description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Store> getStores() {
        return stores;
    }

    public Books stores(Set<Store> stores) {
        this.stores = stores;
        return this;
    }

    public Books addStore(Store store) {
        this.stores.add(store);
        store.getBooks().add(this);
        return this;
    }

    public Books removeStore(Store store) {
        this.stores.remove(store);
        store.getBooks().remove(this);
        return this;
    }

    public void setStores(Set<Store> stores) {
        this.stores = stores;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Books)) {
            return false;
        }
        return id != null && id.equals(((Books) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Books{" +
            "id=" + getId() +
            ", bookName='" + getBookName() + "'" +
            ", genre='" + getGenre() + "'" +
            ", price=" + getPrice() +
            ", date='" + getDate() + "'" +
            ", writerName='" + getWriterName() + "'" +
            ", edition=" + getEdition() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
