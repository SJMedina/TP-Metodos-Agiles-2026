import { Component, inject, OnInit } from '@angular/core';
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

  protected readonly gruposSanguineos = ['A', 'B', 'AB', 'O'];
  protected readonly factoresRH = ['POSITIVO', 'NEGATIVO'];

  protected mensaje: string | null = null;
  protected errorValidacion: string | null = null;
  protected mostrandoCalculador = false;
  protected costoCalculado: number | null = null;
  protected currentUser = '';

  private licenciaService = inject(LicenciaService);
  private authService = inject(AuthService);
  private router = inject(Router);

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
      titular: this.nuevaLicencia.titular,
      edad,
      numeroDocumento: this.nuevaLicencia.numeroDocumento,
      fechaNacimiento: this.nuevaLicencia.fechaNacimiento,
      clase: this.nuevaLicencia.clase,
      observaciones: this.nuevaLicencia.observaciones,
      poseeLicenciaB: this.nuevaLicencia.poseeLicenciaB,
      antiguedadLicenciaBEnAnios: this.nuevaLicencia.antiguedadLicenciaBEnAnios,
      tieneLicenciaProfesionalAnterior: this.nuevaLicencia.tieneLicenciaProfesionalAnterior,
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

  protected toggleCalculador(): void {
    if (!this.nuevaLicencia.clase) {
      this.errorValidacion = 'Seleccione la clase antes de calcular el costo.';
      return;
    }
    this.mostrandoCalculador = !this.mostrandoCalculador;
  }

  protected cerrarCalculador(): void {
    this.mostrandoCalculador = false;
  }

  protected handleCostoCalculado(event: { costo: number; vigencia: number }): void {
    this.costoCalculado = event.costo;
    this.nuevaLicencia.vigencia = event.vigencia;
    this.mensaje = `Costo calculado: $${event.costo}.`;
  }

  private validarFormulario(): boolean {
    const licencia = this.nuevaLicencia;

    if (!licencia.titular?.trim() || !licencia.numeroDocumento?.trim() || !licencia.clase || !licencia.fechaNacimiento) {
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
      if (edad > 65 && !licencia.tieneLicenciaProfesionalAnterior) {
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
    this.errorValidacion = null;
    this.costoCalculado = null;
    this.mostrandoCalculador = false;
  }
}
