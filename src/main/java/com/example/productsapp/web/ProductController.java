package com.example.productsapp.web;

import com.example.productsapp.entities.Product;
import com.example.productsapp.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    /**
     * Afficher la liste paginée et filtrée des produits
     */
    @GetMapping(path = {"/user/index", "/index"})
    public String index(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Product> pageProducts = productRepository.findByNameContains(keyword, PageRequest.of(page, size));

        // Statistiques globales pour le tableau de bord
        List<Product> allProducts = productRepository.findAll();
        double totalStockValue = allProducts.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        long lowStockCount = allProducts.stream().filter(p -> p.getQuantity() <= 10).count();

        model.addAttribute("listProducts", pageProducts.getContent());
        model.addAttribute("pages", new int[pageProducts.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalElements", pageProducts.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);

        // Stats
        model.addAttribute("totalProductsCount", allProducts.size());
        model.addAttribute("totalStockValue", totalStockValue);
        model.addAttribute("lowStockCount", lowStockCount);

        return "products";
    }

    /**
     * Supprimer un produit (Réservé au rôle ADMIN)
     */
    @GetMapping(path = "/admin/delete")
    public String delete(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {

        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le produit a été supprimé avec succès !");

        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Formulaire d'ajout d'un nouveau produit (Réservé au rôle ADMIN)
     */
    @GetMapping(path = "/admin/formProduct")
    public String formProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("mode", "new");
        return "formProduct";
    }

    /**
     * Enregistrer un produit avec validation (Réservé au rôle ADMIN)
     */
    @PostMapping(path = "/admin/save")
    public String save(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", product.getId() == null ? "new" : "edit");
            return product.getId() == null ? "formProduct" : "editProduct";
        }

        boolean isNew = (product.getId() == null);
        productRepository.save(product);

        String message = isNew ? "Nouveau produit ajouté avec succès !" : "Produit mis à jour avec succès !";
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }

    /**
     * Formulaire de modification d'un produit (Réservé au rôle ADMIN)
     */
    @GetMapping(path = "/admin/editProduct")
    public String editProduct(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Produit introuvable avec l'ID : " + id)
        );

        model.addAttribute("product", product);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("mode", "edit");
        return "editProduct";
    }

    /**
     * Fonctionnalité bonus : Export de la liste des produits au format CSV
     */
    @GetMapping(path = "/user/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"produits.csv\"");

        List<Product> products = productRepository.findAll();
        PrintWriter writer = response.getWriter();

        // En-tête CSV (avec BOM UTF-8 pour Excel)
        writer.write('\ufeff');
        writer.println("ID;Nom du Produit;Prix (DH);Quantite;Valeur Stock (DH)");

        for (Product p : products) {
            writer.printf("%d;\"%s\";%.2f;%d;%.2f%n",
                    p.getId(),
                    p.getName().replace("\"", "\"\""),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getPrice() * p.getQuantity()
            );
        }
        writer.flush();
    }
}
