package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;
    private final DetalleVentaRepository detalleRepo;

    // 🔥 Crear venta a partir del carrito
    public Venta crearVenta(VentaRequest request) {

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());

        // 🟢 Si viene idUsuario → asociar usuario
        if (request.getIdUsuario() != null) {
            Usuario u = usuarioRepo.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            venta.setUsuario(u);
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        double total = 0.0;

        // 🔥 Recorrer los items del carrito
        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // 🔍 Validar stock
            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            // ➖ Restar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // 🧾 Crear detalle
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

        // 💾 Guardar venta + detalles
        Venta vGuardada = ventaRepo.save(venta);
        detalleRepo.saveAll(detalles);

        return vGuardada;
    }

    public Venta getVentaById(Long idVenta) {
        return ventaRepo.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    public List<Venta> getAllVentas() {
        return ventaRepo.findAll();
    }
}
