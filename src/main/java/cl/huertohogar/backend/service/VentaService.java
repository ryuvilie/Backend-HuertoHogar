package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepo;
    private final ProductoRepository productoRepo;

    // Crear venta a partir del carrito
    public Venta crearVenta(VentaRequest request) {

        // Crear la venta
        Venta venta = new Venta();
        venta.setFecha(request.getFecha());
        double total = 0.0;

        // Recorrer los items del carrito
        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar stock
            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            // Descontar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // Calcular el total
            total += prod.getPrecio() * item.getCantidad();
        }

        venta.setTotal(total);

        // Guardar la venta
        return ventaRepo.save(venta);
    }
}
