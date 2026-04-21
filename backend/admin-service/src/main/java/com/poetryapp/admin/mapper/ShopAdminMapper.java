package com.poetryapp.admin.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ShopAdminMapper {

    @Insert("INSERT INTO shop_items(name, description, image_url, points_cost, stock, status, created_at, updated_at) "
          + "VALUES(#{name}, #{description}, #{imageUrl}, #{pointsCost}, #{stock}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertItem(com.poetryapp.admin.entity.ShopItemAdmin item);

    @Select("SELECT * FROM shop_items WHERE id = #{id}")
    Optional<com.poetryapp.admin.entity.ShopItemAdmin> findItemById(Long id);

    @Select("SELECT * FROM shop_items ORDER BY id DESC LIMIT #{size} OFFSET #{offset}")
    List<com.poetryapp.admin.entity.ShopItemAdmin> findAllItems(@Param("offset") int offset, @Param("size") int size);

    @Update("UPDATE shop_items SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    void updateItemStatus(@Param("id") Long id, @Param("status") String status);
}
