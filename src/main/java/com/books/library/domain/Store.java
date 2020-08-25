package com.books.library.domain;

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
 * A Store.
 */
@Document(collection = "store")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("issued_date")
    private ZonedDateTime issuedDate;

    @Field("return_date")
    private ZonedDateTime returnDate;

    @Field("period")
    private Integer period;

    @Field("rent")
    private Integer rent;

    @Field("fine")
    private Integer fine;

    @Field("issued_by")
    private String issuedBy;

    @Field("issued_to")
    private String issuedTo;

    @Field("is_member")
    private Boolean isMember;

    @DBRef
    @Field("books")
    private Set<Books> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getIssuedDate() {
        return issuedDate;
    }

    public Store issuedDate(ZonedDateTime issuedDate) {
        this.issuedDate = issuedDate;
        return this;
    }

    public void setIssuedDate(ZonedDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public ZonedDateTime getReturnDate() {
        return returnDate;
    }

    public Store returnDate(ZonedDateTime returnDate) {
        this.returnDate = returnDate;
        return this;
    }

    public void setReturnDate(ZonedDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getPeriod() {
        return period;
    }

    public Store period(Integer period) {
        this.period = period;
        return this;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getRent() {
        return rent;
    }

    public Store rent(Integer rent) {
        this.rent = rent;
        return this;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public Integer getFine() {
        return fine;
    }

    public Store fine(Integer fine) {
        this.fine = fine;
        return this;
    }

    public void setFine(Integer fine) {
        this.fine = fine;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public Store issuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
        return this;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getIssuedTo() {
        return issuedTo;
    }

    public Store issuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
        return this;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public Boolean isIsMember() {
        return isMember;
    }

    public Store isMember(Boolean isMember) {
        this.isMember = isMember;
        return this;
    }

    public void setIsMember(Boolean isMember) {
        this.isMember = isMember;
    }

    public Set<Books> getBooks() {
        return books;
    }

    public Store books(Set<Books> books) {
        this.books = books;
        return this;
    }

    public Store addBooks(Books books) {
        this.books.add(books);
        books.getStores().add(this);
        return this;
    }

    public Store removeBooks(Books books) {
        this.books.remove(books);
        books.getStores().remove(this);
        return this;
    }

    public void setBooks(Set<Books> books) {
        this.books = books;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Store)) {
            return false;
        }
        return id != null && id.equals(((Store) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Store{" +
            "id=" + getId() +
            ", issuedDate='" + getIssuedDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", period=" + getPeriod() +
            ", rent=" + getRent() +
            ", fine=" + getFine() +
            ", issuedBy='" + getIssuedBy() + "'" +
            ", issuedTo='" + getIssuedTo() + "'" +
            ", isMember='" + isIsMember() + "'" +
            "}";
    }
}
