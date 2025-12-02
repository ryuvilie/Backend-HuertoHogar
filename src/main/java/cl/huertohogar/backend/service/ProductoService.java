package cl.huertohogar.backend.service;

import cl.huertohogar.backend.model.Producto;
import cl.huertohogar.backend.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepo;

    // ✔ SOLO productos activos para catálogo + admin
    public List<Producto> getAll() {
        return productoRepo.findByActivoTrue();
    }

    public Producto create(Producto p) {
        p.setActivo(true); // aseguramos que siempre se creen activos
        return productoRepo.save(p);
    }

    public Producto updatePrecio(Long id, Double precio) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setPrecio(precio);
        return productoRepo.save(prod);
    }

    public Producto update(Long id, Producto data) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setNombre(data.getNombre());
        prod.setDescripcion(data.getDescripcion());
        prod.setPrecio(data.getPrecio());
        prod.setStock(data.getStock());
        prod.setCategoria(data.getCategoria());
        prod.setImageUrl(data.getImageUrl());

        return productoRepo.save(prod);
    }

    public Producto updateStock(Long id, Integer stock) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setStock(stock);
        return productoRepo.save(prod);
    }

    // ✔ NUEVO — eliminación lógica
    public void delete(Long id) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setActivo(false);
        productoRepo.save(prod);
    }
}
