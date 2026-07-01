package com.example.tpmetodosagiles2026.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tpmetodosagiles2026.dto.EmitirLicenciaDTO;
import com.example.tpmetodosagiles2026.dto.RenovarLicenciaDTO;
import com.example.tpmetodosagiles2026.model.FactorRH;
import com.example.tpmetodosagiles2026.model.GrupoSanguineo;
import com.example.tpmetodosagiles2026.model.Licencia;
import com.example.tpmetodosagiles2026.repository.LicenciaRepository;
import com.example.tpmetodosagiles2026.repository.TitularRepository;

@Service
public class LicenciaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenciaService.class);

    private final LicenciaRepository repository;
    private final LicenciaCostoService costoService;
    private final TitularRepository titularRepository;

    public LicenciaService(LicenciaRepository repository, LicenciaCostoService costoService,
            TitularRepository titularRepository) {
        this.repository = repository;
        this.costoService = costoService;
        this.titularRepository = titularRepository;
    }

    @Transactional
    public Licencia emitir(EmitirLicenciaDTO dto) {
        validarCamposObligatorios(dto);
        normalizarCampos(dto);
        // La persona debe haber sido dada de alta como titular antes de emitir la licencia.
        if (!titularRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new IllegalArgumentException(
                    "No existe un titular dado de alta con ese documento. Debe darse de alta antes de emitir la licencia.");
        }
        validarEdadContraFechaNacimiento(dto);
        validarPorClase(dto);

        // Invariante: a lo sumo una licencia vigente por titular (documento).
        // Al emitir, se archivan todas las vigentes previas de la persona (cualquier clase)
        // para que solo cuente la nueva con los datos actualizados; se conserva el historial.
        for (Licencia anterior : repository.findByNumeroDocumentoAndVigenteTrue(dto.getNumeroDocumento())) {
            anterior.setVigente(false);
            repository.save(anterior);
            LOGGER.info("Archivando licencia vigente previa id={} documento={} clase={}",
                    anterior.getId(), anterior.getNumeroDocumento(), anterior.getClase());
        }

        // La vigencia se determina automáticamente según la edad del titular.
        boolean esPrimeraVez = repository.findByNumeroDocumento(dto.getNumeroDocumento()).isEmpty();
        int vigencia = calcularVigenciaPorEdad(dto.getEdad(), esPrimeraVez);
        LocalDate inicio = LocalDate.now();

        Licencia licencia = new Licencia();
        licencia.setTitular(dto.getTitular());
        licencia.setEdad(dto.getEdad());
        licencia.setNumeroDocumento(dto.getNumeroDocumento());
        licencia.setFechaNacimiento(dto.getFechaNacimiento());
        licencia.setClase(dto.getClase());
        licencia.setObservaciones(dto.getObservaciones());
        licencia.setVigencia(vigencia);
        licencia.setFechaVencimiento(calcularFechaVencimiento(dto.getFechaNacimiento(), inicio, vigencia));
        if (dto.getGrupoSanguineo() != null && !dto.getGrupoSanguineo().isEmpty())
            licencia.setGrupoSanguineo(GrupoSanguineo.valueOf(dto.getGrupoSanguineo()));
        if (dto.getFactorRH() != null && !dto.getFactorRH().isEmpty())
            licencia.setFactorRH(FactorRH.valueOf(dto.getFactorRH()));
        licencia.setDonanteOrganos(Boolean.TRUE.equals(dto.getDonanteOrganos()));
        double costoLicencia = costoService.calcularCostoTotal(dto.getClase(), vigencia);
        licencia.setCosto(costoLicencia);
        licencia.setFechaEmision(LocalDateTime.now());
        licencia.setVigente(true);
        licencia.setUsuarioAdministrativo(obtenerUsuarioActual());

        LOGGER.info("Persisting licencia for documento={} clase={} usuario={}", dto.getNumeroDocumento(),
                dto.getClase(), licencia.getUsuarioAdministrativo());
        Licencia saved = repository.save(licencia);
        repository.flush();
        LOGGER.info("Licencia persistida con id={} documento={} clase={}", saved.getId(), saved.getNumeroDocumento(),
                saved.getClase());
        return saved;
    }

    @Transactional
    public Licencia renovar(RenovarLicenciaDTO dto) {
        Licencia licenciaActual = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una licencia con id=" + dto.getId()));

        if (!Boolean.TRUE.equals(licenciaActual.getVigente())) {
            throw new IllegalArgumentException("La licencia no está vigente y no puede ser renovada");
        }

        boolean esPorVencimiento = Boolean.TRUE.equals(dto.getRenovarPorVencimiento());

        if (esPorVencimiento) {
            validarVigenciaRenovacion(dto.getVigencia());
            validarVentanaRenovacion(licenciaActual);
            return renovarPorVencimiento(licenciaActual, dto);
        } else {
            return modificarDatos(licenciaActual, dto);
        }
    }

    private Licencia renovarPorVencimiento(Licencia licenciaActual, RenovarLicenciaDTO dto) {
        licenciaActual.setVigente(false);
        repository.save(licenciaActual);

        Licencia nueva = new Licencia();
        nueva.setNumeroDocumento(licenciaActual.getNumeroDocumento());
        nueva.setClase(licenciaActual.getClase());
        nueva.setGrupoSanguineo(licenciaActual.getGrupoSanguineo());
        nueva.setFactorRH(licenciaActual.getFactorRH());
        nueva.setDonanteOrganos(licenciaActual.getDonanteOrganos());
        nueva.setTitular(licenciaActual.getTitular());
        nueva.setEdad(licenciaActual.getEdad());
        nueva.setFechaNacimiento(licenciaActual.getFechaNacimiento());
        nueva.setObservaciones(licenciaActual.getObservaciones());
        nueva.setVigencia(dto.getVigencia());
        nueva.setFechaEmision(LocalDateTime.now());
        nueva.setFechaVencimiento(
                calcularFechaVencimiento(licenciaActual.getFechaNacimiento(), LocalDate.now(), dto.getVigencia()));
        nueva.setUsuarioAdministrativo(obtenerUsuarioActual());
        nueva.setVigente(true);
        nueva.setCosto(costoService.calcularCostoTotal(licenciaActual.getClase(), dto.getVigencia()));

        LOGGER.info("Renovando por vencimiento licencia id={}", licenciaActual.getId());
        Licencia saved = repository.save(nueva);
        repository.flush();
        LOGGER.info("Nueva licencia creada id={} (reemplaza id={})", saved.getId(), licenciaActual.getId());
        return saved;
    }

    private Licencia modificarDatos(Licencia licencia, RenovarLicenciaDTO dto) {
        if (dto.getTitular() != null) licencia.setTitular(dto.getTitular().trim());
        if (dto.getEdad() != null) licencia.setEdad(dto.getEdad());
        if (dto.getFechaNacimiento() != null) licencia.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getObservaciones() != null) licencia.setObservaciones(dto.getObservaciones().trim());
        if (dto.getGrupoSanguineo() != null && !dto.getGrupoSanguineo().isEmpty())
            licencia.setGrupoSanguineo(GrupoSanguineo.valueOf(dto.getGrupoSanguineo()));
        if (dto.getFactorRH() != null && !dto.getFactorRH().isEmpty())
            licencia.setFactorRH(FactorRH.valueOf(dto.getFactorRH()));
        if (dto.getDonanteOrganos() != null) licencia.setDonanteOrganos(dto.getDonanteOrganos());
        licencia.setUsuarioAdministrativo(obtenerUsuarioActual());

        LOGGER.info("Modificando datos licencia id={}", licencia.getId());
        Licencia saved = repository.save(licencia);
        repository.flush();
        return saved;
    }

    @Transactional
    public Licencia renovarConDatosActualizados(RenovarLicenciaDTO dto) {
        Licencia actual = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una licencia con id=" + dto.getId()));
        if (!Boolean.TRUE.equals(actual.getVigente())) {
            throw new IllegalArgumentException("La licencia no está vigente y no puede renovarse");
        }

        actual.setVigente(false);
        repository.save(actual);

        int vigencia = calcularVigenciaPorEdad(actual.getEdad(), false); // ya tuvo licencia
        LocalDate inicio = LocalDate.now();

        Licencia nueva = new Licencia();
        nueva.setNumeroDocumento(actual.getNumeroDocumento());
        nueva.setClase(actual.getClase());
        nueva.setFechaNacimiento(actual.getFechaNacimiento());
        nueva.setEdad(actual.getEdad());
        nueva.setObservaciones(actual.getObservaciones());
        nueva.setTitular(dto.getTitular() != null ? dto.getTitular().trim() : actual.getTitular());
        nueva.setGrupoSanguineo(dto.getGrupoSanguineo() != null && !dto.getGrupoSanguineo().isEmpty()
                ? GrupoSanguineo.valueOf(dto.getGrupoSanguineo()) : actual.getGrupoSanguineo());
        nueva.setFactorRH(dto.getFactorRH() != null && !dto.getFactorRH().isEmpty()
                ? FactorRH.valueOf(dto.getFactorRH()) : actual.getFactorRH());
        nueva.setDonanteOrganos(dto.getDonanteOrganos() != null ? dto.getDonanteOrganos() : actual.getDonanteOrganos());
        nueva.setVigencia(vigencia);
        nueva.setFechaEmision(LocalDateTime.now());
        nueva.setFechaVencimiento(calcularFechaVencimiento(actual.getFechaNacimiento(), inicio, vigencia));
        nueva.setUsuarioAdministrativo(obtenerUsuarioActual());
        nueva.setVigente(true);
        nueva.setCosto(costoService.calcularCostoTotal(actual.getClase(), vigencia));

        LOGGER.info("Renovando con datos actualizados licencia id={} -> nueva", actual.getId());
        Licencia saved = repository.save(nueva);
        repository.flush();
        return saved;
    }

    private void validarVigenciaRenovacion(Integer vigencia) {
        if (vigencia == null) {
            throw new IllegalArgumentException("La vigencia es obligatoria para la renovacion");
        }
        if (vigencia != 1 && vigencia != 3 && vigencia != 4 && vigencia != 5) {
            throw new IllegalArgumentException("La vigencia debe ser 1, 3, 4 o 5 anos");
        }
    }

    private void validarVentanaRenovacion(Licencia licencia) {

        LocalDate fechaVencimiento = obtenerFechaVencimiento(licencia);

        LocalDate hoy = LocalDate.now();
        LocalDate unMesAntes = fechaVencimiento.minusMonths(1);
        LocalDate seisMesesAntes = fechaVencimiento.minusMonths(6);

        // Debe estar entre 6 meses y 1 mes antes del vencimiento
        if (hoy.isBefore(seisMesesAntes) || hoy.isAfter(unMesAntes)) {
            throw new IllegalArgumentException(
                    "La licencia solo puede renovarse por vencimiento entre 6 y 1 mes antes de su fecha de vencimiento. "
                            +
                            "Vence: " + fechaVencimiento);
        }
    }

    

    public long contarLicencias() {
        return repository.count();
    }

    private void validarCamposObligatorios(EmitirLicenciaDTO dto) {
        if (dto.getTitular() == null || dto.getTitular().trim().isEmpty()) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
        if (dto.getEdad() == null || dto.getEdad() <= 0) {
            throw new IllegalArgumentException("La edad es obligatoria y debe ser mayor a 0");
        }
        if (dto.getClase() == null || dto.getClase().trim().isEmpty()) {
            throw new IllegalArgumentException("La clase de licencia es obligatoria");
        }
        if (dto.getNumeroDocumento() == null || dto.getNumeroDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de documento es obligatorio");
        }
        if (!dto.getNumeroDocumento().trim().matches("\\d+")) {
            throw new IllegalArgumentException("El numero de documento debe contener solo digitos");
        }
        if (dto.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        // La vigencia ya no es obligatoria en el request: se determina automáticamente según la edad.
    }

    /**
     * Determina la vigencia (en años) según la edad del titular:
     * - Menores de 21: 1 año la primera vez, 3 años las siguientes
     * - Hasta 46 años: 5 años
     * - Hasta 60 años: 4 años
     * - Hasta 70 años: 3 años
     * - Mayores de 70: 1 año
     */
    private int calcularVigenciaPorEdad(int edad, boolean esPrimeraVez) {
        if (edad < 21) return esPrimeraVez ? 1 : 3;
        if (edad <= 46) return 5;
        if (edad <= 60) return 4;
        if (edad <= 70) return 3;
        return 1;
    }

    /**
     * El día y mes del vencimiento coinciden con la fecha de nacimiento, y la
     * vigencia se cuenta completa a partir de la fecha de inicio (hoy): el
     * vencimiento es el primer cumpleaños en o posterior a (inicio + vigencia años).
     * (withYear ajusta automáticamente el 29/02 a 28/02 en años no bisiestos)
     */
    private LocalDate calcularFechaVencimiento(LocalDate fechaNacimiento, LocalDate inicio, int vigencia) {
        LocalDate base = inicio.plusYears(vigencia);
        LocalDate cumpleEnBase = fechaNacimiento.withYear(base.getYear());
        return cumpleEnBase.isBefore(base)
                ? fechaNacimiento.withYear(base.getYear() + 1)
                : cumpleEnBase;
    }

    /**
     * Fecha de vencimiento de una licencia ya persistida. Usa el campo almacenado
     * y, para registros antiguos sin fechaVencimiento, cae al cálculo fechaEmisión + vigencia.
     */
    private LocalDate obtenerFechaVencimiento(Licencia l) {
        return l.getFechaVencimiento() != null
                ? l.getFechaVencimiento()
                : l.getFechaEmision().toLocalDate().plusYears(l.getVigencia());
    }

    private void normalizarCampos(EmitirLicenciaDTO dto) {
        dto.setTitular(dto.getTitular().trim());
        dto.setNumeroDocumento(dto.getNumeroDocumento().trim());
        dto.setClase(dto.getClase().trim().toUpperCase());

        if (dto.getObservaciones() != null) {
            dto.setObservaciones(dto.getObservaciones().trim());
        }
    }

    private void validarEdadContraFechaNacimiento(EmitirLicenciaDTO dto) {
        LocalDate fechaNacimiento = dto.getFechaNacimiento();
        int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();

        if (!dto.getEdad().equals(edadCalculada)) {
            throw new IllegalArgumentException("La edad no coincide con la fecha de nacimiento");
        }
    }

    private void validarPorClase(EmitirLicenciaDTO dto) {
        String clase = dto.getClase();
        Integer edad = dto.getEdad();

        if (esClaseProfesional(clase)) {
            validarClaseProfesional(dto, edad);
            return;
        }

        validarClaseNoProfesional(edad);
    }

    private void validarClaseProfesional(EmitirLicenciaDTO dto, Integer edad) {
        if (edad < 21) {
            throw new IllegalArgumentException(
                    "Para obtener licencia de clase " + dto.getClase() +
                            " debe tener minimo 21 anos (edad actual: " + edad + ")");
        }

        if (dto.getPoseeLicenciaB() == null || !dto.getPoseeLicenciaB()) {
            throw new IllegalArgumentException(
                    "Para obtener licencia de clase " + dto.getClase() +
                            " debe poseer licencia clase B");
        }

        if (dto.getAntiguedadLicenciaBEnAnios() == null || dto.getAntiguedadLicenciaBEnAnios() < 1) {
            throw new IllegalArgumentException(
                    "La licencia clase B debe tener minimo 1 ano de antiguedad " +
                            "(antiguedad actual: " +
                            (dto.getAntiguedadLicenciaBEnAnios() != null ? dto.getAntiguedadLicenciaBEnAnios() : 0) +
                            " ano/s)");
        }

        boolean tieneLicenciaProfesionalAnterior = dto.getTieneLicenciaProfesionalAnterior() != null
                && dto.getTieneLicenciaProfesionalAnterior();

        if (!tieneLicenciaProfesionalAnterior && edad > 65) {
            throw new IllegalArgumentException(
                    "No puede obtener licencia profesional por primera vez siendo mayor de 65 anos " +
                            "(edad actual: " + edad + ")");
        }
    }

    private void validarClaseNoProfesional(Integer edad) {
        if (edad < 17) {
            throw new IllegalArgumentException(
                    "Para obtener licencia debe tener minimo 17 anos (edad actual: " + edad + ")");
        }
    }

    private boolean esClaseProfesional(String clase) {
        return clase.equals("C") || clase.equals("D") || clase.equals("E");
    }

    private String obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SISTEMA";
    }

    public List<Licencia> listarVigentes(String nombreApellido, String grupoSanguineo, String factorRH, Boolean donanteOrganos) {
        // Vigente = registro activo (no reemplazado) y además no vencida.
        return filtrarLicencias(repository.findByVigenteTrue(), nombreApellido, grupoSanguineo, factorRH, donanteOrganos)
                .stream()
                .filter(l -> !estaExpirada(l))
                .collect(Collectors.toList());
    }

    public List<Licencia> listarHistorial(String nombreApellido, String grupoSanguineo, String factorRH, Boolean donanteOrganos) {
        // Historial = reemplazadas (vigente=false) + las activas que ya vencieron.
        Stream<Licencia> reemplazadas = filtrarLicencias(
                repository.findByVigenteFalse(), nombreApellido, grupoSanguineo, factorRH, donanteOrganos).stream();
        Stream<Licencia> vencidas = filtrarLicencias(
                repository.findByVigenteTrue(), nombreApellido, grupoSanguineo, factorRH, donanteOrganos).stream()
                .filter(this::estaExpirada);
        return Stream.concat(reemplazadas, vencidas).collect(Collectors.toList());
    }

    private boolean estaExpirada(Licencia l) {
        if (l.getVigencia() == null || l.getFechaEmision() == null) {
            return false;
        }
        return obtenerFechaVencimiento(l).isBefore(LocalDate.now());
    }

    private List<Licencia> filtrarLicencias(List<Licencia> base, String nombreApellido, String grupoSanguineo,
            String factorRH, Boolean donanteOrganos) {
        GrupoSanguineo gs = (grupoSanguineo != null && !grupoSanguineo.isEmpty())
                ? GrupoSanguineo.valueOf(grupoSanguineo) : null;
        FactorRH frh = (factorRH != null && !factorRH.isEmpty())
                ? FactorRH.valueOf(factorRH) : null;
        String nombre = (nombreApellido != null && !nombreApellido.trim().isEmpty())
                ? nombreApellido.trim().toLowerCase() : null;

        return base.stream()
                .filter(l -> nombre == null || l.getTitular().toLowerCase().contains(nombre))
                .filter(l -> gs == null || gs.equals(l.getGrupoSanguineo()))
                .filter(l -> frh == null || frh.equals(l.getFactorRH()))
                .filter(l -> donanteOrganos == null || donanteOrganos.equals(l.getDonanteOrganos()))
                .collect(Collectors.toList());
    }

    public List<Licencia> listar() {
        return repository.findAll();
    }

    public List<Licencia> listarPorDocumento(String numeroDocumento) {
        return repository.findByNumeroDocumento(numeroDocumento);
    }

    public Licencia buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Licencia no encontrada con ID: " + id));
    }

    public List<Licencia> listarExpiradas(LocalDate desde, LocalDate hasta) {
        LocalDate hoy = LocalDate.now();
        return repository.findAll().stream()
                .filter(l -> l.getVigencia() != null && l.getFechaEmision() != null)
                .filter(l -> {
                    LocalDate vencimiento = obtenerFechaVencimiento(l);
                    if (!vencimiento.isBefore(hoy)) return false;
                    if (desde != null && vencimiento.isBefore(desde)) return false;
                    if (hasta != null && vencimiento.isAfter(hasta)) return false;
                    return true;
                })
                .toList();
    }

    @Transactional
    public Licencia emitirCopia(Long id) {
        Licencia original = buscarPorId(id);

        Licencia copia = new Licencia();
        copia.setTitular(original.getTitular());
        copia.setEdad(original.getEdad());
        copia.setNumeroDocumento(original.getNumeroDocumento());
        copia.setFechaNacimiento(original.getFechaNacimiento());
        copia.setClase(original.getClase());
        copia.setObservaciones(original.getObservaciones());
        copia.setVigencia(original.getVigencia());
        copia.setCosto(50.0);
        copia.setFechaEmision(LocalDateTime.now());
        copia.setUsuarioAdministrativo(obtenerUsuarioActual());

        Licencia saved = repository.save(copia);
        repository.flush();
        return saved;
    }

}
