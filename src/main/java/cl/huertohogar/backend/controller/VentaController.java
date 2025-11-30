package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.VentaDTO;
import cl.huertohogar.backend.dto.VentaRequest;
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

    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@RequestBody VentaRequest request) {
        Venta venta = ventaService.crearVenta(request);
        return ResponseEntity.ok(new VentaDTO(venta));
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> getAllVentas() {
        return ResponseEntity.ok(
                ventaService.getAllVentas()
                        .stream()
                        .map(VentaDTO::new)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> getVentaById(@PathVariable Long id) {
        return ResponseEntity.ok(new VentaDTO(ventaService.getVentaById(id)));
    }
}
