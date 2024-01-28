package com.example.shop.repositories;


import com.example.shop.model.Products;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends CrudRepository<Products, Integer> {

    @Query("SELECT DISTINCT category FROM Products")
    Optional<List<String>> getAllCategories();

    @Query("SELECT p FROM Products p WHERE p.category = :category")
    Optional<List<Products>> findByCategory(@Param("category") String category);

    @Query("SELECT p FROM Products p WHERE p.id = :id")
    Optional<Products> getProductDetails(@Param("id") int id);

}
