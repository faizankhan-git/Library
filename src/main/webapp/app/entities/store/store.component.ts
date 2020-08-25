import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IStore } from 'app/shared/model/store.model';
import { StoreService } from './store.service';
import { StoreDeleteDialogComponent } from './store-delete-dialog.component';

@Component({
  selector: 'jhi-store',
  templateUrl: './store.component.html',
})
export class StoreComponent implements OnInit, OnDestroy {
  stores?: IStore[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected storeService: StoreService,
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
      this.storeService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IStore[]>) => (this.stores = res.body || []));
      return;
    }

    this.storeService.query().subscribe((res: HttpResponse<IStore[]>) => (this.stores = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInStores();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IStore): string {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInStores(): void {
    this.eventSubscriber = this.eventManager.subscribe('storeListModification', () => this.loadAll());
  }

  delete(store: IStore): void {
    const modalRef = this.modalService.open(StoreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.store = store;
  }
}
