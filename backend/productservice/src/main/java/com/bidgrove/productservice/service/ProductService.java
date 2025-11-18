package com.bidgrove.productservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidgrove.productservice.model.Bid;
import com.bidgrove.productservice.model.Product;
import com.bidgrove.productservice.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    // Add a product (only authorized user)
    public Product addProduct(Product product, Long userId) {
        product.setUserId(userId);
        product.setFinalPrice(product.getStartPrice());
        product.setBids(List.of()); // initialize empty bid list
        return repository.save(product);
    }

    // Update a product (only owner)
    public Product updateProduct(Long productId, Product updatedProduct, Long userId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only update your own products");
        }

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setStartPrice(updatedProduct.getStartPrice());
        product.setStartDateTime(updatedProduct.getStartDateTime());
        product.setEndDateTime(updatedProduct.getEndDateTime());

        return repository.save(product);
    }

    // Delete a product (only owner)
    public void deleteProduct(Long productId, Long userId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own products");
        }

        repository.delete(product);
    }

    // List all products (everyone can see)
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // List products of a specific user (for "My Products" page)
    public List<Product> getProductsByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    // Get product by ID
    public Product getProductById(Long productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

 public Product addBid(Long productId, Bid bid, Long userId) {

	    Product product = repository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found"));

	    LocalDateTime now = LocalDateTime.now();

	    if (now.isBefore(product.getStartDateTime())) {
	        throw new RuntimeException("Bidding has not started yet");
	    }

	    if (now.isAfter(product.getEndDateTime())) {
	        throw new RuntimeException("Bidding has ended");
	    }

	    if (bid.getAmount() <= product.getFinalPrice()) {
	        throw new RuntimeException(
	                "Your bid must be higher than current price: " + product.getFinalPrice()
	        );
	    }

	   
	    bid.setUserId(userId);
	    bid.setBidTime(now);
	    bid.setProduct(product);   

	    if (product.getBids() == null) {
	        product.setBids(new ArrayList<>());
	    }

	    product.getBids().add(bid);

	    product.setFinalPrice(bid.getAmount());
	    product.setLastBidUserId(userId);

	    return repository.save(product);
	}


    // List bids of logged-in user (for "My Orders")
    public List<Product> getProductsBidByUser(Long userId) {
        List<Product> allProducts = repository.findAll();
        return allProducts.stream()
                .filter(p -> p.getBids().stream().anyMatch(b -> b.getUserId().equals(userId)))
                .toList();
    }
}
