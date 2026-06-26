import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideRouter } from '@angular/router';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('debería crearse la aplicación correctamente', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`debería tener el título 'TP-Metodos-Agiles-2026'`, () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    
    // Al castear 'app' como 'any', TypeScript nos deja acceder a propiedades protected o private
    expect((app as any).title()).toEqual('TP-Metodos-Agiles-2026');
  });
});
