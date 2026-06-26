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
  fechaVencimiento?: string;
  costo?: number;
  vigencia?: number;
  vigente?: boolean;
  grupoSanguineo?: string;
  factorRH?: string;
  donanteOrganos?: boolean;
  poseeLicenciaB?: boolean;
  antiguedadLicenciaBEnAnios?: number;
  tieneLicenciaProfesionalAnterior?: boolean;
}
