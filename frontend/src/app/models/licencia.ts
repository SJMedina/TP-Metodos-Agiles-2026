export interface Licencia {
  id?: number;
  titular: string;
  edad?: number;
  numeroDocumento: string;
  clase: string;
  fechaNacimiento?: string;
  observaciones?: string;
  usuarioAdministrativo?: string;
  fechaEmision?: string;
  costo?: number;
  vigencia?: number;
  poseeLicenciaB?: boolean;
  antiguedadLicenciaBEnAnios?: number;
  tieneLicenciaProfesionalAnterior?: boolean;
}
