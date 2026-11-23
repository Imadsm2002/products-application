package com.example.productsapp.repository;

import com.example.productsapp.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Recherche avec pagination par mot-clé sur le nom du produit
     */
    Page<Product> findByNameContains(String keyword, Pageable pageable);

    /**
     * Recherche simple sans pagination par mot-clé sur le nom du produit
     */
    List<Product> findByNameContains(String keyword);
}
