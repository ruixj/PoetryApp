package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    Page<ShopItem> findByStatus(String status, Pageable pageable);
}
