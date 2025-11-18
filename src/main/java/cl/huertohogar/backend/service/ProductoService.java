package cl.huertohogar.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        // 1) Validar que el precio venga informado
        if (producto.getPrecio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio del producto es obligatorio."
            );
        }

        // 2) Validar que el precio sea mayor que 0
        if (producto.getPrecio() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio del producto debe ser mayor a 0."
            );
        }

        // 3) Si pasa las validaciones, guardar
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
