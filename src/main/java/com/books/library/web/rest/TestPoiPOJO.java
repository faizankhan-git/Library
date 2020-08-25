package com.books.library.web.rest;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.List;

public class TestPoiPOJO {

	public static void main(String[] args) throws Exception {

		Workbook workbook = WorkbookFactory.create(new FileInputStream("F:\\Study\\Jhipster\\ExcelBooks.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);
		List<Books> books = PoiPOJOUtils.sheetToPOJO(sheet, Books.class);
		System.out.println(books);

		Books book = new Books();
		book.bookName = "Godan";
		book.genre = "Novel";
		book.price = 600;
		//book.date =  ZonedDateTime.now();
		book.writerName = "Prem Chandra";
		book.edition = 2;
		book.description = "great novel by PC";

		books.add(book);

		sheet = workbook.createSheet();
		PoiPOJOUtils.pojoToSheet(sheet, books);

		FileOutputStream out = new FileOutputStream("F:\\Study\\Jhipster\\ExcelBooksNew.xlsx");
		workbook.write(out);
		out.close();
		workbook.close();
	}
}
