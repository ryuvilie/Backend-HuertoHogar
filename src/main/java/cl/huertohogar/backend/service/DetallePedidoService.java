package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.model.DetallePedido;
import cl.huertohogar.backend.repository.DetallePedidoRepository;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public List<DetallePedido> listar() {
        return detallePedidoRepository.findAll();
    }

    public Optional<DetallePedido> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id);
    }

    public DetallePedido guardar(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    public void eliminar(Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
