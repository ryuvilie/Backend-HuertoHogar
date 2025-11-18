package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import cl.huertohogar.backend.model.DetallePedido;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.repository.DetallePedidoRepository;
import cl.huertohogar.backend.repository.ProductoRepository;


@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public List<DetallePedido> listar() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }
    @Transactional
    public DetallePedido guardar(DetallePedido detalle) {

    // 1) Validar que venga un producto
    if (detalle.getProducto() == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debe indicar un producto para el detalle del pedido."
        );
    }

    // 2) Obtener el ID del producto que llega en el JSON
    Long idProducto = detalle.getProducto().getId_producto();

    if (idProducto == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El id del producto es obligatorio."
        );
    }

    // 3) Buscar el producto real en la base de datos
    Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El producto con id " + idProducto + " no existe."
            ));

    // 4) Validar cantidad
    Integer cantidadSolicitada = detalle.getCantidad();
    if (cantidadSolicitada == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad es obligatoria."
        );
    }

    if (cantidadSolicitada <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad debe ser mayor a cero."
        );
    }

    // 5) Validar stock suficiente
    Integer stockDisponible = producto.getStock();
    if (stockDisponible == null || stockDisponible < cantidadSolicitada) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock suficiente para el producto '" +
                        producto.getNombre() + "'. Disponible: " +
                        stockDisponible + ", solicitado: " + cantidadSolicitada + "."
        );
    }

    // 6) Calcular subtotal (precio * cantidad) en el backend
    Double precioUnitario = producto.getPrecio();
    if (precioUnitario == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El producto no tiene un precio válido configurado."
        );
    }
    Double subtotal = precioUnitario * cantidadSolicitada;
    detalle.setSubtotal(subtotal);

    // 7) Descontar stock del producto
    producto.setStock(stockDisponible - cantidadSolicitada);
    productoRepository.save(producto);

    // 8) Asociar el producto real al detalle
    detalle.setProducto(producto);

    // 9) Guardar el detalle
    return detallePedidoRepository.save(detalle);
    }



    public void eliminar(Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
