import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Search } from 'app/shared/util/request-util';
import { IBooks } from 'app/shared/model/books.model';

type EntityResponseType = HttpResponse<IBooks>;
type EntityArrayResponseType = HttpResponse<IBooks[]>;

@Injectable({ providedIn: 'root' })
export class BooksService {
  public resourceUrl = SERVER_API_URL + 'api/books';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/books';
  public fileUploadUrl = SERVER_API_URL + 'api/books/uploadExcelFile';

  constructor(protected http: HttpClient) {}

  create(books: IBooks): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(books);
    return this.http
      .post<IBooks>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(books: IBooks): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(books);
    return this.http
      .put<IBooks>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IBooks>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBooks[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBooks[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(books: IBooks): IBooks {
    const copy: IBooks = Object.assign({}, books, {
      date: books.date && books.date.isValid() ? books.date.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((books: IBooks) => {
        books.date = books.date ? moment(books.date) : undefined;
      });
    }
    return res;
  }
}
