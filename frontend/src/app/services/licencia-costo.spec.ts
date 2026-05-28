import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { LicenciaCostoService } from './licencia-costo';

describe('LicenciaCostoService', () => {
  let service: LicenciaCostoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LicenciaCostoService,
        provideHttpClient(),
        provideHttpClientTesting() // Herramienta vital para simular respuestas del backend
      ]
    });
    service = TestBed.inject(LicenciaCostoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Verificamos que no queden peticiones pendientes al finalizar cada test
    httpMock.verify(); 
  });

  it('debería calcular el costo armando los parámetros HTTP correctamente', () => {
    const dummyCosto = 48; // Respuesta simulada

    // 1. Llamamos al método
    service.obtenerCosto('B', 5).subscribe(costo => {
      expect(costo).toBe(dummyCosto);
    });

    // 2. Interceptamos la petición saliente y verificamos la URL y parámetros
    const req = httpMock.expectOne('http://localhost:8080/api/licencias/costo?clase=B&vigencia=5');
    
    // 3. Verificamos que sea un método GET
    expect(req.request.method).toBe('GET');
    
    // 4. Simulamos la respuesta exitosa del backend
    req.flush(dummyCosto);
  });
});