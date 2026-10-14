
package com.mit.fmp.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mit.fmp.entity.Product;
import com.mit.fmp.entity.Users;
import com.mit.fmp.exception.ProductNotFoundException;
import com.mit.fmp.exception.UserNotFoundException;
import com.mit.fmp.repository.ProductRepository;
import com.mit.fmp.repository.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    @Override
    @Transactional
    public Product addProduct(Product product) {

        product.setId(null);

        // Get farmer ID from request's Product object
        if (product.getFarmer() == null ||
                product.getFarmer().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Farmer ID is required");
        }

        Long farmerId = product.getFarmer().getId();

        // Check whether farmer exists
        Users farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Farmer not found with ID: " + farmerId));

        // Only FARMER can add products
        if (!"FARMER".equalsIgnoreCase(farmer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only farmers can add products");
        }

        product.setFarmer(farmer);

        return productRepository.save(product);
    }

    // READ ALL
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ BY ID
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id));
    }

    // UPDATE
    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setLocation(product.getLocation());
        existingProduct.setImageUrl(product.getImageUrl());

        // Keep existing farmer relationship unchanged
        return productRepository.save(existingProduct);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product existingProduct = getProductById(id);
        productRepository.delete(existingProduct);
    }
}
