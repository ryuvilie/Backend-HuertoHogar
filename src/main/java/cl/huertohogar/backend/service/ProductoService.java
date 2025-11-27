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

    private final ProductoRepository productoRepo;

    public List<Producto> getAll() {
        return productoRepo.findAll();
    }

    public Producto create(Producto producto) {
        return productoRepo.save(producto);
    }

    public Producto update(Long id, Producto data) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        prod.setNombre(data.getNombre());
        prod.setDescripcion(data.getDescripcion());
        prod.setPrecio(data.getPrecio());
        prod.setStock(data.getStock());
        prod.setImageUrl(data.getImageUrl());
        prod.setCategoria(data.getCategoria());

        return productoRepo.save(prod);
    }

    public Producto updateStock(Long id, Integer newStock) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        prod.setStock(newStock);
        return productoRepo.save(prod);
    }

    public void delete(Long id) {
        productoRepo.deleteById(id);
    }
}
