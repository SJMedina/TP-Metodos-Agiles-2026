import { TestBed } from '@angular/core/testing';

import { AltaTitular } from './alta-titular';

describe('AltaTitular', () => {
  let service: AltaTitular;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AltaTitular);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
