import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ListadoExpiradasComponent } from './listado-expiradas.component';
import { LicenciaService } from '../../services/licencia.service';
import { Licencia } from '../../models/licencia';

const licenciasMock: Licencia[] = [
  {
    id: 1,
    titular: 'Ana García',
    numeroDocumento: '11111111',
    clase: 'B',
    fechaEmision: '2016-01-01T00:00:00',
    vigencia: 5
  }
];

describe('ListadoExpiradasComponent', () => {
  let component: ListadoExpiradasComponent;
  let fixture: ComponentFixture<ListadoExpiradasComponent>;
  let listarExpiradasSpy: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    listarExpiradasSpy = vi.fn().mockReturnValue(of(licenciasMock));

    await TestBed.configureTestingModule({
      imports: [ListadoExpiradasComponent],
      providers: [
        { provide: LicenciaService, useValue: { listarExpiradas: listarExpiradasSpy } },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListadoExpiradasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debería llamar a listarExpiradas sin parámetros en ngOnInit', () => {
    expect(listarExpiradasSpy).toHaveBeenCalledWith(undefined, undefined);
  });

  it('debería mostrar las licencias obtenidas del servicio', () => {
    expect((component as any).licencias.length).toBe(1);
    expect((component as any).licencias[0].titular).toBe('Ana García');
  });

  it('debería llamar a listarExpiradas con las fechas ingresadas al buscar', () => {
    (component as any).fechaDesde = '2020-01-01';
    (component as any).fechaHasta = '2023-12-31';

    component.buscar();

    expect(listarExpiradasSpy).toHaveBeenCalledWith('2020-01-01', '2023-12-31');
  });

  it('debería resetear los filtros y llamar al servicio sin parámetros al limpiar', () => {
    (component as any).fechaDesde = '2020-01-01';
    (component as any).fechaHasta = '2023-12-31';

    component.limpiar();

    expect((component as any).fechaDesde).toBe('');
    expect((component as any).fechaHasta).toBe('');
    expect(listarExpiradasSpy).toHaveBeenCalledWith(undefined, undefined);
  });

  it('debería manejar errores del servicio y asignar mensaje de error', () => {
    listarExpiradasSpy.mockReturnValue(throwError(() => new Error('Error de red')));

    component.buscar();
    fixture.detectChanges();

    expect((component as any).error).toBeTruthy();
    expect((component as any).licencias.length).toBe(0);
  });

  it('calcularFechaVencimiento debería retornar N/D si no hay vigencia', () => {
    const licenciaSinVigencia: Licencia = { titular: 'Test', numeroDocumento: '123', clase: 'B' };
    expect(component.calcularFechaVencimiento(licenciaSinVigencia)).toBe('N/D');
  });
});
