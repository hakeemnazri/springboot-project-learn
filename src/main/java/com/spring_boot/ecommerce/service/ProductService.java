package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.payload.ProductDTO;
import com.spring_boot.ecommerce.payload.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteById(Long productId);
}
