import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificarTitular } from './modificar-titular';

describe('ModificarTitular', () => {
  let component: ModificarTitular;
  let fixture: ComponentFixture<ModificarTitular>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModificarTitular],
    }).compileComponents();

    fixture = TestBed.createComponent(ModificarTitular);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
