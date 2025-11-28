package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.DetalleCarritoDTO;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CarritoServiceTest {

    @InjectMocks
    private CarritoService carritoService;

    @Mock
    private CarritoRepository carritoRepo;

    @Mock
    private ItemCarritoRepository itemRepo;

    @Mock
    private ProductoRepository productoRepo;

    @Mock
    private VentaRepository ventaRepo;

    @Mock
    private DetalleVentaRepository detalleVentaRepo;

    private Carrito carrito;
    private Producto producto;

    @BeforeEach
    public void setUp() {
        // Set up mocks for carrito and producto
        carrito = new Carrito();
        carrito.setId_carrito(1L);
        carrito.setTotal(0.0);

        producto = new Producto();
        producto.setId_producto(1L);
        producto.setPrecio(100.0);
        producto.setStock(10);
        producto.setNombre("Producto de prueba");

        // Mocks
        when(carritoRepo.findAll()).thenReturn(List.of(carrito));
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
    }

    // Test para agregar producto al carrito
    @Test
    public void testAddToCart() {
        // Test de agregar producto al carrito
        carritoService.addToCart(1L, 2);

        // Verificamos que el item se haya agregado al carrito
        verify(itemRepo, times(1)).save(any(ItemCarrito.class));
        verify(carritoRepo, times(1)).save(carrito);
    }

    // Test para obtener carrito
    @Test
    public void testGetCarrito() {
        // Simulamos el carrito con items
        ItemCarrito itemCarrito = new ItemCarrito();
        itemCarrito.setId_item(1L);
        itemCarrito.setProducto(producto);
        itemCarrito.setCantidad(2);
        itemCarrito.setSubtotal(200.0);

        // Devolvemos los items al llamar a getCarrito
        when(itemRepo.findByCarrito(carrito)).thenReturn(List.of(itemCarrito));

        List<DetalleCarritoDTO> carritoDTO = carritoService.getCarrito();

        // Verificamos que la lista de detalles del carrito tiene un item
        assertEquals(1, carritoDTO.size());
        assertEquals(200.0, carritoDTO.get(0).getSubtotal());
    }

    // Test para eliminar producto del carrito
    @Test
    public void testRemoveItem() {
        // Simulamos que el carrito tiene un producto
        ItemCarrito itemCarrito = new ItemCarrito();
        itemCarrito.setId_item(1L);
        itemCarrito.setProducto(producto);
        itemCarrito.setCantidad(1);
        itemCarrito.setSubtotal(100.0);

        when(itemRepo.findByCarrito(carrito)).thenReturn(List.of(itemCarrito));

        // Eliminar el item
        carritoService.removeItem(1L);

        // Verificamos que el item se haya eliminado
        verify(itemRepo, times(1)).deleteById(1L);
        verify(carritoRepo, times(1)).save(carrito);
    }

    // Test para vaciar el carrito
    @Test
    public void testClearCarrito() {
        // Simulamos que el carrito tiene items
        ItemCarrito itemCarrito = new ItemCarrito();
        itemCarrito.setId_item(1L);
        itemCarrito.setProducto(producto);
        itemCarrito.setCantidad(2);
        itemCarrito.setSubtotal(200.0);

        when(itemRepo.findByCarrito(carrito)).thenReturn(List.of(itemCarrito));

        // Vaciar el carrito
        carritoService.clearCarrito();

        // Verificamos que se haya eliminado el item
        verify(itemRepo, times(1)).deleteAll(anyList());
        verify(carritoRepo, times(1)).save(carrito);
    }

   
}
