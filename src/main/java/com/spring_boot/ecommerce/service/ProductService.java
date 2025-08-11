package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.model.Product;
import com.spring_boot.ecommerce.payload.ProductDTO;
import com.spring_boot.ecommerce.payload.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, Product product);

    ProductResponse getAllProducts();
}
