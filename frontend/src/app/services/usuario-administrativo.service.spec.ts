import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UsuarioAdministrativoService } from './usuario-administrativo.service';
import { UsuarioAdministrativo, CrearUsuarioRequest } from '../models/usuario-administrativo';

describe('UsuarioAdministrativoService', () => {
  let service: UsuarioAdministrativoService;
  let httpMock: HttpTestingController;

  const API_URL = 'http://localhost:8080/api/usuarios';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UsuarioAdministrativoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UsuarioAdministrativoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('crear() debería hacer POST con los datos correctos y devolver el usuario creado', () => {
    const request: CrearUsuarioRequest = { id: 'emp01', nombre: 'Juan Perez', password: 'pass123' };
    const mockRespuesta: UsuarioAdministrativo = { id: 'emp01', nombre: 'Juan Perez', passwordHash: '$2a$HASH' };

    service.crear(request).subscribe(usuario => {
      expect(usuario.id).toBe('emp01');
      expect(usuario.nombre).toBe('Juan Perez');
      expect(usuario.passwordHash).toBe('$2a$HASH');
    });

    const req = httpMock.expectOne(API_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockRespuesta);
  });

  it('listar() debería hacer GET y devolver la lista de usuarios', () => {
    const mockLista: UsuarioAdministrativo[] = [
      { id: 'emp01', nombre: 'Juan Perez', passwordHash: '$2a$HASH1' },
      { id: 'emp02', nombre: 'Maria Lopez', passwordHash: '$2a$HASH2' }
    ];

    service.listar().subscribe(lista => {
      expect(lista.length).toBe(2);
      expect(lista[0].id).toBe('emp01');
      expect(lista[1].id).toBe('emp02');
    });

    const req = httpMock.expectOne(API_URL);
    expect(req.request.method).toBe('GET');
    req.flush(mockLista);
  });

  it('listar() con lista vacía debería devolver array vacío', () => {
    service.listar().subscribe(lista => {
      expect(lista).toEqual([]);
    });

    const req = httpMock.expectOne(API_URL);
    req.flush([]);
  });
});
