import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { LibrarySharedModule } from 'app/shared/shared.module';
import { BooksComponent } from './books.component';
import { BooksDetailComponent } from './books-detail.component';
import { BooksUpdateComponent } from './books-update.component';
import { BooksDeleteDialogComponent } from './books-delete-dialog.component';
import { booksRoute } from './books.route';

@NgModule({
  imports: [LibrarySharedModule, RouterModule.forChild(booksRoute)],
  declarations: [BooksComponent, BooksDetailComponent, BooksUpdateComponent, BooksDeleteDialogComponent],
  entryComponents: [BooksDeleteDialogComponent],
})
export class LibraryBooksModule {}
