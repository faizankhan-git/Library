package com.books.library.web.rest;

import com.books.library.domain.Store;
import com.books.library.repository.StoreRepository;
import com.books.library.repository.search.StoreSearchRepository;
import com.books.library.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.books.library.domain.Store}.
 */
@RestController
@RequestMapping("/api")
public class StoreResource {

    private final Logger log = LoggerFactory.getLogger(StoreResource.class);

    private static final String ENTITY_NAME = "store";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoreRepository storeRepository;

    private final StoreSearchRepository storeSearchRepository;

    public StoreResource(StoreRepository storeRepository, StoreSearchRepository storeSearchRepository) {
        this.storeRepository = storeRepository;
        this.storeSearchRepository = storeSearchRepository;
    }

    /**
     * {@code POST  /stores} : Create a new store.
     *
     * @param store the store to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new store, or with status {@code 400 (Bad Request)} if the store has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stores")
    public ResponseEntity<Store> createStore(@RequestBody Store store) throws URISyntaxException {
        log.debug("REST request to save Store : {}", store);
        if (store.getId() != null) {
            throw new BadRequestAlertException("A new store cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Store result = storeRepository.save(store);
        storeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/stores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /stores} : Updates an existing store.
     *
     * @param store the store to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated store,
     * or with status {@code 400 (Bad Request)} if the store is not valid,
     * or with status {@code 500 (Internal Server Error)} if the store couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stores")
    public ResponseEntity<Store> updateStore(@RequestBody Store store) throws URISyntaxException {
        log.debug("REST request to update Store : {}", store);
        if (store.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Store result = storeRepository.save(store);
        storeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, store.getId()))
            .body(result);
    }

    /**
     * {@code GET  /stores} : get all the stores.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stores in body.
     */
    @GetMapping("/stores")
    public List<Store> getAllStores(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Stores");
        return storeRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /stores/:id} : get the "id" store.
     *
     * @param id the id of the store to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the store, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stores/{id}")
    public ResponseEntity<Store> getStore(@PathVariable String id) {
        log.debug("REST request to get Store : {}", id);
        Optional<Store> store = storeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(store);
    }

    /**
     * {@code DELETE  /stores/:id} : delete the "id" store.
     *
     * @param id the id of the store to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stores/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable String id) {
        log.debug("REST request to delete Store : {}", id);
        storeRepository.deleteById(id);
        storeSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/stores?query=:query} : search for the store corresponding
     * to the query.
     *
     * @param query the query of the store search.
     * @return the result of the search.
     */
    @GetMapping("/_search/stores")
    public List<Store> searchStores(@RequestParam String query) {
        log.debug("REST request to search Stores for query {}", query);
        return StreamSupport
            .stream(storeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
