import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'books',
        loadChildren: () => import('./books/books.module').then(m => m.LibraryBooksModule),
      },
      {
        path: 'store',
        loadChildren: () => import('./store/store.module').then(m => m.LibraryStoreModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class LibraryEntityModule {}
