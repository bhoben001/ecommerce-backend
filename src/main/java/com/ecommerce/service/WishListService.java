package com.ecommerce.service;

import com.ecommerce.security.UserPrincipal;
import com.ecommerce.dto.request.WishlistItemRequest;
import com.ecommerce.dto.response.WishlistItemResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.WishList;
import com.ecommerce.entity.WishlistItem;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.WishListItemMapper;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishListItemRepository;
import com.ecommerce.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListItemRepository wishListItemRepository;
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private  final UserRepository userRepository;
    private  final WishListItemMapper wishListItemMapper;

    @Transactional
    public WishlistItemResponse addItem(UserPrincipal me, WishlistItemRequest request) {
        WishList wishList=getOrCreateWishList(me.getId());



        Product product=productRepository.findById(request.getProductId())
                .filter(p->Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        boolean alreadyExists = wishList.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (alreadyExists) {
            throw new ConflictException("Product already exists in wishlist");
        }

        WishlistItem item=new WishlistItem();
        item.setUser(wishList.getUser());
        item.setProduct(product);
        item.setWishList(wishList);

        WishlistItem savedItem=wishListItemRepository.save(item);

        wishList.getItems().add(savedItem);
        return wishListItemMapper.toResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public Page<WishlistItemResponse> list(UserPrincipal me,Pageable pageable) {
        Page<WishlistItem> list=wishListItemRepository.findByUserId(me.getId(),pageable);
        return list.map(wishListItemMapper::toResponse);
    }

    @Transactional
    public void deleteWish(UserPrincipal me, Long wishListItemId) {

        WishList wishList = wishListRepository.findByUserId(me.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem item = wishList.getItems().stream()
                .filter(i -> i.getId().equals(wishListItemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishList.getItems().remove(item);
    }

    @Transactional
    public WishList getOrCreateWishList(Long userId){
        User user=userRepository.findById(userId).get();
        WishList wishList=wishListRepository.findByUserId(userId).orElseGet(()->{
            WishList newWishList = new WishList();
            newWishList.setUser(user);
            return wishListRepository.save(newWishList);
        });
        return wishList;
    }

}
