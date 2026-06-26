import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ModificarTitularComponent } from './modificar-titular';

describe('ModificarTitularComponent', () => {
  let component: ModificarTitularComponent;
  let fixture: ComponentFixture<ModificarTitularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModificarTitularComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ModificarTitularComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
