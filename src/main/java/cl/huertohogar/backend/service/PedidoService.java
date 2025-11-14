package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

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
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
