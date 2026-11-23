package com.example.productsapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le nom du produit est obligatoire.")
    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caractères.")
    private String name;

    @DecimalMin(value = "0.1", message = "Le prix doit être supérieur ou égal à 0.1 DH.")
    private double price;

    @Min(value = 0, message = "La quantité doit être positive ou nulle.")
    private int quantity;
}
