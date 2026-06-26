import { Component, inject, ChangeDetectorRef, OnInit } from '@angular/core';
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
export class ListaLicenciasVigentesComponent implements OnInit {
  protected nombreApellido = '';
  protected grupoSanguineo = '';
  protected factorRH = '';
  protected donanteOrganos: boolean | undefined = undefined;

  // Pestaña activa: licencias vigentes o historial (no vigentes).
  protected tab: 'vigentes' | 'historial' = 'vigentes';

  protected licencias: Licencia[] = [];
  protected buscado = false;
  protected errorMensaje = '';

  protected readonly gruposSanguineos = ['A', 'B', 'AB', 'O'];
  protected readonly factoresRH = ['POSITIVO', 'NEGATIVO'];

  private licenciaService = inject(LicenciaService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.buscar();
  }

  protected cambiarTab(tab: 'vigentes' | 'historial'): void {
    if (this.tab === tab) return;
    this.tab = tab;
    this.buscar();
  }

  protected buscar(): void {
    this.errorMensaje = '';
    this.buscado = false;

    const filtros: any = {};
    if (this.nombreApellido.trim()) filtros.nombreApellido = this.nombreApellido.trim();
    if (this.grupoSanguineo) filtros.grupoSanguineo = this.grupoSanguineo;
    if (this.factorRH) filtros.factorRH = this.factorRH;
    if (this.donanteOrganos !== undefined) filtros.donanteOrganos = this.donanteOrganos;

    const peticion = this.tab === 'vigentes'
      ? this.licenciaService.listarVigentes(filtros)
      : this.licenciaService.listarHistorial(filtros);

    peticion.subscribe({
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
    this.buscar();
  }

  protected getFechaVencimiento(licencia: Licencia): Date | null {
    // Usa la fecha de vencimiento guardada; cae a fechaEmisión + vigencia para registros antiguos.
    if (licencia.fechaVencimiento) {
      const v = String(licencia.fechaVencimiento);
      const m = v.match(/^(\d{4})-(\d{2})-(\d{2})/);
      if (m) return new Date(+m[1], +m[2] - 1, +m[3]);
      return new Date(v);
    }
    if (!licencia.fechaEmision || !licencia.vigencia) return null;
    const emision = new Date(licencia.fechaEmision);
    emision.setFullYear(emision.getFullYear() + licencia.vigencia);
    return emision;
  }

  protected estaVencida(licencia: Licencia): boolean {
    const venc = this.getFechaVencimiento(licencia);
    if (!venc) return false;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    return venc < hoy;
  }

  protected estadoLicencia(licencia: Licencia): string {
    if (this.estaVencida(licencia)) return 'Vencida';
    return licencia.vigente ? 'Vigente' : 'Reemplazada';
  }

  protected formatFactorRH(factor: string | undefined): string {
    if (!factor) return '-';
    return factor === 'POSITIVO' ? '+' : '-';
  }
}
