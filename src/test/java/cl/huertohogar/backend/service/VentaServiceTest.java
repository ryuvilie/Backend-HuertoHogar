package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.VentaRequest;
import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.repository.ProductoRepository;
import cl.huertohogar.backend.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VentaServiceTest {

    @InjectMocks
    private VentaService ventaService;

    @Mock
    private VentaRepository ventaRepo;

    @Mock
    private ProductoRepository productoRepo;

    private Producto buildProducto(Long id, String nombre, double precio, int stock){
        Producto p = new Producto();
        p.setId_producto(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        return p;
    }

    private VentaRequest.ItemCarrito item(long idProducto, int cantidad){
        VentaRequest.ItemCarrito it = new VentaRequest.ItemCarrito();
        it.setIdProducto(idProducto);
        it.setCantidad(cantidad);
        return it;
    }

    private VentaRequest request(LocalDate fecha, VentaRequest.ItemCarrito... items){
        VentaRequest req = new VentaRequest();
        req.setFecha(fecha);
        req.setItems(List.of(items));
        return req;
    }

    @BeforeEach
    void setup(){
        // Default: ventaRepo.save returns entity with id set
        when(ventaRepo.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            if(v.getId_venta() == null){
                v.setId_venta(1L);
            }
            return v;
        });
    }

    // 1) Should compute total and reduce stock for single item
    @Test
    void crearVenta_singleItem_updatesTotalAndStock_andPersistsVenta(){
        Producto p = buildProducto(10L, "Lechuga", 1500.0, 8);
        when(productoRepo.findById(10L)).thenReturn(Optional.of(p));
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta result = ventaService.crearVenta(
                request(LocalDate.of(2024,10,1), item(10L, 2))
        );

        assertNotNull(result.getId_venta());
        assertEquals(LocalDate.of(2024,10,1), result.getFecha());
        assertEquals(3000.0, result.getTotal());
        assertEquals(6, p.getStock());
        verify(productoRepo, times(1)).findById(10L);
        verify(productoRepo, times(1)).save(p);
        verify(ventaRepo, times(1)).save(any(Venta.class));
    }

    // 2) Should sum totals across multiple items
    @Test
    void crearVenta_multipleItems_sumsTotals(){
        Producto p1 = buildProducto(1L, "Tomate", 500.0, 10);
        Producto p2 = buildProducto(2L, "Zanahoria", 250.0, 20);
        when(productoRepo.findById(1L)).thenReturn(Optional.of(p1));
        when(productoRepo.findById(2L)).thenReturn(Optional.of(p2));
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta v = ventaService.crearVenta(
                request(LocalDate.now(), item(1L, 3), item(2L, 4))
        );

        // Total = 3*500 + 4*250 = 1500 + 1000 = 2500
        assertEquals(2500.0, v.getTotal());
        assertEquals(7, p1.getStock());
        assertEquals(16, p2.getStock());
        verify(productoRepo, times(1)).findById(1L);
        verify(productoRepo, times(1)).findById(2L);
        verify(productoRepo, times(2)).save(any(Producto.class));
        verify(ventaRepo, times(1)).save(any(Venta.class));
    }

    // 3) Should throw when product not found
    @Test
    void crearVenta_productoNoEncontrado_lanzaExcepcion(){
        when(productoRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ventaService.crearVenta(
                        request(LocalDate.now(), item(99L, 1))
                )
        );
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productoRepo, times(1)).findById(99L);
        verify(ventaRepo, never()).save(any());
    }

    // 4) Should throw when insufficient stock and not persist or save product
    @Test
    void crearVenta_stockInsuficiente_lanzaExcepcion(){
        Producto p = buildProducto(5L, "Pepino", 800.0, 1);
        when(productoRepo.findById(5L)).thenReturn(Optional.of(p));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ventaService.crearVenta(
                        request(LocalDate.now(), item(5L, 2))
                )
        );
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        // productoRepo.save should not be called because stock check fails
        verify(productoRepo, never()).save(any());
        verify(ventaRepo, never()).save(any());
    }

    // 5) Should save product after stock deduction for each item
    @Test
    void crearVenta_guardaProductoPorCadaItem(){
        Producto p1 = buildProducto(1L, "Ajo", 100.0, 5);
        Producto p2 = buildProducto(2L, "Cebolla", 200.0, 5);

        when(productoRepo.findById(1L)).thenReturn(Optional.of(p1));
        when(productoRepo.findById(2L)).thenReturn(Optional.of(p2));

        ventaService.crearVenta(
                request(LocalDate.now(), item(1L, 1), item(2L, 2))
        );

        verify(productoRepo, times(1)).save(p1);
        verify(productoRepo, times(1)).save(p2);
        verify(ventaRepo, times(1)).save(any(Venta.class));
    }
}
