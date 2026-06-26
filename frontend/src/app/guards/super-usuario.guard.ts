import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SuperUsuarioAuthService } from '../services/super-usuario-auth.service';

export const superUsuarioGuard: CanActivateFn = () => {
  const authService = inject(SuperUsuarioAuthService);
  const router = inject(Router);
  if (authService.isAuthenticated()) {
    return true;
  }
  router.navigate(['/login-super']);
  return false;
};
