import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';
import { Licencia } from '../../models/licencia';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-lista-licencias',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-licencias.component.html',
  styleUrls: ['./lista-licencias.component.css']
})
export class ListaLicenciasComponent implements OnInit {
  licencias: Licencia[] = [];
  errorMensaje = '';
  successMensaje = '';

  private readonly licenciaService: LicenciaService = inject(LicenciaService);
  private readonly cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly http: HttpClient = inject(HttpClient);

  // Modal / copy state
  selectedParaCopia: Licencia | null = null;
  isCopyModalOpen = false;
  readonly copiaCosto = 50;

  ngOnInit(): void {
    this.cargarLicencias();
  }

  cargarLicencias(): void {
    this.licenciaService.listarLicencias().subscribe({
      next: (data: any) => {
        if (Array.isArray(data)) {
          this.licencias = [...data];
        } else if (data?.content && Array.isArray(data.content)) {
          this.licencias = [...data.content];
        } else if (data) {
          this.licencias = Object.values(data as any);
        } else {
          this.licencias = [];
        }
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.errorMensaje = 'Error cargando licencias';
        this.successMensaje = '';
        this.licencias = [];
        this.cdr.detectChanges();
      }
    });
  }

  emitirCopiaLicencia(id?: number): void {
    // kept for backward compatibility; prefer openCopyModal
    if (id == null) {
      console.warn('Emitir copia de licencia: id inválido', id);
      return;
    }

    this.openCopyModal(this.licencias.find(l => l.id === id) || null);
  }

  openCopyModal(lic?: Licencia | null): void {
    if (!lic) {
      console.warn('openCopyModal: licencia inválida', lic);
      return;
    }
    this.selectedParaCopia = lic;
    this.isCopyModalOpen = true;
    this.errorMensaje = '';
    this.successMensaje = '';
  }

  cancelCopy(): void {
    this.selectedParaCopia = null;
    this.isCopyModalOpen = false;
  }

  confirmCopy(): void {
    if (!this.selectedParaCopia?.id) return;

    this.licenciaService.emitirCopia(this.selectedParaCopia.id).subscribe({
      next: (nueva: any) => {
        this.successMensaje = 'Copia de licencia emitida correctamente.';
        this.isCopyModalOpen = false;
        // intentar descargar el comprobante de la copia recien creada
        if (nueva?.id) {
          this.imprimirLicencia(nueva.id);
        }
        this.cargarLicencias();
      },
      error: (err: any) => {
        console.error('Error al emitir copia de licencia', err);
        this.errorMensaje = err.error?.error || 'Error al emitir copia de licencia';
      }
    });
  }

  getCopyLabel(lic: Licencia): string {
    if (!lic.numeroDocumento) return '';
    const mismas = this.licencias
      .filter(l => l.numeroDocumento === lic.numeroDocumento)
      .slice()
      .sort((a, b) => {
        const da = a.fechaEmision ? new Date(a.fechaEmision).getTime() : 0;
        const db = b.fechaEmision ? new Date(b.fechaEmision).getTime() : 0;
        return da - db;
      });
    const idx = mismas.findIndex(m => m === lic) + 1;
    if (idx <= 0) return '';
    const labels: { [k: number]: string } = {
      1: 'Original',
      2: 'Duplicado',
      3: 'Triplicado',
      4: 'Cuadruplicado',
      5: 'Quintuplicado',
      6: 'Sextuplicado',
      7: 'Septuplicado',
      8: 'Octuplicado',
      9: 'Nonuplicado',
      10: 'Decuplicado'
    };
    return labels[idx] || `Copia #${idx}`;
  }

  imprimirLicencia(id?: number): void {
    if (id == null) {
      console.warn('Imprimir licencia: id inválido', id);
      return;
    }

    this.http.get(`http://localhost:8080/api/licencias/${id}/imprimir`, { responseType: 'blob' })
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const enlace = document.createElement('a');
          enlace.href = url;
          enlace.download = `tramite_licencia_${id}.zip`;
          enlace.click();
          window.URL.revokeObjectURL(url);
        },
        error: (err: any) => {
          console.error('Error al descargar el archivo', err);
          alert('Hubo un error al generar los PDFs');
        }
      });
  }

}
