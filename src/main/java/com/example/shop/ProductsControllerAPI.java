package com.example.shop;

import com.example.shop.model.Products;
import com.example.shop.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductsControllerAPI {

    private ProductsRepository productRepository;

    @Autowired
    public void setProductsRepository(ProductsRepository productsRepository){
        productRepository = productsRepository;
    }


    @GetMapping
    public List<Products> getAllProducts() {
        return (List<Products>) productRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") int id) {
        Optional<Products> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<Void> updateProductQuantity(@PathVariable(name = "id") int id, @RequestBody Map<String, Integer> requestBody) {
        int quantity = requestBody.get("quantity");
        Optional<Products> product = productRepository.findById(id);
        if (product.isPresent()) {
            Products existingProduct = product.get();
            existingProduct.setQuantity(quantity);
            productRepository.save(existingProduct);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody Products product) {
        try {
            Products newProduct = productRepository.save(product);
            if (newProduct != null) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            // Obsługa błędu 500
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd podczas dodawania produktu.");
        }
    }
}

