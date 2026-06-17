import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuarioAdministrativoService } from '../../services/usuario-administrativo.service';
import { UsuarioAdministrativo } from '../../models/usuario-administrativo';

@Component({
  selector: 'app-modificar-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modificar-usuario.component.html',
  styleUrls: ['./modificar-usuario.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModificarUsuarioComponent implements OnInit {
  usuarios: UsuarioAdministrativo[] = [];
  usuarioSeleccionado: UsuarioAdministrativo | null = null;
  nombre = '';
  password = '';
  mensaje = '';
  esError = false;
  cargando = false;
  cargandoLista = true;

  constructor(
    private usuarioService: UsuarioAdministrativoService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargandoLista = true;
    this.cdr.markForCheck();
    this.usuarioService.listar().subscribe({
      next: (usuarios) => {
        // Excelente la validación para asegurarte de que sea un array
        this.usuarios = Array.isArray(usuarios) ? usuarios : [];
        this.cargandoLista = false;
        this.mensaje = '';
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.cargandoLista = false;
        this.esError = true;
        this.usuarios = [];
        console.error('Error al cargar usuarios:', err);

        if (err.status === 401) {
          this.mensaje = 'Sesión expirada. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          this.mensaje = err.error?.error || 'No tienes permisos para acceder a esta funcionalidad';
        } else if (err.status === 0) {
          this.mensaje = 'Error de conexión con el servidor';
        } else {
          this.mensaje = err.error?.error || `Error al cargar usuarios (${err.status || 'desconocido'})`;
        }
        this.cdr.markForCheck();
      }
    });
  }

  seleccionarUsuario(usuario: UsuarioAdministrativo): void {
    this.usuarioSeleccionado = usuario;
    this.nombre = usuario.nombre;
    this.password = '';
    this.mensaje = '';
    this.cdr.markForCheck();
  }

  actualizar(): void {
    if (!this.usuarioSeleccionado) {
      this.mensaje = 'Selecciona un usuario';
      this.esError = true;
      this.cdr.markForCheck();
      return;
    }

    const nombre = this.nombre.trim();
    const password = this.password.trim();

    if (!nombre && !password) {
      this.mensaje = 'Completa al menos un campo';
      this.esError = true;
      this.cdr.markForCheck();
      return;
    }

    this.cargando = true;
    this.cdr.markForCheck();
    const datos: any = {};
    if (nombre) datos.nombre = nombre;
    if (password) datos.password = password;

    this.usuarioService.actualizar(this.usuarioSeleccionado.id, datos).subscribe({
      next: () => {
        this.cargando = false;
        this.mensaje = 'Usuario actualizado exitosamente';
        this.esError = false;
        this.usuarioSeleccionado = null;
        this.nombre = '';
        this.password = '';
        this.cdr.markForCheck();
        this.cargarUsuarios();
      },
      error: (err: any) => {
        this.cargando = false;
        this.esError = true;

        if (err.status === 401) {
          this.mensaje = 'Sesión expirada';
        } else if (err.status === 403) {
          this.mensaje = err.error?.error || 'No tienes permisos para realizar esta acción';
        } else if (err.status === 404) {
          this.mensaje = err.error?.error || 'Usuario no encontrado';
        } else if (err.status === 0) {
          this.mensaje = 'Error de conexión con el servidor';
        } else {
          this.mensaje = err.error?.error || 'Error al actualizar el usuario';
        }
        this.cdr.markForCheck();
      }
    });
  }

  cancelar(): void {
    this.usuarioSeleccionado = null;
    this.nombre = '';
    this.password = '';
    this.mensaje = '';
    this.cdr.markForCheck();
  }

  volver(): void {
    this.router.navigate(['/listar-licencias']);
  }
}
