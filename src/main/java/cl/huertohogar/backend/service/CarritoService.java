package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.DetalleCarritoDTO;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepo;
    private final ItemCarritoRepository itemRepo;
    private final ProductoRepository productoRepo;

    // 🆕 Repos de ventas (los necesito para completar)
    private final VentaRepository ventaRepo;
    private final DetalleVentaRepository detalleVentaRepo;

    // ----------------------------------------------------
    // 🟩 Obtener o crear el único carrito del sistema
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // 🟩 Agregar producto al carrito
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // 🟩 Obtener carrito (DTO para Android)
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // 🟩 Eliminar ítem individual
    // ----------------------------------------------------
    public void removeItem(Long idItem) {
        itemRepo.deleteById(idItem);
    }

    // ----------------------------------------------------
    // 🟩 Vaciar carrito completo
    // ----------------------------------------------------
    public void clearCarrito() {
        Carrito carrito = getOrCreateCarrito();
        itemRepo.deleteAllByCarrito(carrito);
        carrito.setTotal(0.0);
        carritoRepo.save(carrito);
    }

    // ----------------------------------------------------
    // 🟩 Actualizar total del carrito
    // ----------------------------------------------------
    private void actualizarTotal(Carrito carrito) {
        double total = itemRepo.findByCarrito(carrito)
                .stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();

        carrito.setTotal(total);
        carritoRepo.save(carrito);
    }

    // =====================================================
    // 🟦 NUEVO: FINALIZAR COMPRA (Checkout real)
    // =====================================================
    @Transactional
    public Long finalizarCompra() {

        Carrito carrito = getOrCreateCarrito();
        List<ItemCarrito> items = itemRepo.findByCarrito(carrito);

        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 1️⃣ Crear venta
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setTotal(carrito.getTotal());
        venta = ventaRepo.save(venta);

        // 2️⃣ Crear detalle_venta + descontar stock
        for (ItemCarrito item : items) {

            Producto prod = item.getProducto();

            // Validar stock
            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            // Descontar stock
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // Crear detalle_venta
            DetalleVenta dv = new DetalleVenta();
            dv.setVenta(venta);
            dv.setProducto(prod);
            dv.setCantidad(item.getCantidad());
            dv.setSubtotal(item.getSubtotal());
            detalleVentaRepo.save(dv);
        }

        // 3️⃣ Vaciar carrito
        itemRepo.deleteAllByCarrito(carrito);
        carrito.setTotal(0.0);
        carritoRepo.save(carrito);

        // 4️⃣ Devolver ID de la venta
        return venta.getId_venta();
    }
}
