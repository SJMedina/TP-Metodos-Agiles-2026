import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';
import { Licencia } from '../../models/licencia';

@Component({
  selector: 'app-lista-licencias-vigentes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lista-licencias-vigentes.html',
  styleUrl: './lista-licencias-vigentes.css',
})
export class ListaLicenciasVigentesComponent {
  protected nombreApellido = '';
  protected grupoSanguineo = '';
  protected factorRH = '';
  protected donanteOrganos: boolean | undefined = undefined;

  protected licencias: Licencia[] = [];
  protected buscado = false;
  protected errorMensaje = '';

  protected readonly gruposSanguineos = ['A', 'B', 'AB', 'O'];
  protected readonly factoresRH = ['POSITIVO', 'NEGATIVO'];

  private licenciaService = inject(LicenciaService);
  private cdr = inject(ChangeDetectorRef);

  protected buscar(): void {
    this.errorMensaje = '';
    this.buscado = false;

    const filtros: any = {};
    if (this.nombreApellido.trim()) filtros.nombreApellido = this.nombreApellido.trim();
    if (this.grupoSanguineo) filtros.grupoSanguineo = this.grupoSanguineo;
    if (this.factorRH) filtros.factorRH = this.factorRH;
    if (this.donanteOrganos !== undefined) filtros.donanteOrganos = this.donanteOrganos;

    this.licenciaService.listarVigentes(filtros).subscribe({
      next: (data) => {
        this.licencias = data;
        this.buscado = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMensaje = 'Error al obtener el listado de licencias.';
        this.cdr.detectChanges();
      }
    });
  }

  protected limpiar(): void {
    this.nombreApellido = '';
    this.grupoSanguineo = '';
    this.factorRH = '';
    this.donanteOrganos = undefined;
    this.licencias = [];
    this.buscado = false;
    this.errorMensaje = '';
  }

  protected getFechaVencimiento(licencia: Licencia): Date | null {
    if (!licencia.fechaEmision || !licencia.vigencia) return null;
    const emision = new Date(licencia.fechaEmision);
    emision.setFullYear(emision.getFullYear() + licencia.vigencia);
    return emision;
  }

  protected formatFactorRH(factor: string | undefined): string {
    if (!factor) return '-';
    return factor === 'POSITIVO' ? '+' : '-';
  }
}
