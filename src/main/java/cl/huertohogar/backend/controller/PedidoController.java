package cl.huertohogar.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.model.Pedido;
import cl.huertohogar.backend.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public List<Pedido> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Pedido guardar(@RequestBody Pedido pedido) {
        return pedidoService.guardar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        pedido.setId_pedido(id);
        return pedidoService.guardar(pedido);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
    }
}
