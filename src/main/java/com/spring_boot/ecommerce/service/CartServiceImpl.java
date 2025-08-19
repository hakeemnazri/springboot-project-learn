package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.exceptions.APIException;
import com.spring_boot.ecommerce.exceptions.ResourceNotFoundException;
import com.spring_boot.ecommerce.model.Cart;
import com.spring_boot.ecommerce.model.CartItem;
import com.spring_boot.ecommerce.model.Product;
import com.spring_boot.ecommerce.payload.CartDTO;
import com.spring_boot.ecommerce.payload.ProductDTO;
import com.spring_boot.ecommerce.repositories.CartItemRepository;
import com.spring_boot.ecommerce.repositories.CartRepository;
import com.spring_boot.ecommerce.repositories.ProductRepository;
import com.spring_boot.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public CartDTO addProdcutToCart(Long productId, Integer quantity) {
        //find existing cart or create one
        Cart cart = createCart();

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(productId, "Product", "productId"));

        //perform validation
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                product.getProductId()
        );

        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() + " already exists in cart");
        }

        if(product.getQuantity() == 0){
            throw  new APIException(product.getProductName() + " is not available!");
        }

        if(product.getQuantity() < quantity){
            throw  new APIException("Please make an order of the " + product.getProductName() + " less than or equal to the quantity" + product.getQuantity() + ".");
        }

        //create cart item

        CartItem newCartItem = new CartItem();

        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity()); // if reduce stock then deduct

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        //return updated cart
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {

            ProductDTO productDto = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDto.setQuantity(item.getQuantity());

            return productDto;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);

        if(cart == null){
            throw new ResourceNotFoundException(cartId, "cartId", "Cart");
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream().map(item -> {
            Product product = item.getProduct();
            product.setQuantity(item.getQuantity());
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

            return productDTO;
        }).toList();

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();

        Cart cart = cartRepository.findCartByEmail(emailId);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(productId, "Product", "productId"));

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Quantity error! more than stock");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if(cartItem == null){
            throw new APIException("Product not available in cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity < 0){
            throw new APIException("cannot be negative");
        }

        if(newQuantity == 0){
            deleteProductFromCart(cart.getCartId(), productId);
        }else{
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setDiscount(product.getDiscount());

        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
        }

        cartRepository.save(cart);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        if(updatedCartItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(c -> {
                    ProductDTO productDTO = modelMapper.map(c.getProduct(), ProductDTO.class);

                    return productDTO;
                }
        ).toList();

        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException(cartId, "Cart", "cartId"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if(cartItem == null){
            throw new ResourceNotFoundException(productId, "Product", "productId");
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getQuantity() * cartItem.getProductPrice()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product" + cartItem.getProduct().getProductName() + " removed from cart.";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException(cartId, "Cart", "cartId"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(productId, "Product", "productId"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if(cartItem == null){
            throw new APIException("Product not in cart");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + cartItem.getProductPrice() * cartItem.getQuantity());

        cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> allCarts = cartRepository.findAll();

        if(allCarts.size() == 0){
            throw new APIException("No cart exists");
        }

        List<CartDTO> cartDTOs = allCarts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOs = cart.getCartItems().stream().map(p -> {
                Product product = p.getProduct();
                ProductDTO map = modelMapper.map(product, ProductDTO.class);
                map.setQuantity(p.getQuantity());
                return map;
            }).toList();
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).toList();

        return cartDTOs;
    }



    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);

        return newCart;
    }


}
