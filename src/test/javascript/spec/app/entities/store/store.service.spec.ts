import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { StoreService } from 'app/entities/store/store.service';
import { IStore, Store } from 'app/shared/model/store.model';

describe('Service Tests', () => {
  describe('Store Service', () => {
    let injector: TestBed;
    let service: StoreService;
    let httpMock: HttpTestingController;
    let elemDefault: IStore;
    let expectedResult: IStore | IStore[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(StoreService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Store('ID', currentDate, currentDate, 0, 0, 0, 'AAAAAAA', 'AAAAAAA', false);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            issuedDate: currentDate.format(DATE_TIME_FORMAT),
            returnDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find('123').subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Store', () => {
        const returnedFromService = Object.assign(
          {
            id: 'ID',
            issuedDate: currentDate.format(DATE_TIME_FORMAT),
            returnDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issuedDate: currentDate,
            returnDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Store()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Store', () => {
        const returnedFromService = Object.assign(
          {
            issuedDate: currentDate.format(DATE_TIME_FORMAT),
            returnDate: currentDate.format(DATE_TIME_FORMAT),
            period: 1,
            rent: 1,
            fine: 1,
            issuedBy: 'BBBBBB',
            issuedTo: 'BBBBBB',
            isMember: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issuedDate: currentDate,
            returnDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Store', () => {
        const returnedFromService = Object.assign(
          {
            issuedDate: currentDate.format(DATE_TIME_FORMAT),
            returnDate: currentDate.format(DATE_TIME_FORMAT),
            period: 1,
            rent: 1,
            fine: 1,
            issuedBy: 'BBBBBB',
            issuedTo: 'BBBBBB',
            isMember: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issuedDate: currentDate,
            returnDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Store', () => {
        service.delete('123').subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
