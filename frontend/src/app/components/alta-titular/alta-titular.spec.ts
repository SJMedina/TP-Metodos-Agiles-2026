import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltaTitular } from './alta-titular';

describe('AltaTitular', () => {
  let component: AltaTitular;
  let fixture: ComponentFixture<AltaTitular>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AltaTitular],
    }).compileComponents();

    fixture = TestBed.createComponent(AltaTitular);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
