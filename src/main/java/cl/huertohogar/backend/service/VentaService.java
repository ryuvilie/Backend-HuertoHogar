package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.DetalleVentaRequest;
import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.DetalleVenta;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.repository.ProductoRepository;
import cl.huertohogar.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    // Obtener todas las ventas
    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    // Obtener por ID
    public Venta getVentaById(Long idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    // Crear venta real desde frontend
    public Venta crearVenta(VentaRequest request) {

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setTotal(request.getTotal());

        // Detalles
        List<DetalleVenta> detalles = request.getDetalles().stream().map(d -> {
            DetalleVenta det = new DetalleVenta();

            Producto producto = productoRepository.findById(d.getId_producto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            det.setVenta(venta);
            det.setProducto(producto);
            det.setCantidad(d.getCantidad());
            det.setSubtotal(d.getSubtotal());

            return det;
        }).collect(Collectors.toList());

        venta.setDetalles(detalles);

        return ventaRepository.save(venta);
    }
}
