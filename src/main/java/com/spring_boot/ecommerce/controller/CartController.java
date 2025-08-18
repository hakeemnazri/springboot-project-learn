package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.payload.CartDTO;
import com.spring_boot.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCard(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ){
        CartDTO cartDTO = cartService.addProdcutToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.CREATED);
    }
}
