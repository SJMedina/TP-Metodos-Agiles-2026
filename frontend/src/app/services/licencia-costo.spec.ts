import { TestBed } from '@angular/core/testing';

import { LicenciaCosto } from './licencia-costo';

describe('LicenciaCosto', () => {
  let service: LicenciaCosto;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LicenciaCosto);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
