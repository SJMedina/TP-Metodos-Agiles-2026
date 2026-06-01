import { Component, inject, OnInit, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LicenciaService } from '../../../services/licencia.service';
import { AuthService } from '../../../auth.service';
import { Licencia } from '../../../models/licencia';

@Component({
  selector: 'app-renovar-modificacion',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './renovar-modificacion.component.html',
  styleUrls: ['./renovar-modificacion.component.css'],
  
})
export class RenovarModificacionComponent {
  protected documento: string = '';
  protected licencias: Licencia[] = [];
  protected licenciaEditable: Licencia | null = null;
  protected mensaje: string | null = null;
  protected errorValidacion: string | null = null;
  protected currentUser = '';
  protected buscado = false;

  private licenciaService = inject(LicenciaService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.currentUser = user?.username || '';
  }

  protected buscarPorDocumento(): void {
    this.errorValidacion = null;
    this.mensaje = null;
    this.buscado = false;

    if (!this.documento.trim()) {
      this.errorValidacion = 'Ingrese un número de documento.';
      this.cdr.detectChanges();
      return;
    }

    this.licenciaService.listarPorDocumento(this.documento.trim()).subscribe({
      next: (licencias) => {
        this.licencias = licencias;
        this.buscado = true;
        if (licencias.length === 0) {
          this.errorValidacion = 'No se encontraron licencias para ese documento.';
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorValidacion = 'Error al buscar licencias.';
        this.cdr.detectChanges(); 
      }
    });
}

  protected seleccionarLicencia(licencia: Licencia): void {
    // Copia para no mutar el original hasta confirmar
    this.licenciaEditable = { ...licencia };
    this.errorValidacion = null;
    this.mensaje = null;
  }

  protected calcularEdad(fechaNacimiento?: string): number {
    if (!fechaNacimiento) return 0;
    const nacimiento = new Date(fechaNacimiento);
    if (isNaN(nacimiento.getTime())) return 0;
    const hoy = new Date();
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();
    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) edad--;
    return edad;
  }

  protected guardar(): void {
    this.errorValidacion = null;

    if (!this.licenciaEditable) return;

    if (!this.licenciaEditable.titular?.trim()) {
      this.errorValidacion = 'El titular es obligatorio.';
      return;
    }

    const payload = {
      id: this.licenciaEditable.id,
      titular: this.licenciaEditable.titular,
      edad: this.calcularEdad(this.licenciaEditable.fechaNacimiento),
      fechaNacimiento: this.licenciaEditable.fechaNacimiento,
      observaciones: this.licenciaEditable.observaciones,
      vigencia: this.licenciaEditable.vigencia,
      renovarPorVencimiento: false
    };

    this.licenciaService.renovarLicencia(payload).subscribe({
      next: () => {
        this.mensaje = 'Licencia actualizada correctamente.';
        this.router.navigate(['/listar-licencias']);
      },
      error: (err: any) => {
        this.errorValidacion = err.error?.error || 'No se pudo actualizar la licencia.';
      }
    });
  }
}