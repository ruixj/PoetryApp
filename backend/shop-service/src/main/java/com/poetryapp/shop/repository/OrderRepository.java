package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderRepository {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Optional<Order> findById(Long id);

    @Select("SELECT * FROM orders WHERE user_id=#{userId} ORDER BY created_at DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Select("SELECT * FROM orders ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Order> findAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT * FROM orders WHERE status=#{status} ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Order> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("size") int size);

    @Insert("INSERT INTO orders(order_no, user_id, total_points, shipping_name, shipping_phone, "
          + "shipping_address, status, created_at, updated_at) "
          + "VALUES(#{orderNo}, #{userId}, #{totalPoints}, #{shippingName}, #{shippingPhone}, "
          + "#{shippingAddress}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    @Update("UPDATE orders SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    void updateStatus(Order order);
}
