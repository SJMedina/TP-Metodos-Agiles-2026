import { ComponentFixture, TestBed } from '@angular/core/testing';
// Acá estaba el error: importamos correctamente CalculadorCostoComponent
import { CalculadorCostoComponent } from './calculador-costo'; 

describe('CalculadorCostoComponent', () => {
  let component: CalculadorCostoComponent;
  let fixture: ComponentFixture<CalculadorCostoComponent>;

  beforeEach(async () => {
    // Como es un componente Standalone, lo ponemos en imports
    await TestBed.configureTestingModule({
      imports: [CalculadorCostoComponent] 
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CalculadorCostoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });
});