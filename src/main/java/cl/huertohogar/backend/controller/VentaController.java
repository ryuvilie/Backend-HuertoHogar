package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    // ✅ VENDEDOR / ADMIN
    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.crearVenta(request));
    }

    // ✅ VENDEDOR / ADMIN
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    // ✅ VENDEDOR / ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<Venta> editarVenta(
            @PathVariable Long id,
            @RequestBody VentaRequest request
    ) {
        return ResponseEntity.ok(ventaService.editarVenta(id, request));
    }
}
