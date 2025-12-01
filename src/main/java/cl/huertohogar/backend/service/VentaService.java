package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Venta crearVenta(VentaRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());

        // Usuario asociado (opcional)
        if (request.getIdUsuario() != null) {
            Usuario u = usuarioRepo.findById(request.getIdUsuario())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Usuario no encontrado"
                    ));

            if (!u.isEnabled()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El usuario está desactivado"
                );
            }

            venta.setUsuario(u);
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        double total = 0.0;

        for (VentaRequest.ItemCarrito item : request.getItems()) {

            Producto prod = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Producto no encontrado (ID: " + item.getIdProducto() + ")"
                    ));

            // VALIDACIÓN DE PRODUCTO ACTIVO
            if (!prod.isActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El producto está desactivado: " + prod.getNombre()
                );
            }

            if (prod.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para: " + prod.getNombre()
                );
            }

            // Actualizar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // Crear detalle
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

        Venta vGuardada = ventaRepo.save(venta);
        detalleRepo.saveAll(detalles);

        return vGuardada;
    }

    public Venta getVentaById(Long idVenta) {
        return ventaRepo.findById(idVenta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Venta no encontrada"));
    }

    public List<Venta> getAllVentas() {
        return ventaRepo.findAll();
    }
}
