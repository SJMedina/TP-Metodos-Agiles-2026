import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TitularService } from '../services/alta-titular';

@Component({
  selector: 'app-alta-titular',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './alta-titular.html',
  styleUrls: ['./alta-titular.css']
})
export class AltaTitularComponent implements OnInit {
  titularForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private titularService: TitularService
  ) {}

  ngOnInit(): void {
    this.titularForm = this.fb.group({
      tipoDocumento: ['', Validators.required],
      // Regla 2.2: Exactamente 8 dígitos numéricos
      numeroDocumento: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      apellido: ['', Validators.required],
      nombre: ['', Validators.required],
      // Regla 2.1: Validador personalizado para mayor de 18 años
      fechaNacimiento: ['', [Validators.required, this.edadMinimaValidator(18)]],
      claseSolicitada: ['', Validators.required],
      grupoSanguineo: ['', Validators.required],
      factorRH: ['', Validators.required],
      donanteOrganos: [false, Validators.required],
      
      // Regla 1: Sub-formulario (FormGroup anidado) para la Dirección
      direccion: this.fb.group({
        calle: ['', Validators.required],
        nro: ['', [Validators.required, Validators.min(1)]],
        // Código postal de exactamente 4 dígitos
        codigoPostal: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
        localidad: ['', Validators.required],
        provincia: ['', Validators.required],
        piso: [''],
        depto: ['']
      })
    });
  }

  // Validador personalizado para calcular la edad
  edadMinimaValidator(edadMinima: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      
      const fechaNacimiento = new Date(control.value);
      const hoy = new Date();
      let edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
      const m = hoy.getMonth() - fechaNacimiento.getMonth();
      
      if (m < 0 || (m === 0 && hoy.getDate() < fechaNacimiento.getDate())) {
        edad--;
      }
      
      return edad >= edadMinima ? null : { menorDeEdad: true };
    };
  }

  onSubmit(): void {
    if (this.titularForm.valid) {
      this.titularService.registrarTitular(this.titularForm.value).subscribe({
        next: (response) => {
          alert('¡Alta exitosa!');
          this.titularForm.reset();
        },
        error: (err) => {
          // Mostramos el mensaje de error que configuramos en el backend
          const mensaje = err.error ? err.error : 'Ocurrió un error en el servidor.';
          alert('Error: ' + mensaje);
        }
      });
    } else {
      this.titularForm.markAllAsTouched();
    }
  }
}