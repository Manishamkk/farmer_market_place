package com.mit.fmp.service;

import java.util.List;

import com.mit.fmp.entity.Product;

public interface ProductService {

    // Create
    Product addProduct(Product product);

    // Read all
    List<Product> getAllProducts();

    // Read by ID
    Product getProductById(Long id);

    // Update
    Product updateProduct(Long id, Product product);

    // Delete
    void deleteProduct(Long id);
}