package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartItemRepository {

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId}")
    List<CartItem> findByUserId(Long userId);

    @Select("SELECT * FROM cart_items WHERE user_id=#{userId} AND item_id=#{itemId}")
    Optional<CartItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Insert("INSERT INTO cart_items(user_id, item_id, quantity, created_at) VALUES(#{userId}, #{itemId}, #{quantity}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CartItem item);

    @Update("UPDATE cart_items SET quantity=#{quantity} WHERE id=#{id}")
    void update(CartItem item);

    @Delete("DELETE FROM cart_items WHERE user_id=#{userId} AND item_id=#{itemId}")
    void deleteByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Delete("DELETE FROM cart_items WHERE user_id=#{userId}")
    void deleteByUserId(Long userId);
}
