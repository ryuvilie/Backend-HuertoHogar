package cl.huertohogar.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.model.DetallePedido;
import cl.huertohogar.backend.service.DetallePedidoService;

@RestController
@RequestMapping("/api/detalles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @GetMapping
    public List<DetallePedido> listar() {
        return detallePedidoService.listar();
    }

    @GetMapping("/{id}")
    public DetallePedido buscarPorId(@PathVariable Long id) {
        return detallePedidoService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public DetallePedido guardar(@RequestBody DetallePedido detalle) {
        return detallePedidoService.guardar(detalle);
    }

    @PutMapping("/{id}")
    public DetallePedido actualizar(@PathVariable Long id, @RequestBody DetallePedido detalle) {
        detalle.setId_detalle(id);
        return detallePedidoService.guardar(detalle);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        detallePedidoService.eliminar(id);
    }
}
