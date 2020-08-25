import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IBooks, Books } from 'app/shared/model/books.model';
import { BooksService } from './books.service';

@Component({
  selector: 'jhi-books-update',
  templateUrl: './books-update.component.html',
})
export class BooksUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    bookName: [],
    genre: [],
    price: [],
    date: [],
    writerName: [],
    edition: [],
    description: [],
  });

  constructor(protected booksService: BooksService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ books }) => {
      if (!books.id) {
        const today = moment().startOf('day');
        books.date = today;
      }

      this.updateForm(books);
    });
  }

  updateForm(books: IBooks): void {
    this.editForm.patchValue({
      id: books.id,
      bookName: books.bookName,
      genre: books.genre,
      price: books.price,
      date: books.date ? books.date.format(DATE_TIME_FORMAT) : null,
      writerName: books.writerName,
      edition: books.edition,
      description: books.description,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const books = this.createFromForm();
    if (books.id !== undefined) {
      this.subscribeToSaveResponse(this.booksService.update(books));
    } else {
      this.subscribeToSaveResponse(this.booksService.create(books));
    }
  }

  private createFromForm(): IBooks {
    return {
      ...new Books(),
      id: this.editForm.get(['id'])!.value,
      bookName: this.editForm.get(['bookName'])!.value,
      genre: this.editForm.get(['genre'])!.value,
      price: this.editForm.get(['price'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      writerName: this.editForm.get(['writerName'])!.value,
      edition: this.editForm.get(['edition'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooks>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
