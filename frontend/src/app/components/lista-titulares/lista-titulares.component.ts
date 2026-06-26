import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TitularService } from '../services/alta-titular';
import { Titular } from '../models/titular';

@Component({
  selector: 'app-lista-titulares',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-titulares.component.html',
  styleUrls: ['./lista-titulares.component.css']
})
export class ListaTitularesComponent implements OnInit {
  protected titulares: Titular[] = [];
  protected error: string | null = null;
  protected cargando = false;

  private titularService = inject(TitularService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = null;
    this.titularService.listarTitulares().subscribe({
      next: (data) => {
        this.titulares = Array.isArray(data) ? data : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar el listado de titulares.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }
}
