import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CalculadorCostoComponent } from '../calculador-costo/calculador-costo';
import { LicenciaService } from '../../services/licencia.service';
import { Licencia } from '../../models/licencia';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-emitir-licencias',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CalculadorCostoComponent],
  templateUrl: './emitir-licencias.component.html',
  styleUrls: ['./emitir-licencias.component.css']
})
export class EmitirLicenciasComponent implements OnInit {
  protected nuevaLicencia: Licencia = {
    titular: '',
    edad: 0,
    numeroDocumento: '',
    clase: '',
    fechaNacimiento: '',
    observaciones: '',
    fechaEmision: '',
    costo: 0,
    vigencia: 1,
    poseeLicenciaB: false,
    antiguedadLicenciaBEnAnios: 0,
    tieneLicenciaProfesionalAnterior: false,
    grupoSanguineo: '',
    factorRH: '',
    donanteOrganos: false
  };

  // Nombre y apellido por separado; se combinan en "Apellido, Nombre" al emitir.
  protected nombre = '';
  protected apellido = '';

  protected readonly gruposSanguineos = ['A', 'B', 'AB', 'O'];
  protected readonly factoresRH = ['POSITIVO', 'NEGATIVO'];

  protected mensaje: string | null = null;
  protected errorValidacion: string | null = null;
  protected costoCalculado: number | null = null;
  protected currentUser = '';

  // Vigencia determinada automáticamente según la edad (regla de negocio).
  protected vigenciaAutomatica: number | null = null;
  protected fechaVencimientoCalculada: string | null = null;

  private licenciaService = inject(LicenciaService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.currentUser = user?.username || '';
  }

  protected emitirLicencia(): void {
    this.errorValidacion = null;

    if (!this.validarFormulario()) {
      return;
    }

    if (this.costoCalculado === null) {
      this.errorValidacion = 'Calcule el costo antes de emitir la licencia.';
      return;
    }

    const edad = this.calcularEdad(this.nuevaLicencia.fechaNacimiento);
    if (Number.isNaN(edad)) {
      this.errorValidacion = 'La fecha de nacimiento no es válida.';
      return;
    }

    const payload = {
      titular: `${this.apellido.trim()}, ${this.nombre.trim()}`,
      edad,
      numeroDocumento: this.nuevaLicencia.numeroDocumento,
      fechaNacimiento: this.nuevaLicencia.fechaNacimiento,
      clase: this.nuevaLicencia.clase,
      observaciones: this.nuevaLicencia.observaciones,
      poseeLicenciaB: this.nuevaLicencia.poseeLicenciaB,
      antiguedadLicenciaBEnAnios: this.nuevaLicencia.antiguedadLicenciaBEnAnios,
      vigencia: this.nuevaLicencia.vigencia,
      costo: this.costoCalculado,
      grupoSanguineo: this.nuevaLicencia.grupoSanguineo || undefined,
      factorRH: this.nuevaLicencia.factorRH || undefined,
      donanteOrganos: this.nuevaLicencia.donanteOrganos ?? false
    };

    this.licenciaService.emitirLicencia(payload).subscribe({
      next: () => {
        this.mensaje = 'Licencia emitida correctamente.';
        this.resetFormulario();
        this.router.navigate(['/listar-licencias']);
      },
      error: (err: any) => {
        console.error('Error al emitir licencia:', err);
        this.mensaje = err.error?.error || 'No se pudo emitir la licencia. Intente nuevamente.';
      }
    });
  }


  protected handleCostoCalculado(event: { costo: number; vigencia: number }): void {
    this.costoCalculado = event.costo;
    this.nuevaLicencia.vigencia = event.vigencia;
    this.mensaje = `Costo calculado: $${event.costo}.`;
  }

  /**
   * Determina la vigencia (en años) según la edad del titular:
   * - Menores de 21: 1 año la primera vez, 3 años las siguientes
   * - Hasta 46: 5 años | Hasta 60: 4 años | Hasta 70: 3 años | Mayores de 70: 1 año
   */
  protected calcularVigenciaPorEdad(edad: number, esPrimeraVez: boolean): number {
    if (edad < 21) return esPrimeraVez ? 1 : 3;
    if (edad <= 46) return 5;
    if (edad <= 60) return 4;
    if (edad <= 70) return 3;
    return 1;
  }

