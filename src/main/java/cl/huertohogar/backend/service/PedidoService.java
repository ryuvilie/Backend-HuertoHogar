package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import cl.huertohogar.backend.model.DetallePedido;
import cl.huertohogar.backend.model.Pedido;
import cl.huertohogar.backend.repository.PedidoRepository;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {

        // 1) Validar que el pedido tenga detalles
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido debe contener al menos un detalle."
            );
        }

        // 2) Calcular el total como suma de subtotales
        double total = 0.0;

        for (DetallePedido detalle : pedido.getDetalles()) {

            // Si por alguna razón viene subtotal nulo, lo calculamos aquí
            if (detalle.getSubtotal() == null) {

                if (detalle.getProducto() == null ||
                    detalle.getProducto().getPrecio() == null ||
                    detalle.getCantidad() == null) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Cada detalle debe tener producto, cantidad y precio para calcular el total."
                    );
                }

                double subtotal = detalle.getProducto().getPrecio() * detalle.getCantidad();
                detalle.setSubtotal(subtotal);
            }

            total += detalle.getSubtotal();
        }

        // 3) Asignar el total calculado al pedido (ignorando lo que venga del front)
        pedido.setTotal(total);

        // 4) Guardar el pedido
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
