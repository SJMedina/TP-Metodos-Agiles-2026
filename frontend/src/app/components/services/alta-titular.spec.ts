import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { TitularService } from './alta-titular';

describe('TitularService', () => {
  let service: TitularService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TitularService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
