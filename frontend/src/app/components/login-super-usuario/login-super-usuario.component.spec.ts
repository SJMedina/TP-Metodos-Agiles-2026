import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginSuperUsuarioComponent } from './login-super-usuario.component';
import { SuperUsuarioAuthService } from '../../services/super-usuario-auth.service';

describe('LoginSuperUsuarioComponent', () => {
  let component: LoginSuperUsuarioComponent;
  let fixture: ComponentFixture<LoginSuperUsuarioComponent>;
  let authServiceSpy: jasmine.SpyObj<SuperUsuarioAuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('SuperUsuarioAuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginSuperUsuarioComponent],
      providers: [
        { provide: SuperUsuarioAuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginSuperUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('formulario vacío debería mostrar mensaje de error sin llamar al servicio', () => {
    component.id = '';
    component.password = '';

    component.iniciarSesion();

    expect(component.mensaje).toBe('Complete ID y contraseña');
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('solo ID completado debería mostrar mensaje de error', () => {
    component.id = 'superadmin';
    component.password = '';

    component.iniciarSesion();

    expect(component.mensaje).toBe('Complete ID y contraseña');
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('login exitoso debería navegar a /alta-usuario', () => {
    authServiceSpy.login.and.returnValue(
      of({ success: true, id: 'superadmin', token: 'super:123:superadmin' })
    );
    const routerSpy = spyOn((component as any).router, 'navigate');

    component.id = 'superadmin';
    component.password = 'super1234';
    component.iniciarSesion();

    expect(routerSpy).toHaveBeenCalledWith(['/alta-usuario']);
    expect(component.cargando).toBeFalse();
  });

  it('login con credenciales incorrectas (401) debería mostrar mensaje de error', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ status: 401 })));

    component.id = 'superadmin';
    component.password = 'wrong';
    component.iniciarSesion();

    expect(component.mensaje).toBe('ID o contraseña incorrectos');
    expect(component.cargando).toBeFalse();
  });

  it('error de conexión (status 0) debería mostrar mensaje apropiado', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ status: 0 })));

    component.id = 'superadmin';
    component.password = 'super1234';
    component.iniciarSesion();

    expect(component.mensaje).toBe('Error de conexión con el servidor');
  });
});
