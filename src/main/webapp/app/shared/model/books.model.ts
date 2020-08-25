import { Moment } from 'moment';
import { IStore } from 'app/shared/model/store.model';

export interface IBooks {
  id?: string;
  bookName?: string;
  genre?: string;
  price?: number;
  date?: Moment;
  writerName?: string;
  edition?: number;
  description?: string;
  stores?: IStore[];
}

export class Books implements IBooks {
  constructor(
    public id?: string,
    public bookName?: string,
    public genre?: string,
    public price?: number,
    public date?: Moment,
    public writerName?: string,
    public edition?: number,
    public description?: string,
    public stores?: IStore[]
  ) {}
}
