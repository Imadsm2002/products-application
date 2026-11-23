package com.example.productsapp.repository;

import com.example.productsapp.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Tester l'enregistrement d'un produit")
    void testSaveProduct() {
        Product product = Product.builder()
                .name("Tablette Graphique Wacom")
                .price(1800.0)
                .quantity(5)
                .build();

        Product saved = productRepository.save(product);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Tablette Graphique Wacom");
    }

    @Test
    @DisplayName("Tester la recherche par mot-clé (findByNameContains)")
    void testFindByNameContains() {
        productRepository.save(new Product(null, "PC Gamer Asus ROG", 15000.0, 3));
        productRepository.save(new Product(null, "PC Portable Dell XPS", 14000.0, 4));
        productRepository.save(new Product(null, "Écran Gamer Samsung", 3500.0, 10));

        List<Product> results = productRepository.findByNameContains("PC");
        assertThat(results).hasSize(2);

        Page<Product> pageResults = productRepository.findByNameContains("PC", PageRequest.of(0, 1));
        assertThat(pageResults.getTotalElements()).isEqualTo(2);
        assertThat(pageResults.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Tester la modification d'un produit")
    void testUpdateProduct() {
        Product product = productRepository.save(new Product(null, "Microphone Shure", 1200.0, 7));
        product.setPrice(1350.0);
        product.setQuantity(9);
        Product updated = productRepository.save(product);

        assertThat(updated.getPrice()).isEqualTo(1350.0);
        assertThat(updated.getQuantity()).isEqualTo(9);
    }

    @Test
    @DisplayName("Tester la suppression d'un produit")
    void testDeleteProduct() {
        Product product = productRepository.save(new Product(null, "Câble HDMI 4K", 120.0, 50));
        Long id = product.getId();

        productRepository.deleteById(id);
        Optional<Product> optional = productRepository.findById(id);

        assertThat(optional).isEmpty();
    }
}
