import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LicenciaCostoService } from '../../services/licencia-costo'; 

@Component({
  selector: 'app-calculador-costo',
  standalone: true,
  imports: [FormsModule, CommonModule], 
  templateUrl: './calculador-costo.html',
  styleUrl: './calculador-costo.css'
})
export class CalculadorCostoComponent {
  private costoService = inject(LicenciaCostoService);
  private cdr = inject(ChangeDetectorRef);

  protected claseSeleccionada: string = '';
  protected vigenciaSeleccionada: number | null = null;
  protected costoCalculado: number | null = null;

  protected actualizarCosto(): void {
    if (this.claseSeleccionada && this.vigenciaSeleccionada) {
      this.costoService.obtenerCosto(this.claseSeleccionada, this.vigenciaSeleccionada).subscribe({
        next: (costo: number) => {
          this.costoCalculado = costo;
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          console.error('Error al calcular el costo:', err);
          this.costoCalculado = null;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.costoCalculado = null;
      this.cdr.detectChanges();
    }
  }
}