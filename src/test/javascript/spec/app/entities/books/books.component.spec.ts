import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { LibraryTestModule } from '../../../test.module';
import { BooksComponent } from 'app/entities/books/books.component';
import { BooksService } from 'app/entities/books/books.service';
import { Books } from 'app/shared/model/books.model';

describe('Component Tests', () => {
  describe('Books Management Component', () => {
    let comp: BooksComponent;
    let fixture: ComponentFixture<BooksComponent>;
    let service: BooksService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [LibraryTestModule],
        declarations: [BooksComponent],
      })
        .overrideTemplate(BooksComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BooksComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BooksService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Books('123')],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.books && comp.books[0]).toEqual(jasmine.objectContaining({ id: '123' }));
    });
  });
});
