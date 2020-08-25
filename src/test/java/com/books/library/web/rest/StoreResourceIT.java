package com.books.library.web.rest;

import com.books.library.LibraryApp;
import com.books.library.domain.Store;
import com.books.library.repository.StoreRepository;
import com.books.library.repository.search.StoreSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.books.library.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link StoreResource} REST controller.
 */
@SpringBootTest(classes = LibraryApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class StoreResourceIT {

    private static final ZonedDateTime DEFAULT_ISSUED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ISSUED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_RETURN_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RETURN_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_PERIOD = 1;
    private static final Integer UPDATED_PERIOD = 2;

    private static final Integer DEFAULT_RENT = 1;
    private static final Integer UPDATED_RENT = 2;

    private static final Integer DEFAULT_FINE = 1;
    private static final Integer UPDATED_FINE = 2;

    private static final String DEFAULT_ISSUED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ISSUED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_ISSUED_TO = "AAAAAAAAAA";
    private static final String UPDATED_ISSUED_TO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_MEMBER = false;
    private static final Boolean UPDATED_IS_MEMBER = true;

    @Autowired
    private StoreRepository storeRepository;

    @Mock
    private StoreRepository storeRepositoryMock;

    /**
     * This repository is mocked in the com.books.library.repository.search test package.
     *
     * @see com.books.library.repository.search.StoreSearchRepositoryMockConfiguration
     */
    @Autowired
    private StoreSearchRepository mockStoreSearchRepository;

    @Autowired
    private MockMvc restStoreMockMvc;

    private Store store;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createEntity() {
        Store store = new Store()
            .issuedDate(DEFAULT_ISSUED_DATE)
            .returnDate(DEFAULT_RETURN_DATE)
            .period(DEFAULT_PERIOD)
            .rent(DEFAULT_RENT)
            .fine(DEFAULT_FINE)
            .issuedBy(DEFAULT_ISSUED_BY)
            .issuedTo(DEFAULT_ISSUED_TO)
            .isMember(DEFAULT_IS_MEMBER);
        return store;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createUpdatedEntity() {
        Store store = new Store()
            .issuedDate(UPDATED_ISSUED_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .period(UPDATED_PERIOD)
            .rent(UPDATED_RENT)
            .fine(UPDATED_FINE)
            .issuedBy(UPDATED_ISSUED_BY)
            .issuedTo(UPDATED_ISSUED_TO)
            .isMember(UPDATED_IS_MEMBER);
        return store;
    }

    @BeforeEach
    public void initTest() {
        storeRepository.deleteAll();
        store = createEntity();
    }

    @Test
    public void createStore() throws Exception {
        int databaseSizeBeforeCreate = storeRepository.findAll().size();
        // Create the Store
        restStoreMockMvc.perform(post("/api/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(store)))
            .andExpect(status().isCreated());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate + 1);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getIssuedDate()).isEqualTo(DEFAULT_ISSUED_DATE);
        assertThat(testStore.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);
        assertThat(testStore.getPeriod()).isEqualTo(DEFAULT_PERIOD);
        assertThat(testStore.getRent()).isEqualTo(DEFAULT_RENT);
        assertThat(testStore.getFine()).isEqualTo(DEFAULT_FINE);
        assertThat(testStore.getIssuedBy()).isEqualTo(DEFAULT_ISSUED_BY);
        assertThat(testStore.getIssuedTo()).isEqualTo(DEFAULT_ISSUED_TO);
        assertThat(testStore.isIsMember()).isEqualTo(DEFAULT_IS_MEMBER);

        // Validate the Store in Elasticsearch
        verify(mockStoreSearchRepository, times(1)).save(testStore);
    }

    @Test
    public void createStoreWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = storeRepository.findAll().size();

        // Create the Store with an existing ID
        store.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoreMockMvc.perform(post("/api/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(store)))
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Store in Elasticsearch
        verify(mockStoreSearchRepository, times(0)).save(store);
    }


    @Test
    public void getAllStores() throws Exception {
        // Initialize the database
        storeRepository.save(store);

        // Get all the storeList
        restStoreMockMvc.perform(get("/api/stores?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(store.getId())))
            .andExpect(jsonPath("$.[*].issuedDate").value(hasItem(sameInstant(DEFAULT_ISSUED_DATE))))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(sameInstant(DEFAULT_RETURN_DATE))))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].rent").value(hasItem(DEFAULT_RENT)))
            .andExpect(jsonPath("$.[*].fine").value(hasItem(DEFAULT_FINE)))
            .andExpect(jsonPath("$.[*].issuedBy").value(hasItem(DEFAULT_ISSUED_BY)))
            .andExpect(jsonPath("$.[*].issuedTo").value(hasItem(DEFAULT_ISSUED_TO)))
            .andExpect(jsonPath("$.[*].isMember").value(hasItem(DEFAULT_IS_MEMBER.booleanValue())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllStoresWithEagerRelationshipsIsEnabled() throws Exception {
        when(storeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStoreMockMvc.perform(get("/api/stores?eagerload=true"))
            .andExpect(status().isOk());

        verify(storeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllStoresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(storeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStoreMockMvc.perform(get("/api/stores?eagerload=true"))
            .andExpect(status().isOk());

        verify(storeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    public void getStore() throws Exception {
        // Initialize the database
        storeRepository.save(store);

        // Get the store
        restStoreMockMvc.perform(get("/api/stores/{id}", store.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(store.getId()))
            .andExpect(jsonPath("$.issuedDate").value(sameInstant(DEFAULT_ISSUED_DATE)))
            .andExpect(jsonPath("$.returnDate").value(sameInstant(DEFAULT_RETURN_DATE)))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.rent").value(DEFAULT_RENT))
            .andExpect(jsonPath("$.fine").value(DEFAULT_FINE))
            .andExpect(jsonPath("$.issuedBy").value(DEFAULT_ISSUED_BY))
            .andExpect(jsonPath("$.issuedTo").value(DEFAULT_ISSUED_TO))
            .andExpect(jsonPath("$.isMember").value(DEFAULT_IS_MEMBER.booleanValue()));
    }
    @Test
    public void getNonExistingStore() throws Exception {
        // Get the store
        restStoreMockMvc.perform(get("/api/stores/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateStore() throws Exception {
        // Initialize the database
        storeRepository.save(store);

        int databaseSizeBeforeUpdate = storeRepository.findAll().size();

        // Update the store
        Store updatedStore = storeRepository.findById(store.getId()).get();
        updatedStore
            .issuedDate(UPDATED_ISSUED_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .period(UPDATED_PERIOD)
            .rent(UPDATED_RENT)
            .fine(UPDATED_FINE)
            .issuedBy(UPDATED_ISSUED_BY)
            .issuedTo(UPDATED_ISSUED_TO)
            .isMember(UPDATED_IS_MEMBER);

        restStoreMockMvc.perform(put("/api/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedStore)))
            .andExpect(status().isOk());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getIssuedDate()).isEqualTo(UPDATED_ISSUED_DATE);
        assertThat(testStore.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testStore.getPeriod()).isEqualTo(UPDATED_PERIOD);
        assertThat(testStore.getRent()).isEqualTo(UPDATED_RENT);
        assertThat(testStore.getFine()).isEqualTo(UPDATED_FINE);
        assertThat(testStore.getIssuedBy()).isEqualTo(UPDATED_ISSUED_BY);
        assertThat(testStore.getIssuedTo()).isEqualTo(UPDATED_ISSUED_TO);
        assertThat(testStore.isIsMember()).isEqualTo(UPDATED_IS_MEMBER);

        // Validate the Store in Elasticsearch
        verify(mockStoreSearchRepository, times(1)).save(testStore);
    }

    @Test
    public void updateNonExistingStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoreMockMvc.perform(put("/api/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(store)))
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Store in Elasticsearch
        verify(mockStoreSearchRepository, times(0)).save(store);
    }

    @Test
    public void deleteStore() throws Exception {
        // Initialize the database
        storeRepository.save(store);

        int databaseSizeBeforeDelete = storeRepository.findAll().size();

        // Delete the store
        restStoreMockMvc.perform(delete("/api/stores/{id}", store.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Store in Elasticsearch
        verify(mockStoreSearchRepository, times(1)).deleteById(store.getId());
    }

    @Test
    public void searchStore() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        storeRepository.save(store);
        when(mockStoreSearchRepository.search(queryStringQuery("id:" + store.getId())))
            .thenReturn(Collections.singletonList(store));

        // Search the store
        restStoreMockMvc.perform(get("/api/_search/stores?query=id:" + store.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(store.getId())))
            .andExpect(jsonPath("$.[*].issuedDate").value(hasItem(sameInstant(DEFAULT_ISSUED_DATE))))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(sameInstant(DEFAULT_RETURN_DATE))))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].rent").value(hasItem(DEFAULT_RENT)))
            .andExpect(jsonPath("$.[*].fine").value(hasItem(DEFAULT_FINE)))
            .andExpect(jsonPath("$.[*].issuedBy").value(hasItem(DEFAULT_ISSUED_BY)))
            .andExpect(jsonPath("$.[*].issuedTo").value(hasItem(DEFAULT_ISSUED_TO)))
            .andExpect(jsonPath("$.[*].isMember").value(hasItem(DEFAULT_IS_MEMBER.booleanValue())));
    }
}
