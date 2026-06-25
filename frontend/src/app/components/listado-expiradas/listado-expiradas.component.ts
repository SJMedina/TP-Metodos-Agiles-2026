import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';
import { Licencia } from '../../models/licencia';

@Component({
  selector: 'app-listado-expiradas',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './listado-expiradas.component.html',
  styleUrls: ['./listado-expiradas.component.css']
})
export class ListadoExpiradasComponent implements OnInit {
  protected licencias: Licencia[] = [];
  protected fechaDesde: string = '';
  protected fechaHasta: string = '';
  protected cargando = false;
  protected error: string | null = null;

  constructor(
    private licenciaService: LicenciaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.cargando = true;
    this.error = null;
    const desde = this.fechaDesde || undefined;
    const hasta = this.fechaHasta || undefined;

    this.licenciaService.listarExpiradas(desde, hasta).subscribe({
      next: (data: Licencia[]) => {
        this.licencias = Array.isArray(data) ? data : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Error al cargar las licencias expiradas';
        this.licencias = [];
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  limpiar(): void {
    this.fechaDesde = '';
    this.fechaHasta = '';
    this.buscar();
  }

  calcularFechaVencimiento(licencia: Licencia): string {
    if (!licencia.fechaEmision || !licencia.vigencia) return 'N/D';
    try {
      const emision = licencia.fechaEmision as unknown;
      let fecha: Date;
      if (Array.isArray(emision)) {
        // Jackson serializa LocalDateTime como array [año, mes, día, hora, min, seg, ...]
        const [anio, mes, dia] = emision as number[];
        fecha = new Date(anio, mes - 1, dia);
      } else {
        fecha = new Date(emision as string);
      }
      fecha.setFullYear(fecha.getFullYear() + licencia.vigencia);
      return fecha.toLocaleDateString('es-AR');
    } catch {
      return 'N/D';
    }
  }

  formatearFechaEmision(fechaEmision: unknown): string {
    if (!fechaEmision) return 'N/D';
    try {
      if (Array.isArray(fechaEmision)) {
        const [anio, mes, dia] = fechaEmision as number[];
        return new Date(anio, mes - 1, dia).toLocaleDateString('es-AR');
      }
      return new Date(fechaEmision as string).toLocaleDateString('es-AR');
    } catch {
      return String(fechaEmision);
    }
  }
}
