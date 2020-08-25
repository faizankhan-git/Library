import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IBooks, Books } from 'app/shared/model/books.model';
import { BooksService } from './books.service';
import { BooksComponent } from './books.component';
import { BooksDetailComponent } from './books-detail.component';
import { BooksUpdateComponent } from './books-update.component';

@Injectable({ providedIn: 'root' })
export class BooksResolve implements Resolve<IBooks> {
  constructor(private service: BooksService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBooks> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((books: HttpResponse<Books>) => {
          if (books.body) {
            return of(books.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Books());
  }
}

export const booksRoute: Routes = [
  {
    path: '',
    component: BooksComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Books',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BooksDetailComponent,
    resolve: {
      books: BooksResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Books',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BooksUpdateComponent,
    resolve: {
      books: BooksResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Books',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BooksUpdateComponent,
    resolve: {
      books: BooksResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Books',
    },
    canActivate: [UserRouteAccessService],
  },
];
