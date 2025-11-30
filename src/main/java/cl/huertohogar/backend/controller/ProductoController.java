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

    @GetMapping
    public List<Producto> getAll() {
        return productoService.getAll();
    }
    @PatchMapping("/{id}/precio")
    public Producto updatePrecio(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body
    ) {
        Double nuevoPrecio = Double.valueOf(body.get("precio").toString());
        return productoService.updatePrecio(id, nuevoPrecio);
    }



    @PostMapping
    public Producto create(@RequestBody Producto producto) {
        return productoService.create(producto);
    }

    @PutMapping("/{id}")
    public Producto update(
            @PathVariable Long id,
            @RequestBody Producto productoUpdate
    ) {
        return productoService.update(id, productoUpdate);
    }

    @PatchMapping("/{id}/stock")
    public Producto updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequest request
    ) {
        return productoService.updateStock(id, request.getStock());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}
