import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalculadorCosto } from './calculador-costo';

describe('CalculadorCosto', () => {
  let component: CalculadorCosto;
  let fixture: ComponentFixture<CalculadorCosto>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculadorCosto],
    }).compileComponents();

    fixture = TestBed.createComponent(CalculadorCosto);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
