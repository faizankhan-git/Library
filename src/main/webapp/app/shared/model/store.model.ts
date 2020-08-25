import { Moment } from 'moment';
import { IBooks } from 'app/shared/model/books.model';

export interface IStore {
  id?: string;
  issuedDate?: Moment;
  returnDate?: Moment;
  period?: number;
  rent?: number;
  fine?: number;
  issuedBy?: string;
  issuedTo?: string;
  isMember?: boolean;
  books?: IBooks[];
}

export class Store implements IStore {
  constructor(
    public id?: string,
    public issuedDate?: Moment,
    public returnDate?: Moment,
    public period?: number,
    public rent?: number,
    public fine?: number,
    public issuedBy?: string,
    public issuedTo?: string,
    public isMember?: boolean,
    public books?: IBooks[]
  ) {
    this.isMember = this.isMember || false;
  }
}
