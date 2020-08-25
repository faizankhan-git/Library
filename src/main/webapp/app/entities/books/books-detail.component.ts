import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBooks } from 'app/shared/model/books.model';

@Component({
  selector: 'jhi-books-detail',
  templateUrl: './books-detail.component.html',
})
export class BooksDetailComponent implements OnInit {
  books: IBooks | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ books }) => (this.books = books));
  }

  previousState(): void {
    window.history.back();
  }
}
