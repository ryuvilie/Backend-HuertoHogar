package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.DetalleCarritoDTO;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepo;
    private final ItemCarritoRepository itemRepo;
    private final ProductoRepository productoRepo;

    // Repos de ventas
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
    @Transactional
    public void addToCart(Long idProducto, int cantidad) {

        Carrito carrito = getOrCreateCarrito();

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        if (!producto.isActivo()) {
            throw new RuntimeException("El producto está desactivado y no se puede agregar al carrito");
        }

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
                .filter(item -> item.getProducto().isActivo()) // no devolver inactivos
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
    @Transactional
    public void removeItem(Long idItem) {
        itemRepo.deleteById(idItem);

        // Recalcular total después de eliminar
        Carrito carrito = getOrCreateCarrito();
        actualizarTotal(carrito);
    }

    // ----------------------------------------------------
    // 🟩 Vaciar carrito completo
    // ----------------------------------------------------
    @Transactional
    public void clearCarrito() {

        Carrito carrito = getOrCreateCarrito();

        // 1) Traemos los items del carrito
        List<ItemCarrito> items = itemRepo.findByCarrito(carrito);

        // 2) Los borramos con deleteAll (dentro de la transacción)
        itemRepo.deleteAll(items);

        // 3) Reseteamos el total y guardamos el carrito
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
    // 🟦 FINALIZAR COMPRA (Checkout real – Android)
    // =====================================================
   // =====================================================
// 🟦 FINALIZAR COMPRA (Checkout real – Android)
// =====================================================
@Transactional
public Long finalizarCompra() {

    Carrito carrito = getOrCreateCarrito();
    List<ItemCarrito> items = itemRepo.findByCarrito(carrito);

    // 🔹 1) Validar que el carrito tenga items
    if (items == null || items.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El carrito está vacío, no se puede finalizar la compra"
        );
    }

    // 🔹 2) Recalcular el total “por si acaso”
    double total = items.stream()
            .mapToDouble(ItemCarrito::getSubtotal)
            .sum();

    if (total <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El total del carrito es 0, revisa los productos antes de comprar"
        );
    }

    // 🔹 3) Crear la venta
    Venta venta = new Venta();
    venta.setFecha(LocalDate.now());
    venta.setTotal(total);          // usamos el recalculado
    // ⚠️ Si tu entidad Venta tiene usuario NOT NULL, aquí deberías setearlo.
    // venta.setUsuario(usuario);   // si más adelante quieres asociar usuario

    venta = ventaRepo.save(venta);

    // 🔹 4) Recorrer items, validar y crear detalle_venta
    for (ItemCarrito item : items) {

        Producto prod = item.getProducto();

        if (prod == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hay un item en el carrito sin producto asociado"
            );
        }

        // Producto inactivo
        if (!prod.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El producto está desactivado: " + prod.getNombre()
            );
        }

        // Stock insuficiente
        if (prod.getStock() < item.getCantidad()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para: " + prod.getNombre()
            );
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

    // 🔹 5) Vaciar carrito
    itemRepo.deleteAll(items);
    carrito.setTotal(0.0);
    carritoRepo.save(carrito);

    // 🔹 6) Devolver ID de la venta
    return venta.getId_venta();
}

}
