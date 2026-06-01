import { Component, inject, Input, Output, EventEmitter, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LicenciaCostoService } from '../../services/licencia-costo';

@Component({
  selector: 'app-calculador-costo',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './calculador-costo.html',
  styleUrls: ['./calculador-costo.css']
})
export class CalculadorCostoComponent implements OnChanges {
  @Input() claseBase: string = '';
  @Input() vigenciaFija: number | null = null;
  @Output() costoEmitido = new EventEmitter<{ costo: number; vigencia: number }>();

  private costoService = inject(LicenciaCostoService);
  private cdr = inject(ChangeDetectorRef);

  protected claseSeleccionada: string = '';
  protected vigenciaSeleccionada: number | null = null;
  protected costoCalculado: number | null = null;
  protected mensajeCarga: string | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['claseBase'] && this.claseBase) {
      this.claseSeleccionada = this.claseBase;
      this.actualizarCosto();
    }
    //Agregado para calcular el costo automaticamente cuando se trata de una renovacion
    if (changes['vigenciaFija'] && this.vigenciaFija) {
      this.vigenciaSeleccionada = this.vigenciaFija;
    }
    if (this.claseSeleccionada && this.vigenciaSeleccionada) {
      this.actualizarCosto();
    }
  }

  protected actualizarCosto(): void {
    this.mensajeCarga = null;

    if (!this.claseSeleccionada) {
      this.costoCalculado = null;
      this.mensajeCarga = 'Seleccione una clase de licencia válida en el formulario principal.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.vigenciaSeleccionada) {
      this.costoCalculado = null;
      this.mensajeCarga = 'Seleccione la vigencia para calcular el costo.';
      this.cdr.detectChanges();
      return;
    }

    this.costoService.obtenerCosto(this.claseSeleccionada, this.vigenciaSeleccionada).subscribe({
      next: (costo: number) => {
        this.costoCalculado = costo;
        this.costoEmitido.emit({ costo, vigencia: this.vigenciaSeleccionada! });
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Error al calcular el costo:', err);
        this.costoCalculado = null;
        this.mensajeCarga = err.error?.error || 'No se pudo calcular el costo. Intente nuevamente.';
        this.cdr.detectChanges();
      }
    });
  }
}