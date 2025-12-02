package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequest request) {
        Venta venta = ventaService.crearVenta(request);
        return ResponseEntity.ok(venta);
    }
}
