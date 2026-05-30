import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LicenciaService } from '../../services/licencia.service';

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
   private cdr: ChangeDetectorRef
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
}
