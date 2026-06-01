import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';
import { HttpClient } from '@angular/common/http';

@Component({
 selector:'app-lista-licencias',
 standalone:true,
 imports:[CommonModule,RouterLink],
 templateUrl:'./lista-licencias.component.html',
 styleUrls:['./lista-licencias.component.css']
})
export class ListaLicenciasComponent implements OnInit {
 licencias:any[]=[];
 errorMensaje='';

 constructor(
   private licenciaService: LicenciaService,
   private cdr: ChangeDetectorRef,
   private http: HttpClient
 ){}

 ngOnInit(): void {
   this.cargarLicencias();
 }

 cargarLicencias(): void {
   this.licenciaService.listarLicencias().subscribe({
     next:(data:any)=>{
       console.log('LICENCIAS', data);

       if (Array.isArray(data)) {
         this.licencias = [...data];
       } else if (data?.content && Array.isArray(data.content)) {
         this.licencias = [...data.content];
       } else {
         this.licencias = Object.values(data || {});
       }

       console.log('TOTAL LICENCIAS:', this.licencias.length);
       this.cdr.detectChanges();
     },
     error:(err)=>{
       console.error(err);
       this.errorMensaje='Error cargando licencias';
       this.licencias=[];
       this.cdr.detectChanges();
     }
   });
 }

 imprimirLicencia(id: number): void{
    this.http.get(`http://localhost:8080/api/licencias/${id}/imprimir`, { responseType: 'blob' })
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const enlace = document.createElement('a');
          enlace.href = url;
          enlace.download = `tramite_licencia_${id}.zip`; 
          
          enlace.click();
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error al descargar el archivo', err);
          alert('Hubo un error al generar los PDFs');
        }
      });
 }


}
