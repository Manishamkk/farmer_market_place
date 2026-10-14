package com.mit.fmp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin("0.01")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String location;
    private String imageUrl;

    

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "farmer_id", nullable = false)
private Users farmer;

    public Users getFarmer() {
	return farmer;
}

public void setFarmer(Users farmer) {
	this.farmer = farmer;
}

	public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
