package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.model.Cart;
import com.spring_boot.ecommerce.payload.CartDTO;
import com.spring_boot.ecommerce.repositories.CartRepository;
import com.spring_boot.ecommerce.service.CartService;
import com.spring_boot.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCard(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ){
        CartDTO cartDTO = cartService.addProdcutToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){

        List<CartDTO> cartDtos = cartService.getAllCarts();

        return new ResponseEntity<List<CartDTO>>(cartDtos , HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.FOUND);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
           @PathVariable Long productId,
           @PathVariable Long cartId
    ){
        String message = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}
