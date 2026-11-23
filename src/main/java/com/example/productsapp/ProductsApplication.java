package com.example.productsapp;

import com.example.productsapp.entities.Product;
import com.example.productsapp.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProductsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsApplication.class, args);
    }

    /**
     * Test de la couche DAO au démarrage de l'application (Étape 4)
     * Insertion de données de test et affichage dans la console
     */
    @Bean
    public CommandLineRunner commandLineRunner(ProductRepository productRepository) {
        return args -> {
            System.out.println("==================================================");
            System.out.println("  INITIALISATION & TEST DE LA COUCHE DAO (JPA)    ");
            System.out.println("==================================================");

            if (productRepository.count() == 0) {
                productRepository.save(new Product(null, "Ordinateur Portable HP ProBook", 8500.0, 15));
                productRepository.save(new Product(null, "Imprimante Laser HP LaserJet", 2400.0, 8));
                productRepository.save(new Product(null, "Smartphone Samsung Galaxy S23", 9500.0, 22));
                productRepository.save(new Product(null, "Clavier Mécanique RGB Logitech", 650.0, 35));
                productRepository.save(new Product(null, "Souris Sans Fil Logitech MX Master 3", 890.0, 40));
                productRepository.save(new Product(null, "Écran 27 Pouces Dell 4K IPS", 4200.0, 12));
                productRepository.save(new Product(null, "Disque Dur Externe SSD 1TB SanDisk", 1150.0, 28));
                productRepository.save(new Product(null, "Casque Bluetooth Sony WH-1000XM5", 3600.0, 10));
                productRepository.save(new Product(null, "Webcam HD 1080p Logitech C920", 720.0, 18));
                productRepository.save(new Product(null, "Station d'accueil USB-C Thunderbolt", 1850.0, 14));
            }

            System.out.println("Total des produits insérés : " + productRepository.count());
            System.out.println("Liste des produits récupérés depuis la BDD :");
            productRepository.findAll().forEach(p -> 
                System.out.printf(" - [ID: %d] %-35s | Prix: %8.2f DH | Quantité: %3d%n",
                        p.getId(), p.getName(), p.getPrice(), p.getQuantity())
            );
            System.out.println("==================================================");
        };
    }
}
