package com.books.library.repository.search;

import com.books.library.domain.Books;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Books} entity.
 */
public interface BooksSearchRepository extends ElasticsearchRepository<Books, String> {
}
