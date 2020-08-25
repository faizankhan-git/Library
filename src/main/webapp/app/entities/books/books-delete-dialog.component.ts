import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBooks } from 'app/shared/model/books.model';
import { BooksService } from './books.service';

@Component({
  templateUrl: './books-delete-dialog.component.html',
})
export class BooksDeleteDialogComponent {
  books?: IBooks;

  constructor(protected booksService: BooksService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.booksService.delete(id).subscribe(() => {
      this.eventManager.broadcast('booksListModification');
      this.activeModal.close();
    });
  }
}
