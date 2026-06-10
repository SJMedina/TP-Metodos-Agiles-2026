import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuarioAdministrativoService } from '../../services/usuario-administrativo.service';

@Component({
  selector: 'app-alta-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alta-usuario.component.html',
  styleUrls: ['./alta-usuario.component.css']
})
export class AltaUsuarioComponent {
  id = '';
  nombre = '';
  password = '';
  mensaje = '';
  esError = false;
  cargando = false;

  constructor(
    private usuarioService: UsuarioAdministrativoService,
    private router: Router
  ) {}

  darDeAlta(): void {
    this.mensaje = '';

    if (!this.id.trim() || !this.nombre.trim() || !this.password.trim()) {
      this.mensaje = 'Todos los campos son obligatorios';
      this.esError = true;
      return;
    }

    this.cargando = true;
    this.usuarioService.crear({ id: this.id.trim(), nombre: this.nombre.trim(), password: this.password }).subscribe({
      next: () => {
        this.cargando = false;
        this.mensaje = 'Usuario creado exitosamente';
        this.esError = false;
        this.id = '';
        this.nombre = '';
        this.password = '';
      },
      error: (err: any) => {
        this.cargando = false;
        this.esError = true;
        if (err.status === 400) {
          this.mensaje = err.error?.error || 'Datos inválidos';
        } else if (err.status === 0) {
          this.mensaje = 'Error de conexión con el servidor';
        } else {
          this.mensaje = 'Error al crear el usuario';
        }
      }
    });
  }

  volver(): void {
    this.router.navigate(['/listar-licencias']);
  }
}
