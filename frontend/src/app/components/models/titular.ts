import { Direccion } from './direccion';

export interface Titular {
  id?: number;
  tipoDocumento: string;
  numeroDocumento: string;
  apellido: string;
  nombre: string;
  fechaNacimiento: string; 
  direccion: Direccion; // <-- Ahora usa la interfaz Direccion
  claseSolicitada: string;
  grupoSanguineo: string;
  donanteOrganos: boolean;
}