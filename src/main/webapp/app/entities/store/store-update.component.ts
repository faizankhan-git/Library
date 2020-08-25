import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IStore, Store } from 'app/shared/model/store.model';
import { StoreService } from './store.service';
import { IBooks } from 'app/shared/model/books.model';
import { BooksService } from 'app/entities/books/books.service';

@Component({
  selector: 'jhi-store-update',
  templateUrl: './store-update.component.html',
})
export class StoreUpdateComponent implements OnInit {
  isSaving = false;
  books: IBooks[] = [];

  editForm = this.fb.group({
    id: [],
    issuedDate: [],
    returnDate: [],
    period: [],
    rent: [],
    fine: [],
    issuedBy: [],
    issuedTo: [],
    isMember: [],
    books: [],
  });

  constructor(
    protected storeService: StoreService,
    protected booksService: BooksService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ store }) => {
      if (!store.id) {
        const today = moment().startOf('day');
        store.issuedDate = today;
        store.returnDate = today;
      }

      this.updateForm(store);

      this.booksService.query().subscribe((res: HttpResponse<IBooks[]>) => (this.books = res.body || []));
    });
  }

  updateForm(store: IStore): void {
    this.editForm.patchValue({
      id: store.id,
      issuedDate: store.issuedDate ? store.issuedDate.format(DATE_TIME_FORMAT) : null,
      returnDate: store.returnDate ? store.returnDate.format(DATE_TIME_FORMAT) : null,
      period: store.period,
      rent: store.rent,
      fine: store.fine,
      issuedBy: store.issuedBy,
      issuedTo: store.issuedTo,
      isMember: store.isMember,
      books: store.books,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const store = this.createFromForm();
    if (store.id !== undefined) {
      this.subscribeToSaveResponse(this.storeService.update(store));
    } else {
      this.subscribeToSaveResponse(this.storeService.create(store));
    }
  }

  private createFromForm(): IStore {
    return {
      ...new Store(),
      id: this.editForm.get(['id'])!.value,
      issuedDate: this.editForm.get(['issuedDate'])!.value ? moment(this.editForm.get(['issuedDate'])!.value, DATE_TIME_FORMAT) : undefined,
      returnDate: this.editForm.get(['returnDate'])!.value ? moment(this.editForm.get(['returnDate'])!.value, DATE_TIME_FORMAT) : undefined,
      period: this.editForm.get(['period'])!.value,
      rent: this.editForm.get(['rent'])!.value,
      fine: this.editForm.get(['fine'])!.value,
      issuedBy: this.editForm.get(['issuedBy'])!.value,
      issuedTo: this.editForm.get(['issuedTo'])!.value,
      isMember: this.editForm.get(['isMember'])!.value,
      books: this.editForm.get(['books'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStore>>): void {
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

  trackById(index: number, item: IBooks): any {
    return item.id;
  }

  getSelected(selectedVals: IBooks[], option: IBooks): IBooks {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
