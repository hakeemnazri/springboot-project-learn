package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.model.Product;
import com.spring_boot.ecommerce.payload.ProductDTO;
import com.spring_boot.ecommerce.payload.ProductResponse;
import com.spring_boot.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product, @PathVariable  Long categoryId){
        ProductDTO productDTO = productService.addProduct(categoryId, product);
        System.out.println(product);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){
        ProductResponse allProducts = productService.getAllProducts();
        return new ResponseEntity<ProductResponse>(allProducts, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(
            @PathVariable Long categoryId
    ){
        ProductResponse productResponse = productService.searchByCategory(categoryId);

        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword
    ){
        ProductResponse productResponse = productService.searchProductByKeyword("%" +keyword+ "%");

        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestBody ProductDTO productDTO,
            @PathVariable Long productId
    ){
        ProductDTO savedProductDTO = productService.updateProduct(productId, productDTO);

        return new ResponseEntity<ProductDTO>( savedProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

        ProductDTO productDTO = productService.deleteById(productId);

        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }

}
