package com.bidgrove.productservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidgrove.productservice.model.Bid;
import com.bidgrove.productservice.model.Product;
import com.bidgrove.productservice.security.AuthUser;
import com.bidgrove.productservice.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ============================
    // HELPER → Get logged-in user
    // ============================
    private AuthUser getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        return (AuthUser) authentication.getPrincipal();
    }

    // ============================
    // TEST AUTH ENDPOINT
    // ============================
    @GetMapping("/test-auth")
    public String testAuth() {
        AuthUser user = getLoggedUser();
        return "Hello, User ID: " + user.getUserId() + ", Role: " + user.getRole();
    }

    // ============================
    // ADD PRODUCT
    // ============================
    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        AuthUser user = getLoggedUser();
        return productService.addProduct(product, user.getUserId());
    }

    // ============================
    // UPDATE PRODUCT
    // ============================
    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestBody Product product) {
        AuthUser user = getLoggedUser();
        return productService.updateProduct(id, product, user.getUserId());
    }

    // ============================
    // DELETE PRODUCT
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        AuthUser user = getLoggedUser();
        productService.deleteProduct(id, user.getUserId());
        return ResponseEntity.noContent().build();   // 204 No Content
    }

    // ============================
    // GET ALL PRODUCTS (public)
    // ============================
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // ============================
    // GET MY PRODUCTS
    // ============================
    @GetMapping("/my")
    public List<Product> getMyProducts() {
        AuthUser user = getLoggedUser();
        return productService.getProductsByUser(user.getUserId());
    }

    // ============================
    // GET PRODUCT BY ID
    // ============================
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ============================
    // PLACE BID
    // ============================
    @PostMapping("/bid/{id}")
    public Product placeBid(@PathVariable Long id,
                            @RequestBody Bid bid) {
        AuthUser user = getLoggedUser();
        return productService.addBid(id, bid, user.getUserId());
    }

    // ============================
    // GET MY ORDERS
    // ============================
    @GetMapping("/orders/my")
    public List<Product> getMyOrders() {
        AuthUser user = getLoggedUser();
        return productService.getProductsBidByUser(user.getUserId());
    }
}
