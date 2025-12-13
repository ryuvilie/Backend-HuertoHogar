package cl.huertohogar.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.dto.UpdateStockRequest;
import cl.huertohogar.backend.dto.ComentarioRequestDTO;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // 📦 LISTAR TODOS (público)
    @GetMapping
    public List<Producto> getAll() {
        return productoService.getAll();
    }

    // 🔎 OBTENER PRODUCTO POR ID (DETALLE) ← 🔥 ESTO SOLUCIONA TU ERROR
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        Producto producto = productoService.getById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    // ➕ CREAR (ADMIN)
    @PostMapping
    public Producto create(@RequestBody Producto producto) {
        return productoService.create(producto);
    }

    // ✏️ ACTUALIZAR (ADMIN)
    @PutMapping("/{id}")
    public Producto update(
            @PathVariable Long id,
            @RequestBody Producto productoUpdate
    ) {
        return productoService.update(id, productoUpdate);
    }

    // 📦 ACTUALIZAR STOCK (ADMIN)
    @PatchMapping("/{id}/stock")
    public Producto updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequest request
    ) {
        return productoService.updateStock(id, request.getStock());
    }

    // 💲 ACTUALIZAR PRECIO (ADMIN)
    @PatchMapping("/{id}/precio")
    public Producto updatePrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Double nuevoPrecio = Double.valueOf(body.get("precio").toString());
        return productoService.updatePrecio(id, nuevoPrecio);
    }

    // ⭐ COMENTARIOS — SOLO CLIENTE (NO se guarda en BD)
    @PostMapping("/{id}/comentario")
    public ResponseEntity<?> comentarProducto(
            @PathVariable Long id,
            @RequestBody ComentarioRequestDTO req
    ) {
        if (req.getTexto() == null || req.getTexto().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El texto no puede estar vacío");
        }

        if (req.getNota() == null || req.getNota() < 1 || req.getNota() > 5) {
            return ResponseEntity.badRequest().body("La nota debe estar entre 1 y 5");
        }

        return ResponseEntity.ok("Comentario recibido");
    }

    // ❌ ELIMINAR (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}
