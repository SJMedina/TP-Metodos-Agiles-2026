import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModificarUsuarioComponent } from './modificar-usuario.component';
import { UsuarioAdministrativoService } from '../../services/usuario-administrativo.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('ModificarUsuarioComponent', () => {
  let component: ModificarUsuarioComponent;
  let fixture: ComponentFixture<ModificarUsuarioComponent>;
  // @ts-ignore
  let usuarioService: jasmine.SpyObj<UsuarioAdministrativoService>;
  // @ts-ignore
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // @ts-ignore
    const usuarioServiceSpy = jasmine.createSpyObj('UsuarioAdministrativoService', ['listar', 'actualizar']);
    // @ts-ignore
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ModificarUsuarioComponent],
      providers: [
        { provide: UsuarioAdministrativoService, useValue: usuarioServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    // @ts-ignore
    usuarioService = TestBed.inject(UsuarioAdministrativoService) as jasmine.SpyObj<UsuarioAdministrativoService>;
    // @ts-ignore
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(ModificarUsuarioComponent);
    component = fixture.componentInstance;
  });

  it('debería crear', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar usuarios en ngOnInit', () => {
    const usuarios = [
      { id: 'emp01', nombre: 'Juan', passwordHash: 'hash1' },
      { id: 'emp02', nombre: 'María', passwordHash: 'hash2' }
    ];
    usuarioService.listar.and.returnValue(of(usuarios));

    fixture.detectChanges();

    expect(usuarioService.listar).toHaveBeenCalled();
    expect(component.usuarios.length).toBe(2);
  });

  it('debería seleccionar usuario y cargar sus datos', () => {
    const usuario = { id: 'emp01', nombre: 'Juan', passwordHash: 'hash1' };
    component.seleccionarUsuario(usuario);

    expect(component.usuarioSeleccionado).toBe(usuario);
    expect(component.nombre).toBe('Juan');
  });

  it('debería actualizar usuario exitosamente', () => {
    const usuario = { id: 'emp01', nombre: 'Juan', passwordHash: 'hash1' };
    component.usuarioSeleccionado = usuario;
    component.nombre = 'Nuevo Nombre';
    component.password = 'newPass';

    usuarioService.actualizar.and.returnValue(of(usuario));

    component.actualizar();

    expect(usuarioService.actualizar).toHaveBeenCalledWith('emp01', { nombre: 'Nuevo Nombre', password: 'newPass' });
    expect(component.esError).toBe(false);
  });

  it('no debería actualizar si no hay usuario seleccionado', () => {
    component.usuarioSeleccionado = null;
    component.actualizar();

    expect(component.mensaje).toContain('Selecciona');
    expect(component.esError).toBe(true);
  });

  it('debería navegar al hacer click en volver', () => {
    component.volver();
    expect(router.navigate).toHaveBeenCalledWith(['/listar-licencias']);
  });
});

