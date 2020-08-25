package com.books.library.repository.search;

import com.books.library.domain.Store;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Store} entity.
 */
public interface StoreSearchRepository extends ElasticsearchRepository<Store, String> {
}
