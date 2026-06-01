import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';


@Component({
  selector: 'app-renovar-licencia',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './renovar-licencia.component.html',
  styleUrls: ['./renovar-licencia.component.css']
})
export class RenovarLicenciaComponent {
  private router = inject(Router);

  irAVencimiento(): void {
    this.router.navigate(['/renovar-licencia/vencimiento']);
  }

  irAModificacion(): void {
    this.router.navigate(['/renovar-licencia/modificacion']);
  }
}