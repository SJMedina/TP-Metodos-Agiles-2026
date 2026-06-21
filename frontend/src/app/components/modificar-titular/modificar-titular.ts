import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TitularService } from '../services/alta-titular'; 
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-modificar-titular',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modificar-titular.html',
  styleUrls: ['../alta-titular/alta-titular.css']
})
export class ModificarTitularComponent implements OnInit {
  titularForm!: FormGroup;
  titularId!: number;

  constructor(
    private fb: FormBuilder,
    private titularService: TitularService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {

    //inicializo formualrio
    this.titularForm = this.fb.group({
      // campos bloqueados
      tipoDocumento: [{ value: '', disabled: true }, Validators.required],
      numeroDocumento: [{ value: '', disabled: true }, Validators.required],
      fechaNacimiento: [{ value: '', disabled: true }, Validators.required],
      
      // campos editables
      apellido: ['', Validators.required],
      nombre: ['', Validators.required],
      claseSolicitada: ['', Validators.required],
      grupoSanguineo: ['', Validators.required],
      donanteOrganos: [false, Validators.required],
      
      direccion: this.fb.group({
        calle: ['', Validators.required],
        nro: ['', [Validators.required, Validators.min(1)]],
        codigoPostal: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
        localidad: ['', Validators.required],
        provincia: ['', Validators.required],
        piso: [''],
        depto: ['']
      })
    });

    // capturo id y cargo datos
    this.titularId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.titularId) {
      this.cargarDatosTitular();
    }
  }

  cargarDatosTitular(): void {
    this.titularService.buscarPorId(this.titularId).subscribe({
      next: (titular) => {
        // patchValue rellena automáticamente todos los campos del formulario
        this.titularForm.patchValue(titular);
      },
      error: (err) => alert('Error al cargar los datos del titular')
    });
  }

  onSubmit(renovar: boolean = false): void {
    if (this.titularForm.valid) {

      const datosActualizados = this.titularForm.getRawValue();

      this.titularService.modificarTitular(this.titularId, datosActualizados).subscribe({
        next: () => {
          alert('Datos actualizados correctamente');
          
          if (renovar) {
            this.router.navigate(['/renovar-licencia', this.titularId]);
          } else {
            this.router.navigate(['/listado-titulares']); 
          }
        },
        error: (err) => {
          const mensaje = err.error ? err.error : 'Hubo un error en el servidor.';
          alert('Error: ' + mensaje);
        }
      });
    } else {
      this.titularForm.markAllAsTouched();
    }
  }
}