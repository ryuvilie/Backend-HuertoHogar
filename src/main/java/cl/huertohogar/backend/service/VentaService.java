package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.DetalleVenta;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.repository.DetalleVentaRepository;
import cl.huertohogar.backend.repository.ProductoRepository;
import cl.huertohogar.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepo;
    private final ProductoRepository productoRepo;
    private final DetalleVentaRepository detalleRepo;

    // Crear venta
    @Transactional
    public Venta crearVenta(VentaRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("No se enviaron productos");
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());

        double total = 0.0;
        List<DetalleVenta> detalles = new ArrayList<>();

        // Recorrer items del carrito
        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validación de stock
            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            // Descontar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // Crear detalle de venta
            DetalleVenta det = new DetalleVenta();
            det.setProducto(prod);
            det.setCantidad(item.getCantidad());
            det.setSubtotal(prod.getPrecio() * item.getCantidad());
            det.setVenta(venta);

            detalles.add(det);
            total += det.getSubtotal();
        }

        venta.setTotal(total);
        venta.setDetalles(detalles);

        // Guardar venta y detalles
        Venta vGuardada = ventaRepo.save(venta);
        detalleRepo.saveAll(detalles);

        return vGuardada;
    }

    // Obtener todas las ventas
    public List<Venta> getAllVentas() {
        return ventaRepo.findAll();
    }

    // Obtener venta por ID
    public Venta getVentaById(Long idVenta) {
        return ventaRepo.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }
}
