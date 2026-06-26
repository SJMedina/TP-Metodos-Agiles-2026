package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tpmetodosagiles2026.entity.Direccion;
import com.example.tpmetodosagiles2026.entity.Titular;
import com.example.tpmetodosagiles2026.repository.TitularRepository;

@Service
public class TitularService {

    private final TitularRepository titularRepository;

    @Autowired
    public TitularService(TitularRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    public Titular registrarTitular(Titular titular) {
        // Regla 2.2: Documento de 8 dígitos exactos
        if (titular.getNumeroDocumento() == null || !titular.getNumeroDocumento().matches("^\\d{8}$")) {
            throw new IllegalArgumentException("El número de documento debe contener exactamente 8 dígitos.");
        }

        // Regla 2.1: Mayor de 18 años
        if (titular.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        int edad = Period.between(titular.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El titular debe ser mayor de 18 años.");
        }

        // Validación adicional requerida: Código Postal de 4 dígitos
        if (titular.getDireccion() == null || titular.getDireccion().getCodigoPostal() == null || 
            !titular.getDireccion().getCodigoPostal().matches("^\\d{4}$")) {
            throw new IllegalArgumentException("El código postal debe contener exactamente 4 dígitos.");
        }

        return titularRepository.save(titular);
    }


    public Titular buscarTitularPorId(Long id) {
        return titularRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el titular con ID: " + id));
    }

    public java.util.List<Titular> listarTitulares() {
        return titularRepository.findAll();
    }
    
    public Titular modificarTitular(Long id, Titular datosActualizados) {
        
        Titular titularActual = titularRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el titular con ID: " + id));

        titularActual.setNombre(datosActualizados.getNombre());
        titularActual.setApellido(datosActualizados.getApellido());
        titularActual.setGrupoSanguineo(datosActualizados.getGrupoSanguineo());
        titularActual.setFactorRH(datosActualizados.getFactorRH());
        titularActual.setDonanteOrganos(datosActualizados.getDonanteOrganos());

        if (datosActualizados.getDireccion() != null) {
            if (datosActualizados.getDireccion().getCodigoPostal() == null || 
                !datosActualizados.getDireccion().getCodigoPostal().matches("^\\d{4}$")) {
                throw new IllegalArgumentException("El código postal debe contener exactamente 4 dígitos.");
            }
            
            Direccion dirExistente = titularActual.getDireccion();
            Direccion dirNueva = datosActualizados.getDireccion();
            
            dirExistente.setCalle(dirNueva.getCalle());
            dirExistente.setNro(dirNueva.getNro());
            dirExistente.setCodigoPostal(dirNueva.getCodigoPostal());
            dirExistente.setLocalidad(dirNueva.getLocalidad());
            dirExistente.setProvincia(dirNueva.getProvincia());
            dirExistente.setPiso(dirNueva.getPiso());
            dirExistente.setDepto(dirNueva.getDepto());
        }

        // 4. Guardamos los cambios. Al tener el mismo ID, Hibernate hace un UPDATE en vez de un INSERT
        return titularRepository.save(titularActual);
    }


}