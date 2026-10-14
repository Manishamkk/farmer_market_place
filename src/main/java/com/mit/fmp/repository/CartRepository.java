package com.mit.fmp.repository;


import com.mit.fmp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByBuyer_Id(Long buyerId);

    Optional<CartItem> findByBuyer_IdAndProduct_Id(
            Long buyerId, Long productId);

    Optional<CartItem> findByIdAndBuyer_Id(
            Long id, Long buyerId);

    void deleteByBuyer_Id(Long buyerId);
}