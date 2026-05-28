import { Component, signal } from '@angular/core';
import { CalculadorCostoComponent } from './components/calculador-costo/calculador-costo'; // Importamos tu componente

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CalculadorCostoComponent], 
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('TP-Metodos-Agiles-2026');
}