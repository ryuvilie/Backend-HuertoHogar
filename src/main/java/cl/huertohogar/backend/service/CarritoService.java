package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.DetalleCarritoDTO;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepo;
    private final ItemCarritoRepository itemRepo;
    private final ProductoRepository productoRepo;

    // Obtener o crear el único carrito
    public Carrito getOrCreateCarrito() {

        return carritoRepo.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setTotal(0.0);
                    return carritoRepo.save(nuevo);
                });
    }

    // Agregar producto
    public void addToCart(Long idProducto, int cantidad) {

        Carrito carrito = getOrCreateCarrito();

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        double subtotal = producto.getPrecio() * cantidad;

        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        item.setSubtotal(subtotal);

        itemRepo.save(item);

        actualizarTotal(carrito);
    }

    // Obtener carrito como DTO
    public List<DetalleCarritoDTO> getCarrito() {

        Carrito carrito = getOrCreateCarrito();

        return itemRepo.findByCarrito(carrito)
                .stream()
                .map(item -> new DetalleCarritoDTO(
                        item.getId_item(),
                        item.getCantidad(),
                        item.getSubtotal(),
                        item.getProducto()
                ))
                .toList();
    }

    // Eliminar item
    public void removeItem(Long idItem) {
        itemRepo.deleteById(idItem);
    }

    // Vaciar carrito
    public void clearCarrito() {
        Carrito carrito = getOrCreateCarrito();
        itemRepo.deleteAllByCarrito(carrito);
        carrito.setTotal(0.0);
        carritoRepo.save(carrito);
    }

    // Actualizar total
    private void actualizarTotal(Carrito carrito) {
        double total = itemRepo.findByCarrito(carrito)
                .stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();

        carrito.setTotal(total);
        carritoRepo.save(carrito);
    }
}
