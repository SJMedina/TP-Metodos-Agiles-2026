import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { EmitirLicenciasComponent } from './emitir-licencias.component';
import { LicenciaService } from '../../services/licencia.service';
import { AuthService } from '../../auth.service';

describe('EmitirLicenciasComponent', () => {
  let component: EmitirLicenciasComponent;
  let fixture: ComponentFixture<EmitirLicenciasComponent>;
  let listarPorDocumentoSpy: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    listarPorDocumentoSpy = vi.fn().mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [EmitirLicenciasComponent],
      providers: [
        {
          provide: LicenciaService,
          useValue: {
            listarPorDocumento: listarPorDocumentoSpy,
            emitirLicencia: vi.fn().mockReturnValue(of({}))
          }
        },
        { provide: AuthService, useValue: { getUser: () => ({ username: 'empleado' }) } },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmitirLicenciasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('calcularVigenciaPorEdad respeta la tabla por edad', () => {
    const c = component as any;
    expect(c.calcularVigenciaPorEdad(18, true)).toBe(1);   // menor 21, primera vez
    expect(c.calcularVigenciaPorEdad(18, false)).toBe(3);  // menor 21, no primera vez
    expect(c.calcularVigenciaPorEdad(30, true)).toBe(5);   // hasta 46
    expect(c.calcularVigenciaPorEdad(46, true)).toBe(5);
    expect(c.calcularVigenciaPorEdad(50, true)).toBe(4);   // hasta 60
    expect(c.calcularVigenciaPorEdad(65, true)).toBe(3);   // hasta 70
    expect(c.calcularVigenciaPorEdad(75, true)).toBe(1);   // mayor 70
  });

  it('actualizarVigencia consulta por documento y, sin licencias previas + menor de 21, fija vigencia 1', () => {
    const c = component as any;
    const nacimiento = new Date();
    nacimiento.setFullYear(nacimiento.getFullYear() - 18);
    c.nuevaLicencia.numeroDocumento = '12345678';
    c.nuevaLicencia.fechaNacimiento = nacimiento.toISOString().slice(0, 10);

    component['actualizarVigencia']();

    expect(listarPorDocumentoSpy).toHaveBeenCalledWith('12345678');
    expect(c.vigenciaAutomatica).toBe(1);
    expect(c.fechaVencimientoCalculada).toBeTruthy();
  });

  it('actualizarVigencia no calcula nada si falta el documento', () => {
    const c = component as any;
    c.nuevaLicencia.numeroDocumento = '';
    c.nuevaLicencia.fechaNacimiento = '1990-03-15';

    component['actualizarVigencia']();

    expect(c.vigenciaAutomatica).toBeNull();
    expect(listarPorDocumentoSpy).not.toHaveBeenCalled();
  });
});
