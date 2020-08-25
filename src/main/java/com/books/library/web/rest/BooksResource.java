package com.books.library.web.rest;

import com.books.library.domain.Books;
import com.books.library.repository.BooksRepository;
import com.books.library.repository.search.BooksSearchRepository;
import com.books.library.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentileRanks.Iter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.books.library.domain.Books}.
 */
@RestController
@RequestMapping("/api")
public class BooksResource {

	private final Logger log = LoggerFactory.getLogger(BooksResource.class);

	private static final String ENTITY_NAME = "books";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private final BooksRepository booksRepository;

	private final BooksSearchRepository booksSearchRepository;

	public BooksResource(BooksRepository booksRepository, BooksSearchRepository booksSearchRepository) {
		this.booksRepository = booksRepository;
		this.booksSearchRepository = booksSearchRepository;
	}

	/**
	 * {@code POST  /books} : Create a new books.
	 *
	 * @param books the books to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new books, or with status {@code 400 (Bad Request)} if the
	 *         books has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/books")
	public ResponseEntity<Books> createBooks(@RequestBody Books books) throws URISyntaxException {
		log.debug("REST request to save Books : {}", books);
		if (books.getId() != null) {
			throw new BadRequestAlertException("A new books cannot already have an ID", ENTITY_NAME, "idexists");
		}
		Books result = booksRepository.save(books);
		booksSearchRepository.save(result);
		return ResponseEntity.created(new URI("/api/books/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
				.body(result);
	}

	/**
	 * {@code PUT  /books} : Updates an existing books.
	 *
	 * @param books the books to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated books, or with status {@code 400 (Bad Request)} if the
	 *         books is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the books couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/books")
	public ResponseEntity<Books> updateBooks(@RequestBody Books books) throws URISyntaxException {
		log.debug("REST request to update Books : {}", books);
		if (books.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		Books result = booksRepository.save(books);
		booksSearchRepository.save(result);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, books.getId()))
				.body(result);
	}

	/**
	 * {@code GET  /books} : get all the books.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of books in body.
	 */
	@GetMapping("/books")
	public List<Books> getAllBooks() {
		log.debug("REST request to get all Books");
		return booksRepository.findAll();
	}

	/**
	 * {@code GET  /books/:id} : get the "id" books.
	 *
	 * @param id the id of the books to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the books, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/books/{id}")
	public ResponseEntity<Books> getBooks(@PathVariable String id) {
		log.debug("REST request to get Books : {}", id);
		Optional<Books> books = booksRepository.findById(id);
		return ResponseUtil.wrapOrNotFound(books);
	}

	/**
	 * {@code DELETE  /books/:id} : delete the "id" books.
	 *
	 * @param id the id of the books to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/books/{id}")
	public ResponseEntity<Void> deleteBooks(@PathVariable String id) {
		log.debug("REST request to delete Books : {}", id);
		booksRepository.deleteById(id);
		booksSearchRepository.deleteById(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
	}

	/**
	 * {@code SEARCH  /_search/books?query=:query} : search for the books
	 * corresponding to the query.
	 *
	 * @param query the query of the books search.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/books")
	public List<Books> searchBooks(@RequestParam String query) {
		log.debug("REST request to search Books for query {}", query);
		return StreamSupport.stream(booksSearchRepository.search(queryStringQuery(query)).spliterator(), false)
				.collect(Collectors.toList());
	}

	@PostMapping("/books/uploadExcelFile")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
		InputStream in = file.getInputStream();
		File tempFile = stream2file(in);
		Workbook workbook = WorkbookFactory.create(tempFile);
		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		System.out.println("Retrieving Sheets using for-each loop");
		for (Sheet sheet : workbook) {
			System.out.println("=> " + sheet.getSheetName());
		}

		// Getting the Sheet at index zero
		Sheet sheet = workbook.getSheetAt(0);

		List<com.books.library.web.rest.Books> bookListFromSheet = PoiPOJOUtils.sheetToPOJO(sheet,
				com.books.library.web.rest.Books.class);
		for(com.books.library.web.rest.Books book : bookListFromSheet) {
			Books books = new Books();
			ZonedDateTime dateTime = ZonedDateTime.now();
			books.setBookName(book.getBookName());
			books.setGenre(book.getGenre());
			books.setEdition(book.getEdition());
			books.setPrice(book.getPrice());
			books.setDate(dateTime);
			books.setWriterName(book.getWriterName());
			books.setDescription(book.getDescription());
			booksRepository.save(books);
		}

		workbook.close();
		tempFile.delete();
		return ResponseEntity.ok()
				.body("File: " + file.getOriginalFilename() + " has been uploaded successfully!");

	}

	public static final String PREFIX = "stream2file";
	public static final String SUFFIX = ".tmp";

	public static File stream2file(InputStream in) throws IOException {
		final File tempFile = File.createTempFile(PREFIX, SUFFIX);
		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}
}
