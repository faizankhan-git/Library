package com.books.library.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link StoreSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class StoreSearchRepositoryMockConfiguration {

    @MockBean
    private StoreSearchRepository mockStoreSearchRepository;

}
