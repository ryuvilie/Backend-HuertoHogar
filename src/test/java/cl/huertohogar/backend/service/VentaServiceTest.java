package cl.huertohogar.backend.service;

import cl.huertohogar.backend.model.Venta;
import cl.huertohogar.backend.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VentaServiceTest {

    @InjectMocks
    private VentaService ventaService;

    @Mock
    private VentaRepository ventaRepository;

    private Venta buildVenta(Long id, LocalDate fecha, Double total) {
        Venta v = new Venta();
        v.setId_venta(id);
        v.setFecha(fecha);
        v.setTotal(total);
        return v;
    }

    
    @Test
    void testGetAllVentasReturnsList() {
        List<Venta> ventas = List.of(
                buildVenta(1L, LocalDate.of(2024, 1, 10), 100.0),
                buildVenta(2L, LocalDate.of(2024, 1, 11), 200.0)
        );
        when(ventaRepository.findAll()).thenReturn(ventas);

        List<Venta> result = ventaService.getAllVentas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId_venta());
        verify(ventaRepository, times(1)).findAll();
        verifyNoMoreInteractions(ventaRepository);
    }

   
    @Test
    void testGetAllVentasReturnsEmptyList() {
        when(ventaRepository.findAll()).thenReturn(List.of());

        List<Venta> result = ventaService.getAllVentas();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ventaRepository, times(1)).findAll();
        verifyNoMoreInteractions(ventaRepository);
    }


    @Test
    void testGetVentaByIdFound() {
        Venta venta = buildVenta(5L, LocalDate.of(2024, 2, 1), 500.0);
        when(ventaRepository.findById(5L)).thenReturn(Optional.of(venta));

        Venta result = ventaService.getVentaById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId_venta());
        assertEquals(500.0, result.getTotal());
        verify(ventaRepository, times(1)).findById(5L);
        verifyNoMoreInteractions(ventaRepository);
    }

    
    @Test
    void testGetVentaByIdNotFoundThrows() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ventaService.getVentaById(999L));
        assertEquals("Venta no encontrada", ex.getMessage());
        verify(ventaRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(ventaRepository);
    }

    // 5) Should call repository exactly once per invocation for getById
    @Test
    void testRepositoryInteractionOnceForGetById() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(buildVenta(1L, LocalDate.now(), 10.0)));

        ventaService.getVentaById(1L);

        verify(ventaRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(ventaRepository);
    }
}
