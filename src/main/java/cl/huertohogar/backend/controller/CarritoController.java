package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.CheckoutResponseDTO;
import cl.huertohogar.backend.dto.DetalleCarritoDTO;
import cl.huertohogar.backend.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestParam Long idProducto,
            @RequestParam(defaultValue = "1") int cantidad
    ) {
        carritoService.addToCart(idProducto, cantidad);
        return ResponseEntity.ok("Producto agregado");
    }

    @GetMapping
    public List<DetalleCarritoDTO> getCarrito() {
        return carritoService.getCarrito();
    }

    @DeleteMapping("/item/{idItem}")
    public ResponseEntity<?> deleteItem(@PathVariable Long idItem) {
        carritoService.removeItem(idItem);
        return ResponseEntity.ok("Item eliminado");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clear() {
        carritoService.clearCarrito();
        return ResponseEntity.ok("Carrito vaciado");
    }
    // 5️⃣ FINALIZAR COMPRA → genera venta y descuenta stock
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout() {
    Long idVenta = carritoService.finalizarCompra();

    CheckoutResponseDTO response = new CheckoutResponseDTO(
            idVenta,
            "Compra realizada exitosamente"
    );

    return ResponseEntity.ok(response);
    }

}
