package cl.huertohogar.backend.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import cl.huertohogar.backend.model.Cliente;
import cl.huertohogar.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
