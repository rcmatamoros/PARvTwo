package hn.unah.proyecto.controladores;

import hn.unah.proyecto.dtos.PrestamosDTO;
import hn.unah.proyecto.modelos.Prestamos;
import hn.unah.proyecto.servicios.PrestamoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoServicio prestamoServicio;

    @PostMapping
    @Operation(summary = "Crear un nuevo préstamo", responses = {@ApiResponse(description = "Préstamo creado exitosamente")})
    public String crearPrestamo(@RequestBody Prestamos prestamo2) {
        return prestamoServicio.crearPrestamo(prestamo2);
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar préstamos por DNI", responses = {@ApiResponse(description = "Lista de préstamos")})
    public List<PrestamosDTO> buscarPrestamosPorDni(@PathVariable String dni) {
        return prestamoServicio.buscarPrestamosPorDni(dni);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar préstamo por ID", responses = {@ApiResponse(description = "Préstamo encontrado")})
    public Optional<PrestamosDTO> buscarPrestamoPorId(@PathVariable int id) {
        return prestamoServicio.buscarPrestamoPorId(id);
    }

    @PutMapping("/{idPrestamo}/{dniCliente}")
    @Operation(summary = "Asociar préstamo a cliente", responses = {@ApiResponse(description = "Préstamo asociado al cliente")})
    public void asociarPrestamoACliente(@PathVariable int idPrestamo, @PathVariable String dniCliente) {
        prestamoServicio.asociarPrestamoACliente(idPrestamo, dniCliente);
    }

    @GetMapping("/saldo/{idPrestamo}")
    @Operation(summary = "Obtener saldo pendiente de un préstamo", responses = {@ApiResponse(description = "Saldo pendiente")})
    public double obtenerSaldoPendiente(@PathVariable int idPrestamo) {
        return prestamoServicio.obtenerSaldoPendiente(idPrestamo);
    }

    @PutMapping("/pagar/{idPrestamo}")
    @Operation(summary = "Pagar cuota del préstamo", responses = {@ApiResponse(description = "Cuota pagada")})
    public void pagarCuota(@PathVariable int idPrestamo) {
        prestamoServicio.pagarCuota(idPrestamo);
    }
}


/*package hn.unah.proyecto.controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.proyecto.dtos.PrestamosDTO;
import hn.unah.proyecto.modelos.Cliente;
import hn.unah.proyecto.modelos.Prestamos;
import hn.unah.proyecto.servicios.PrestamoServicio;

@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoServicio prestamoServicio;

    public String crearPrestamo(PrestamosDTO prestamoDTO) {
        return "";
    }

    private double calcularCuota(Prestamos prestamo) {
        return 0;
    }
    private double calcularTotalEgresos(Cliente cliente) {
        return cliente;
    }
    private void insertarCuotasEnTablaAmortizacion(Prestamos prestamo) {

    }
    public String pagarCuota(int idPrestamo) {
        return "";
    }
    public double obtenerSaldoPendiente(int idPrestamo) {
        return idPrestamo;
     }
    public String asociarPrestamoACliente(int idPrestamo, String dni) {
        return "";
    }
    public List<PrestamosDTO> buscarPrestamosPorDni(String dni) {
    return ;
    } 
    public Optional<PrestamosDTO> buscarPrestamoPorId(int id) {

    }

}
*/