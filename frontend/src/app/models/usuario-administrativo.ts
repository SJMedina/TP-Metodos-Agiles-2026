export interface UsuarioAdministrativo {
  id: string;
  nombre: string;
  passwordHash: string;
}

export interface CrearUsuarioRequest {
  id: string;
  nombre: string;
  password: string;
}
