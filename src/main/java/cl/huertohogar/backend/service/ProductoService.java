package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.repository.ProductoRepository;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
