package com.books.library.web.rest;

import java.time.ZonedDateTime;

public class Books {

	@ExcelColumn(name = "Book Name")
	public String bookName;
	@ExcelColumn(name = "Genre")
	public String genre;

	@ExcelColumn(name = "Edition", numberFormat = "0")
	public Integer edition;

	@ExcelColumn(name = "Price")
	public Integer price;
	
//	@ExcelColumn(name = "Date")
//	public ZonedDateTime date;

	@ExcelColumn(name = "Writer Name")
	public String writerName;

	@ExcelColumn(name = "Description")
	public String description;

	public String getBookName() {
		return bookName;
	}


	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public Integer getEdition() {
		return edition;
	}


	public void setEdition(Integer edition) {
		this.edition = edition;
	}


	public Integer getPrice() {
		return price;
	}


	public void setPrice(Integer price) {
		this.price = price;
	}


	

	public String getWriterName() {
		return writerName;
	}


	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return "Books [bookName=" + bookName + ", genre=" + genre + ", edition=" + edition + ", price=" + price
				+ ", writerName=" + writerName + ", description=" + description + "]";
	}

	
}
