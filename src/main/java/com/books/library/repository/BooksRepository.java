package com.books.library.repository;

import com.books.library.domain.Books;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Books entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BooksRepository extends MongoRepository<Books, String> {
}
