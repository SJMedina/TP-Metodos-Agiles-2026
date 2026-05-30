import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = 'admin';
  password = 'admin123';
  mensaje = '';
  cargando = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    this.mensaje = '';

    if (!this.username.trim() || !this.password.trim()) {
      this.mensaje = 'Complete usuario y contraseña';
      return;
    }

    this.cargando = true;
    this.authService.login(this.username.trim(), this.password.trim()).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/listar-licencias']);
      },
      error: (err: any) => {
        this.cargando = false;
        
        // Mejor manejo de errores
        if (err.status === 0) {
          this.mensaje = 'Error de conexión. Verifica que el backend esté corriendo (http://localhost:8080)';
        } else if (err.status === 401 || err.status === 400) {
          this.mensaje = 'Usuario o contraseña incorrectos';
        } else if (err.status === 403) {
          this.mensaje = 'Acceso denegado';
        } else {
          this.mensaje = err.error?.message || 'Error al iniciar sesión. Intenta de nuevo.';
        }
      }
    });
  }
}
