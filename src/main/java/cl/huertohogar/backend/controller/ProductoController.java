package cl.huertohogar.backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import cl.huertohogar.backend.dto.UpdateStockRequest;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // 🔹 Catálogo público: solo activos
    @GetMapping
    public List<Producto> getAll() {
        return productoService.getAll();
    }

    // 🔹 Vista completa para admin
    @GetMapping("/admin")
    public List<Producto> getAllAdmin() {
        return productoService.getAllIncluyendoInactivos();
    }

    // 🔹 Crear producto
    @PostMapping
    public Producto create(@RequestBody Producto producto) {
        return productoService.create(producto);
    }

    // 🔹 Editar producto (nombre, descripción, img, stock, etc.)
    @PutMapping("/{id}")
    public Producto update(
            @PathVariable Long id,
            @RequestBody Producto productoUpdate
    ) {
        return productoService.update(id, productoUpdate);
    }

    // 🔹 Editar stock
    @PatchMapping("/{id}/stock")
    public Producto updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequest request
    ) {
        return productoService.updateStock(id, request.getStock());
    }

    // 🔹 Editar precio
    @PatchMapping("/{id}/precio")
    public Producto updatePrecio(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body
    ) {
        Double nuevoPrecio = Double.valueOf(body.get("precio").toString());
        return productoService.updatePrecio(id, nuevoPrecio);
    }

    // 🔥 DESACTIVAR producto (NO se borra)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok("Producto desactivado correctamente");
    }

    // 🔥 REACTIVAR producto (si fuese necesario)
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        productoService.activar(id);
        return ResponseEntity.ok("Producto activado nuevamente");
    }
}
