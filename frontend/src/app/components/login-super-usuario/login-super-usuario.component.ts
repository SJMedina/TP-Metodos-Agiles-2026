import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SuperUsuarioAuthService } from '../../services/super-usuario-auth.service';

@Component({
  selector: 'app-login-super-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-super-usuario.component.html',
  styleUrls: ['./login-super-usuario.component.css']
})
export class LoginSuperUsuarioComponent {
  id = '';
  password = '';
  mensaje = '';
  cargando = false;

  constructor(
    private authService: SuperUsuarioAuthService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    this.mensaje = '';

    if (!this.id.trim() || !this.password.trim()) {
      this.mensaje = 'Complete ID y contraseña';
      return;
    }

    this.cargando = true;
    this.authService.login(this.id.trim(), this.password).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/alta-usuario']);
      },
      error: (err: any) => {
        this.cargando = false;
        if (err.status === 0) {
          this.mensaje = 'Error de conexión con el servidor';
        } else if (err.status === 401) {
          this.mensaje = 'ID o contraseña incorrectos';
        } else {
          this.mensaje = 'Error al iniciar sesión';
        }
      }
    });
  }
}
