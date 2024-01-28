package com.example.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private String imageUrl;

    private Integer quantity;

    private Double price;

    private String category;

    private String manufacturer;

    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }
}
