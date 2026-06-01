import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CalculadorCostoComponent } from '../../calculador-costo/calculador-costo';
import { LicenciaService } from '../../../services/licencia.service';
import { AuthService } from '../../../auth.service';
import { Licencia } from '../../../models/licencia';


@Component({
    selector: 'app-renovar-vencimiento',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, CalculadorCostoComponent],
    templateUrl: './renovar-vencimiento.component.html',
    styleUrls: ['./renovar-vencimiento.component.css'],
})
export class RenovarVencimientoComponent {
    protected vigenciaCalculada: number = 1;
    protected documento: string = '';
    protected licencias: Licencia[] = [];
    protected licenciaSeleccionada: Licencia | null = null;
    protected mensaje: string | null = null;
    protected errorValidacion: string | null = null;
    protected mostrandoCalculador = false;
    protected costoCalculado: number | null = null;
    protected vigenciaSeleccionada: number = 1;
    protected currentUser = '';
    protected buscado = false;

    private licenciaService = inject(LicenciaService);
    private authService = inject(AuthService);
    private router = inject(Router);
    private cdr = inject(ChangeDetectorRef);

    ngOnInit(): void {
        const user = this.authService.getUser();
        this.currentUser = user?.username || '';
    }

    protected buscarPorDocumento(): void {
        this.errorValidacion = null;
        this.mensaje = null;
        this.licenciaSeleccionada = null;
        this.costoCalculado = null;
        this.buscado = false;

        if (!this.documento.trim()) {
            this.errorValidacion = 'Ingrese un número de documento.';
            this.cdr.detectChanges();
            return;
        }

        this.licenciaService.listarPorDocumento(this.documento.trim()).subscribe({
            next: (licencias) => {
                this.licencias = licencias;
                this.buscado = true;
                if (licencias.length === 0) {
                    this.errorValidacion = 'No se encontraron licencias para ese documento.';
                }
                this.cdr.detectChanges();
            },
            error: () => {
                this.errorValidacion = 'Error al buscar licencias.';
                this.cdr.detectChanges();
            }
        });
    }

    protected seleccionarLicencia(licencia: Licencia): void {
        this.licenciaSeleccionada = licencia;
        this.costoCalculado = null;
        this.vigenciaCalculada = this.calcularVigenciaSegunEdad(licencia.fechaNacimiento);
        this.mostrandoCalculador = false;
        this.errorValidacion = null;
        this.mensaje = null;
    }
    protected calcularVigenciaSegunEdad(fechaNacimiento: string | undefined): number {
        if (!fechaNacimiento) return 1;
        const edad = this.calcularEdad(fechaNacimiento);
        if (edad < 21) return 3;
        else if (edad <= 46) return 5;
        else if (edad <= 60) return 4;
        else if (edad <= 70) return 3;
        else return 1;
    }

    protected calcularEdad(fechaNacimiento?: string): number {
        if (!fechaNacimiento) return 0;
        const nacimiento = new Date(fechaNacimiento);
        if (isNaN(nacimiento.getTime())) return 0;
        const hoy = new Date();
        let edad = hoy.getFullYear() - nacimiento.getFullYear();
        const mes = hoy.getMonth() - nacimiento.getMonth();
        if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) edad--;
        return edad;
    }

    protected getFechaVencimiento(licencia: Licencia): Date {
        const emision = new Date(licencia.fechaEmision || new Date());
        emision.setFullYear(emision.getFullYear() + (licencia.vigencia ?? 0));
        return emision;
    }

    protected estaEnVentanaRenovacion(licencia: Licencia): boolean {
        const hoy = new Date();
        const vencimiento = this.getFechaVencimiento(licencia);
        const unMesAntes = new Date(vencimiento);
        unMesAntes.setMonth(unMesAntes.getMonth() - 1);
        const seisMesesAntes = new Date(vencimiento);
        seisMesesAntes.setMonth(seisMesesAntes.getMonth() - 6);
        return hoy >= seisMesesAntes && hoy <= unMesAntes;
    }

    protected toggleCalculador(): void {
        if (!this.licenciaSeleccionada) {
            this.errorValidacion = 'Seleccione una licencia primero.';
            return;
        }
        this.mostrandoCalculador = !this.mostrandoCalculador;
    }

    protected cerrarCalculador(): void {
        this.mostrandoCalculador = false;
    }

    protected handleCostoCalculado(event: { costo: number; vigencia: number }): void {
        this.costoCalculado = event.costo;
        this.vigenciaSeleccionada = event.vigencia;
        this.mostrandoCalculador = false;
        this.mensaje = `Costo calculado: $${event.costo}.`;
    }

    protected renovar(): void {
        this.errorValidacion = null;

        if (!this.licenciaSeleccionada) {
            this.errorValidacion = 'Seleccione una licencia.';
            return;
        }

        if (!this.estaEnVentanaRenovacion(this.licenciaSeleccionada)) {
            this.errorValidacion = 'La licencia no está dentro del período de renovación (entre 1 y 6 meses antes del vencimiento).';
            return;
        }

        if (this.costoCalculado === null) {
            this.errorValidacion = 'Calcule el costo antes de renovar.';
            return;
        }

        const payload = {
            id: this.licenciaSeleccionada.id,
            vigencia: this.vigenciaSeleccionada,
            renovarPorVencimiento: true
        };

        this.licenciaService.renovarLicencia(payload).subscribe({
            next: () => {
                this.mensaje = 'Licencia renovada correctamente.';
                this.router.navigate(['/listar-licencias']);
            },
            error: (err: any) => {
                this.errorValidacion = err.error?.error || 'No se pudo renovar la licencia.';
            }
        });
    }
}