  /**
   * Recalcula la vigencia automática a partir de la edad y de si el titular ya
   * posee licencias (consultando por documento). También calcula la fecha de
   * vencimiento (cumpleaños en el año emisión + vigencia).
   */
  protected actualizarVigencia(): void {
    const documento = this.nuevaLicencia.numeroDocumento?.trim();
    const fechaNacimiento = this.nuevaLicencia.fechaNacimiento;

    if (!fechaNacimiento) {
      this.vigenciaAutomatica = null;
      this.fechaVencimientoCalculada = null;
      this.cdr.detectChanges();
      return;
    }

    const edad = this.calcularEdad(fechaNacimiento);
    if (!edad || Number.isNaN(edad)) {
      this.vigenciaAutomatica = null;
      this.fechaVencimientoCalculada = null;
      this.cdr.detectChanges();
      return;
    }

    // Apenas hay fecha de nacimiento (edad) se calcula la vigencia. Sin documento se
    // asume primera vez; al ingresar el documento se refina (1 vs 3 años para <21).
    if (!documento) {
      this.aplicarVigencia(edad, fechaNacimiento, true);
      return;
    }

    this.licenciaService.listarPorDocumento(documento).subscribe({
      next: (licencias) => this.aplicarVigencia(edad, fechaNacimiento, (licencias?.length ?? 0) === 0),
      // Ante un error de consulta, asumimos primera vez para no bloquear el trámite.
      error: () => this.aplicarVigencia(edad, fechaNacimiento, true)
    });
  }

  private aplicarVigencia(edad: number, fechaNacimiento: string, esPrimeraVez: boolean): void {
    const vigencia = this.calcularVigenciaPorEdad(edad, esPrimeraVez);
    this.vigenciaAutomatica = vigencia;
    this.nuevaLicencia.vigencia = vigencia;

    // El vencimiento cuenta la vigencia completa desde hoy y cae en el cumpleaños:
    // primer cumpleaños en o posterior a (hoy + vigencia años).
    const hoy = new Date();
    const nacimiento = new Date(fechaNacimiento);
    const base = new Date(hoy);
    base.setFullYear(hoy.getFullYear() + vigencia);
    const venc = new Date(base.getFullYear(), nacimiento.getMonth(), nacimiento.getDate());
    if (venc < base) {
      venc.setFullYear(base.getFullYear() + 1);
    }
    this.fechaVencimientoCalculada = venc.toLocaleDateString('es-AR');
    this.cdr.detectChanges();
  }

  private validarFormulario(): boolean {
    const licencia = this.nuevaLicencia;

    if (!this.nombre.trim() || !this.apellido.trim() || !licencia.numeroDocumento?.trim() || !licencia.clase || !licencia.fechaNacimiento) {
      this.errorValidacion = 'Complete todos los campos obligatorios antes de emitir la licencia.';
      return false;
    }

    const edad = this.calcularEdad(licencia.fechaNacimiento);
    if (Number.isNaN(edad)) {
      this.errorValidacion = 'La fecha de nacimiento no es válida.';
      return false;
    }

    const clase = licencia.clase.toUpperCase();
    if (['C', 'D', 'E'].includes(clase)) {
      if (edad < 21) {
        this.errorValidacion = 'La edad mínima para la clase C, D o E es 21 años.';
        return false;
      }
      if (!licencia.poseeLicenciaB || (licencia.antiguedadLicenciaBEnAnios ?? 0) < 1) {
        this.errorValidacion = 'Para clases C, D y E debe poseer una licencia B con al menos 1 año de antigüedad.';
        return false;
      }
      if (edad > 65) {
        this.errorValidacion = 'No puede otorgarse una licencia profesional por primera vez a mayores de 65 años.';
        return false;
      }
    } else {
      if (edad < 17) {
        this.errorValidacion = 'La edad mínima para esta clase es 17 años.';
        return false;
      }
    }

    return true;
  }

  protected calcularEdad(fechaNacimiento?: string): number {
    if (!fechaNacimiento) {
      return 0;
    }

    const nacimiento = new Date(fechaNacimiento);
    if (isNaN(nacimiento.getTime())) {
      return 0;
    }

    const hoy = new Date();
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();
    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
      edad--;
    }
    return edad;
  }

  private resetFormulario(): void {
    this.nuevaLicencia = {
      titular: '',
      edad: 0,
      numeroDocumento: '',
      clase: '',
      fechaNacimiento: '',
      observaciones: '',
      fechaEmision: '',
      costo: 0,
      vigencia: 1,
      poseeLicenciaB: false,
      antiguedadLicenciaBEnAnios: 0,
      tieneLicenciaProfesionalAnterior: false,
      grupoSanguineo: '',
      factorRH: '',
      donanteOrganos: false
    };
    this.nombre = '';
    this.apellido = '';
    this.errorValidacion = null;
    this.costoCalculado = null;
  }
}
