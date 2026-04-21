package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.ShopItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ShopItemRepository {

    @Select("SELECT * FROM shop_items WHERE id = #{id}")
    Optional<ShopItem> findById(Long id);

    @Select("SELECT * FROM shop_items WHERE status = #{status} LIMIT #{size} OFFSET #{offset}")
    List<ShopItem> findByStatus(@Param("status") String status,
                                @Param("offset") int offset,
                                @Param("size") int size);

    @Select("SELECT COUNT(*) FROM shop_items WHERE status = #{status}")
    long countByStatus(String status);

    @Select("SELECT * FROM shop_items ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<ShopItem> findAll(@Param("offset") int offset, @Param("size") int size);

    @Insert("INSERT INTO shop_items(name, description, image_url, points_cost, stock, status, created_at, updated_at) "
          + "VALUES(#{name}, #{description}, #{imageUrl}, #{pointsCost}, #{stock}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ShopItem item);

    @Update("UPDATE shop_items SET name=#{name}, description=#{description}, image_url=#{imageUrl}, "
          + "points_cost=#{pointsCost}, stock=#{stock}, status=#{status}, updated_at=NOW() WHERE id=#{id}")
    void update(ShopItem item);
}
