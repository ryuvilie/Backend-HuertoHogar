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

    // 🔥 Catálogo y listados normales → solo activos
    public List<Producto> getAll() {
        return productoRepo.findByActivoTrue();
    }

    // (Opcional) si alguna vista admin necesita ver todos:
    public List<Producto> getAllIncluyendoInactivos() {
        return productoRepo.findAll();
    }

    public Producto create(Producto p) {
        p.setActivo(true); // por defecto activo
        return productoRepo.save(p);
    }

    // Actualizar solo precio
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

    // ✔ actualiza solo stock
    public Producto updateStock(Long id, Integer stock) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setStock(stock);
        return productoRepo.save(prod);
    }

    // ❌ Antes borraba de verdad → ahora DESACTIVA
    public void delete(Long id) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setActivo(false); // 🔥 eliminación lógica
        productoRepo.save(prod);
    }
    public void activar(Long id) {
    Producto prod = productoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    prod.setActivo(true);
    productoRepo.save(prod);
}

}
