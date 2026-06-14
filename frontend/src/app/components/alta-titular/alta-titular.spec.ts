import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { AltaTitularComponent } from './alta-titular';

describe('AltaTitularComponent', () => {
  let component: AltaTitularComponent;
  let fixture: ComponentFixture<AltaTitularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AltaTitularComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AltaTitularComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
