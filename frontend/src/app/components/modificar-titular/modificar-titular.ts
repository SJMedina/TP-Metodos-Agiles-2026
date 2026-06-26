import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TitularService } from '../services/alta-titular';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';
import { CalculadorCostoComponent } from '../calculador-costo/calculador-costo';

@Component({
  selector: 'app-modificar-titular',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CalculadorCostoComponent],
  templateUrl: './modificar-titular.html',
  styleUrls: ['../alta-titular/alta-titular.css']
})
export class ModificarTitularComponent implements OnInit {
  titularForm!: FormGroup;
  titularId!: number;

  // Datos de la licencia vigente del titular (para renovación inline con costo)
  licenciaId: number | null = null;
  claseLicencia = '';
  vigenciaAutomatica: number | null = null;
  costoRenovacion: number | null = null;
  mensajeRenovacion: string | null = null;

  constructor(
    private fb: FormBuilder,
    private titularService: TitularService,
    private licenciaService: LicenciaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.titularForm = this.fb.group({
      // campos bloqueados
      tipoDocumento: [{ value: '', disabled: true }, Validators.required],
      numeroDocumento: [{ value: '', disabled: true }, Validators.required],
      fechaNacimiento: [{ value: '', disabled: true }, Validators.required],
      claseSolicitada: [{ value: '', disabled: true }, Validators.required],

      // campos editables
      apellido: ['', Validators.required],
      nombre: ['', Validators.required],
      grupoSanguineo: ['', Validators.required],
      factorRH: ['', Validators.required],
      donanteOrganos: [false, Validators.required],

      direccion: this.fb.group({
        calle: ['', Validators.required],
        nro: ['', [Validators.required, Validators.min(1)]],
        codigoPostal: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
        localidad: ['', Validators.required],
        provincia: ['', Validators.required],
        piso: [''],
        depto: ['']
      })
    });

    this.titularId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.titularId) {
      this.cargarDatosTitular();
    }
  }

  cargarDatosTitular(): void {
    this.titularService.buscarPorId(this.titularId).subscribe({
      next: (titular) => {
        this.titularForm.patchValue(titular);
        this.buscarLicenciaVigente(titular.numeroDocumento, titular.claseSolicitada, titular.fechaNacimiento);
      },
      error: () => alert('Error al cargar los datos del titular')
    });
  }

  /** Busca la licencia vigente del titular para habilitar la renovación con costo. */
  private buscarLicenciaVigente(documento: string, clase: string, fechaNacimiento: string): void {
    this.licenciaService.obtenerPorDocumentoYClase(documento, clase).subscribe({
      next: (licencia) => {
        if (licencia && licencia.id && licencia.vigente) {
          this.licenciaId = licencia.id;
          this.claseLicencia = licencia.clase;
          const edad = this.calcularEdad(fechaNacimiento);
          this.vigenciaAutomatica = this.calcularVigenciaPorEdad(edad, false);
        } else {
          this.licenciaId = null;
        }
      },
      error: () => { this.licenciaId = null; }
    });
  }

  protected calcularEdad(fechaNacimiento?: string): number {
    if (!fechaNacimiento) return 0;
    const nacimiento = new Date(fechaNacimiento);
    if (isNaN(nacimiento.getTime())) return 0;
    const hoy = new Date();
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const m = hoy.getMonth() - nacimiento.getMonth();
    if (m < 0 || (m === 0 && hoy.getDate() < nacimiento.getDate())) edad--;
    return edad;
  }

  /** Misma tabla que en emisión; al renovar nunca es "primera vez". */
  protected calcularVigenciaPorEdad(edad: number, esPrimeraVez: boolean): number {
    if (edad < 21) return esPrimeraVez ? 1 : 3;
    if (edad <= 46) return 5;
    if (edad <= 60) return 4;
    if (edad <= 70) return 3;
    return 1;
  }

  protected handleCostoCalculado(event: { costo: number; vigencia: number }): void {
    this.costoRenovacion = event.costo;
  }

  /** Guarda los cambios del titular. Si renovar=true, además renueva la licencia con los datos actualizados. */
  onSubmit(renovar: boolean = false): void {
    if (!this.titularForm.valid) {
      this.titularForm.markAllAsTouched();
      return;
    }

    const datosActualizados = this.titularForm.getRawValue();

    this.titularService.modificarTitular(this.titularId, datosActualizados).subscribe({
      next: () => {
        if (renovar && this.licenciaId) {
          this.renovarLicencia(datosActualizados);
        } else {
          alert('Datos actualizados correctamente');
          this.router.navigate(['/listar-licencias']);
        }
      },
      error: (err) => alert('Error: ' + (err.error?.error || err.error || 'Hubo un error en el servidor.'))
    });
  }

  private renovarLicencia(datos: any): void {
    const payload = {
      id: this.licenciaId,
      titular: `${(datos.apellido || '').trim()}, ${(datos.nombre || '').trim()}`,
      grupoSanguineo: datos.grupoSanguineo,
      factorRH: datos.factorRH,
      donanteOrganos: datos.donanteOrganos
    };

    this.licenciaService.renovarDatos(payload).subscribe({
      next: (lic) => {
        this.mensajeRenovacion = `Licencia renovada con los datos actualizados. Costo: $${lic.costo}. Vence: ${lic.fechaVencimiento}.`;
        alert('Datos actualizados y licencia renovada correctamente.');
        this.router.navigate(['/licencias-vigentes']);
      },
      error: (err) => alert('Titular actualizado, pero no se pudo renovar la licencia: ' +
        (err.error?.error || err.error || 'error del servidor'))
    });
  }
}
