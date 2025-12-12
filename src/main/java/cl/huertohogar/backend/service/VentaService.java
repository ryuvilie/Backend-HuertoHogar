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

    // 🟢 Crear venta (manual / vendedor)
    public Venta crearVenta(VentaRequest request) {

        Venta venta = new Venta();
        venta.setFecha(request.getFecha());
        double total = 0.0;

        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            total += prod.getPrecio() * item.getCantidad();
        }

        venta.setTotal(total);
        return ventaRepo.save(venta);
    }

    // 🟢 Listar ventas
    public List<Venta> listarVentas() {
        return ventaRepo.findAll();
    }

    // 🟢 Editar venta (recalcula total)
    public Venta editarVenta(Long id, VentaRequest request) {

        Venta venta = ventaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no existe"));

        venta.setFecha(request.getFecha());
        double total = 0.0;

        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            total += prod.getPrecio() * item.getCantidad();
        }

        venta.setTotal(total);
        return ventaRepo.save(venta);
    }
}
