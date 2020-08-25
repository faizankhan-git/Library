import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBooks } from 'app/shared/model/books.model';
import { BooksService } from './books.service';
import { BooksDeleteDialogComponent } from './books-delete-dialog.component';

@Component({
  selector: 'jhi-books',
  templateUrl: './books.component.html',
})
export class BooksComponent implements OnInit, OnDestroy {
  books?: IBooks[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected booksService: BooksService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.booksService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IBooks[]>) => (this.books = res.body || []));
      return;
    }

    this.booksService.query().subscribe((res: HttpResponse<IBooks[]>) => (this.books = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInBooks();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IBooks): string {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInBooks(): void {
    this.eventSubscriber = this.eventManager.subscribe('booksListModification', () => this.loadAll());
  }

  delete(books: IBooks): void {
    const modalRef = this.modalService.open(BooksDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.books = books;
  }
}
