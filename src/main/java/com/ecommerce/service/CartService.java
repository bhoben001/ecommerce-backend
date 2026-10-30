package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.CartItemRequest;
import com.ecommerce.dto.response.CartItemResponse;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.CartItemMapper;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;


    @Transactional()
    public CartResponse getCart(UserPrincipal me) {
         Cart cart=getOrCreateCart(me.getId());
         return cartMapper.toResponse(cart);
    }

    @Transactional
    public void addItemToCart(UserPrincipal me, CartItemRequest request){
        Cart cart=getOrCreateCart(me.getId());
        Product product=productRepository.findById(request.getProductId()).orElseThrow(()->
                new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) throw new BadRequestException("Product is not available");

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if(cartItem==null){
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setUnitPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
            cart.getItems().add(cartItemRepository.save(newItem));
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity()+ request.getQuantity());
        }
    }

    @Transactional
    public CartItemResponse changeQuantity(UserPrincipal me, long cartItemId, int quantity){
        Cart cart=getOrCreateCart(me.getId());
        CartItem item=cart.getItems().stream()
                .filter(i ->i.getId().equals(cartItemId))
                .findFirst().orElse(null);
        if(item==null) throw new ResourceNotFoundException("No cart item found with id : "+cartItemId);
        if (quantity <= 0) { throw new IllegalArgumentException("Quantity must be greater than zero"); }
        item.setQuantity(quantity);

        return cartItemMapper.toResponse(item);
    }

    @Transactional
    public void removeItem(UserPrincipal me, long cartItemId){
        Cart cart=getOrCreateCart(me.getId());
        CartItem cartItem=cart.getItems().stream()
                .filter(i->i.getId().equals(cartItemId)).findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("No cart item found with id : "+cartItemId));
        cart.getItems().remove(cartItem);
    }

    public void deleteCart(UserPrincipal me){
        Cart cart = cartRepository.findByUserId(me.getId()).orElseThrow(()->
                new ResourceNotFoundException("No cart found"));
        cartRepository.delete(cart);
    }

    @Transactional
    public Cart getOrCreateCart(Long userId){
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
            return cartRepository.save(c);
        });
    }


}
