package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.VentaDTO;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    // Endpoint para obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<VentaDTO>> getAllVentas() {
        List<Venta> ventas = ventaService.getAllVentas();
        List<VentaDTO> ventaDTOs = ventas.stream()
                .map(VentaDTO::new)  // Convertir cada Venta a VentaDTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(ventaDTOs);
    }

    // Endpoint para obtener una venta por ID (si deseas filtrar por ID)
    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> getVentaById(@PathVariable Long idVenta) {
        Venta venta = ventaService.getVentaById(idVenta);
        return ResponseEntity.ok(new VentaDTO(venta));
    }
}
