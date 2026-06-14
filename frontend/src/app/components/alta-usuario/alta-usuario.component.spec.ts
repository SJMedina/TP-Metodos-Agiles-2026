import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { AltaUsuarioComponent } from './alta-usuario.component';
import { UsuarioAdministrativoService } from '../../services/usuario-administrativo.service';
import { UsuarioAdministrativo } from '../../models/usuario-administrativo';

describe('AltaUsuarioComponent', () => {
  let component: AltaUsuarioComponent;
  let fixture: ComponentFixture<AltaUsuarioComponent>;
  let usuarioServiceSpy: { crear: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    usuarioServiceSpy = { crear: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [AltaUsuarioComponent],
      providers: [
        { provide: UsuarioAdministrativoService, useValue: usuarioServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AltaUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('formulario vacío debería mostrar error sin llamar al servicio', () => {
    component.id = '';
    component.nombre = '';
    component.password = '';

    component.darDeAlta();

    expect(component.mensaje).toBe('Todos los campos son obligatorios');
    expect(component.esError).toBe(true);
    expect(usuarioServiceSpy.crear).not.toHaveBeenCalled();
  });

  it('nombre vacío debería mostrar error aunque ID y password estén completos', () => {
    component.id = 'emp01';
    component.nombre = '';
    component.password = 'pass123';

    component.darDeAlta();

    expect(component.mensaje).toBe('Todos los campos son obligatorios');
    expect(usuarioServiceSpy.crear).not.toHaveBeenCalled();
  });

  it('alta exitosa debería limpiar el formulario y mostrar mensaje de éxito', () => {
    const mockUsuario: UsuarioAdministrativo = { id: 'emp01', nombre: 'Juan Perez', passwordHash: '$2a$HASH' };
    usuarioServiceSpy.crear.mockReturnValue(of(mockUsuario));

    component.id = 'emp01';
    component.nombre = 'Juan Perez';
    component.password = 'pass123';
    component.darDeAlta();

    expect(component.mensaje).toBe('Usuario creado exitosamente');
    expect(component.esError).toBe(false);
    expect(component.id).toBe('');
    expect(component.nombre).toBe('');
    expect(component.password).toBe('');
    expect(component.cargando).toBe(false);
  });

  it('ID duplicado (400) debería mostrar el mensaje de error del servidor', () => {
    usuarioServiceSpy.crear.mockReturnValue(
      throwError(() => ({ status: 400, error: { error: 'Ya existe un usuario con el ID: emp01' } }))
    );

    component.id = 'emp01';
    component.nombre = 'Juan Perez';
    component.password = 'pass123';
    component.darDeAlta();

    expect(component.mensaje).toBe('Ya existe un usuario con el ID: emp01');
    expect(component.esError).toBe(true);
    expect(component.cargando).toBe(false);
  });

  it('error de conexión (status 0) debería mostrar mensaje apropiado', () => {
    usuarioServiceSpy.crear.mockReturnValue(throwError(() => ({ status: 0 })));

    component.id = 'emp01';
    component.nombre = 'Juan Perez';
    component.password = 'pass123';
    component.darDeAlta();

    expect(component.mensaje).toBe('Error de conexión con el servidor');
    expect(component.esError).toBe(true);
  });

  it('estado cargando debería ser true durante la petición y false al finalizar', () => {
    const mockUsuario: UsuarioAdministrativo = { id: 'emp01', nombre: 'Juan Perez', passwordHash: '$2a$HASH' };
    usuarioServiceSpy.crear.mockReturnValue(of(mockUsuario));

    component.id = 'emp01';
    component.nombre = 'Juan Perez';
    component.password = 'pass123';
    component.darDeAlta();

    expect(component.cargando).toBe(false);
  });
});
