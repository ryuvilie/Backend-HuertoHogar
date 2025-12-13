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

    // 📦 LISTAR TODOS
    public List<Producto> getAll() {
        return productoRepo.findAll();
    }

    // 🔎 OBTENER POR ID (DETALLE)
    public Producto getById(Long id) {
        return productoRepo.findById(id).orElse(null);
    }

    // ➕ CREAR
    public Producto create(Producto p) {
        return productoRepo.save(p);
    }

    // 💲 ACTUALIZAR PRECIO
    public Producto updatePrecio(Long id, Double precio) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setPrecio(precio);
        return productoRepo.save(prod);
    }

    // ✏️ ACTUALIZAR COMPLETO
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

    // 📦 ACTUALIZAR SOLO STOCK
    public Producto updateStock(Long id, Integer stock) {
        Producto prod = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        prod.setStock(stock);
        return productoRepo.save(prod);
    }

    // ❌ ELIMINAR
    public void delete(Long id) {
        productoRepo.deleteById(id);
    }
}
