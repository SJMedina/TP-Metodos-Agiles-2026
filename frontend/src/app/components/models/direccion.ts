export interface Direccion {
    calle: string;
    nro: number;
    codigoPostal: string;
    localidad: string;
    provincia: string;
    piso?: string; // Opcional
    depto?: string; // Opcional
}