package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemRepository {

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);

    @Insert("INSERT INTO order_items(order_id, item_id, item_name, item_image, quantity, points_cost) "
          + "VALUES(#{orderId}, #{itemId}, #{itemName}, #{itemImage}, #{quantity}, #{pointsCost})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderItem item);
}
