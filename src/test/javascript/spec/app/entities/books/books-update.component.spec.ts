import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { LibraryTestModule } from '../../../test.module';
import { BooksUpdateComponent } from 'app/entities/books/books-update.component';
import { BooksService } from 'app/entities/books/books.service';
import { Books } from 'app/shared/model/books.model';

describe('Component Tests', () => {
  describe('Books Management Update Component', () => {
    let comp: BooksUpdateComponent;
    let fixture: ComponentFixture<BooksUpdateComponent>;
    let service: BooksService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [LibraryTestModule],
        declarations: [BooksUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(BooksUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BooksUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BooksService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Books('123');
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Books();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
