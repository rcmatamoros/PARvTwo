package hn.unah.proyecto.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hn.unah.proyecto.ModelMapper.ModelMapperSingleton;
import hn.unah.proyecto.dtos.PrestamosDTO;
import hn.unah.proyecto.modelos.Cliente;
import hn.unah.proyecto.modelos.Prestamos;
import hn.unah.proyecto.modelos.TablaAmortizacion;
import hn.unah.proyecto.repositorios.ClienteRepositorio;
import hn.unah.proyecto.repositorios.PrestamosRepositorio;

@Service
public class PrestamoServicio {

    @Autowired
    private PrestamosRepositorio prestamosRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    private ModelMapper modelMapper = ModelMapperSingleton.getInstancia();
    

    @Value("${prestamo.personal}")
    private double tasaPersonal;

    @Value("${prestamo.vehicular}")
    private double tasaVehicular;

    @Value("${prestamo.hipotecario}")
    private double tasaHipotecario;

    public String crearPrestamo(Prestamos prestamo2) {
        Optional<Cliente> clienteOpt = clienteRepositorio.findById(prestamo2.getListaCliente().get(0).getDni());

        if (!clienteOpt.isPresent()) {
            return "Cliente no encontrado";
        }

        Cliente cliente = clienteOpt.get();

        if (prestamo2.getPlazo() < 1) {
            return "El plazo mínimo para un préstamo es de 1 año";
        }

        double totalEgresos = calcularTotalEgresos(cliente);
        double nivelEndeudamiento = totalEgresos / cliente.getSueldo();

        if (nivelEndeudamiento > 0.40) {
            return "El nivel de endeudamiento del cliente supera el 40% de su sueldo";
        }

        Prestamos prestamo = modelMapper.map(prestamo2, Prestamos.class);

       
        switch (prestamo2.getTipoPrestamo()) {
            case 'P': 
                prestamo.setTasaInteres(tasaPersonal);
                break;
            case 'V':
                prestamo.setTasaInteres(tasaVehicular);
                break;
            case 'H': 
                prestamo.setTasaInteres(tasaHipotecario);
                break;
            default:
                return "Tipo de préstamo inválido";
        }

        double cuota = calcularCuota(prestamo);
        prestamo.setCuota(cuota);

        prestamo.getListaCliente().add(cliente);
        cliente.getListaPrestamos().add(prestamo);

        prestamosRepositorio.save(prestamo);
        clienteRepositorio.save(cliente);

        insertarCuotasEnTablaAmortizacion(prestamo);

        return "Préstamo creado exitosamente";
    }

    private double calcularCuota(Prestamos prestamo) {
        double r = prestamo.getTasaInteres() / 12 / 100;
        int n = prestamo.getPlazo() * 12; 
        double P = prestamo.getMonto(); 

        return (P * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    }

    private double calcularTotalEgresos(Cliente cliente) {
        double totalEgresos = 0;

        for (Prestamos prestamo : cliente.getListaPrestamos()) {
            totalEgresos += prestamo.getCuota();
        }

        return totalEgresos;
    }

    private void insertarCuotasEnTablaAmortizacion(Prestamos prestamo) {
        double saldoPendiente = prestamo.getMonto();
        double r = prestamo.getTasaInteres() / 12 / 100;
        int n = prestamo.getPlazo() * 12;

        for (int i = 1; i <= n; i++) {
            double interes = saldoPendiente * r;
            double capital = prestamo.getCuota() - interes;

            saldoPendiente -= capital;

            TablaAmortizacion cuota = new TablaAmortizacion();
            cuota.setNumeroCuota(i);
            cuota.setInteres(interes);
            cuota.setCapital(capital);
            cuota.setSaldo(saldoPendiente);
            cuota.setFechaVencimiento(LocalDate.now().plusMonths(i));
            cuota.setEstado('P'); 

            cuota.setPrestamo(prestamo);
            prestamo.getListaAmortizacion().add(cuota);
        }

        prestamosRepositorio.save(prestamo);
    }
    public String pagarCuota(int idPrestamo) {
        Optional<Prestamos> prestamoOpt = prestamosRepositorio.findById(idPrestamo);
    
        if (!prestamoOpt.isPresent()) {
            return "Préstamo no encontrado";
        }
    
        Prestamos prestamo = prestamoOpt.get();
    
        // Buscar la cuota más antigua pendiente
        TablaAmortizacion cuotaPendiente = prestamo.getListaAmortizacion().stream()
            .filter(tabla -> tabla.getEstado() == 'P')  
            .min(Comparator.comparing(TablaAmortizacion::getFechaVencimiento)).orElse(null);
    
        if (cuotaPendiente == null) {
            return "No hay cuotas pendientes";
        }
    
        cuotaPendiente.setEstado('A');  
        prestamosRepositorio.save(prestamo);
    
        return "Cuota pagada correctamente";
    }

    public double obtenerSaldoPendiente(int idPrestamo) {
        Optional<Prestamos> prestamoOpt = prestamosRepositorio.findById(idPrestamo);
    
        if (!prestamoOpt.isPresent()) {
            return 0.0; 
        }
    
        Prestamos prestamo = prestamoOpt.get();
        double saldoPendiente = 0.0;
    
        for (TablaAmortizacion tabla : prestamo.getListaAmortizacion()) {
            if (tabla.getEstado() == 'P') { 
                saldoPendiente += tabla.getSaldo();
            }
        }
    
        return saldoPendiente;
    }

    public String asociarPrestamoACliente(int idPrestamo, String dni) {
        Optional<Prestamos> prestamoOpt = prestamosRepositorio.findById(idPrestamo);
        Optional<Cliente> clienteOpt = clienteRepositorio.findById(dni);
    
        if (!prestamoOpt.isPresent()) {
            return "Préstamo no encontrado";
        }
    
        if (!clienteOpt.isPresent()) {
            return "Cliente no encontrado";
        }
    
        Cliente cliente = clienteOpt.get();
        Prestamos prestamo = prestamoOpt.get();
    
        if (prestamo.getListaCliente().contains(cliente)) {
            return "El préstamo ya está asociado a este cliente";
        }
    
        prestamo.getListaCliente().add(cliente);
        cliente.getListaPrestamos().add(prestamo);
    
        prestamosRepositorio.save(prestamo);
        clienteRepositorio.save(cliente);
    
        return "Préstamo asociado correctamente";
    }

    public List<PrestamosDTO> buscarPrestamosPorDni(String dni) {
    Optional<Cliente> clienteOpt = clienteRepositorio.findById(dni);
    
    if (!clienteOpt.isPresent()) {
        return Collections.emptyList();
    }
    
    Cliente cliente = clienteOpt.get();
    List<Prestamos> prestamos = cliente.getListaPrestamos();
    List<PrestamosDTO> prestamosDTO = new ArrayList<>();

    for (Prestamos prestamo : prestamos) {
        prestamosDTO.add(modelMapper.map(prestamo, PrestamosDTO.class));
    }

    return prestamosDTO;
    }
    
    public Optional<PrestamosDTO> buscarPrestamoPorId(int id) {
        Optional<Prestamos> prestamoOpt = prestamosRepositorio.findById(id);
        
        if (prestamoOpt.isPresent()) {
            return Optional.of(modelMapper.map(prestamoOpt.get(), PrestamosDTO.class));
        }
        
        return Optional.empty();  // No se encontró el préstamo
    }
}
