package com.books.library.web.rest;

import com.books.library.LibraryApp;
import com.books.library.domain.Books;
import com.books.library.repository.BooksRepository;
import com.books.library.repository.search.BooksSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
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
 * Integration tests for the {@link BooksResource} REST controller.
 */
@SpringBootTest(classes = LibraryApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class BooksResourceIT {

    private static final String DEFAULT_BOOK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GENRE = "AAAAAAAAAA";
    private static final String UPDATED_GENRE = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_WRITER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_WRITER_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_EDITION = 1;
    private static final Integer UPDATED_EDITION = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private BooksRepository booksRepository;

    /**
     * This repository is mocked in the com.books.library.repository.search test package.
     *
     * @see com.books.library.repository.search.BooksSearchRepositoryMockConfiguration
     */
    @Autowired
    private BooksSearchRepository mockBooksSearchRepository;

    @Autowired
    private MockMvc restBooksMockMvc;

    private Books books;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createEntity() {
        Books books = new Books()
            .bookName(DEFAULT_BOOK_NAME)
            .genre(DEFAULT_GENRE)
            .price(DEFAULT_PRICE)
            .date(DEFAULT_DATE)
            .writerName(DEFAULT_WRITER_NAME)
            .edition(DEFAULT_EDITION)
            .description(DEFAULT_DESCRIPTION);
        return books;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createUpdatedEntity() {
        Books books = new Books()
            .bookName(UPDATED_BOOK_NAME)
            .genre(UPDATED_GENRE)
            .price(UPDATED_PRICE)
            .date(UPDATED_DATE)
            .writerName(UPDATED_WRITER_NAME)
            .edition(UPDATED_EDITION)
            .description(UPDATED_DESCRIPTION);
        return books;
    }

    @BeforeEach
    public void initTest() {
        booksRepository.deleteAll();
        books = createEntity();
    }

    @Test
    public void createBooks() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();
        // Create the Books
        restBooksMockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isCreated());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate + 1);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getBookName()).isEqualTo(DEFAULT_BOOK_NAME);
        assertThat(testBooks.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testBooks.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testBooks.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testBooks.getWriterName()).isEqualTo(DEFAULT_WRITER_NAME);
        assertThat(testBooks.getEdition()).isEqualTo(DEFAULT_EDITION);
        assertThat(testBooks.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    public void createBooksWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books with an existing ID
        books.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restBooksMockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }


    @Test
    public void getAllBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        // Get all the booksList
        restBooksMockMvc.perform(get("/api/books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId())))
            .andExpect(jsonPath("$.[*].bookName").value(hasItem(DEFAULT_BOOK_NAME)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].writerName").value(hasItem(DEFAULT_WRITER_NAME)))
            .andExpect(jsonPath("$.[*].edition").value(hasItem(DEFAULT_EDITION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    public void getBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(books.getId()))
            .andExpect(jsonPath("$.bookName").value(DEFAULT_BOOK_NAME))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.writerName").value(DEFAULT_WRITER_NAME))
            .andExpect(jsonPath("$.edition").value(DEFAULT_EDITION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }
    @Test
    public void getNonExistingBooks() throws Exception {
        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books
        Books updatedBooks = booksRepository.findById(books.getId()).get();
        updatedBooks
            .bookName(UPDATED_BOOK_NAME)
            .genre(UPDATED_GENRE)
            .price(UPDATED_PRICE)
            .date(UPDATED_DATE)
            .writerName(UPDATED_WRITER_NAME)
            .edition(UPDATED_EDITION)
            .description(UPDATED_DESCRIPTION);

        restBooksMockMvc.perform(put("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBooks)))
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getBookName()).isEqualTo(UPDATED_BOOK_NAME);
        assertThat(testBooks.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testBooks.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBooks.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBooks.getWriterName()).isEqualTo(UPDATED_WRITER_NAME);
        assertThat(testBooks.getEdition()).isEqualTo(UPDATED_EDITION);
        assertThat(testBooks.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    public void updateNonExistingBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBooksMockMvc.perform(put("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }

    @Test
    public void deleteBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        int databaseSizeBeforeDelete = booksRepository.findAll().size();

        // Delete the books
        restBooksMockMvc.perform(delete("/api/books/{id}", books.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).deleteById(books.getId());
    }

    @Test
    public void searchBooks() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        booksRepository.save(books);
        when(mockBooksSearchRepository.search(queryStringQuery("id:" + books.getId())))
            .thenReturn(Collections.singletonList(books));

        // Search the books
        restBooksMockMvc.perform(get("/api/_search/books?query=id:" + books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId())))
            .andExpect(jsonPath("$.[*].bookName").value(hasItem(DEFAULT_BOOK_NAME)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].writerName").value(hasItem(DEFAULT_WRITER_NAME)))
            .andExpect(jsonPath("$.[*].edition").value(hasItem(DEFAULT_EDITION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
