package com.poetryapp.admin.mapper;

import com.poetryapp.admin.dto.OrderSummary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderAdminMapper {

    @Select("SELECT o.id, o.order_no, o.user_id, u.nickname AS user_nickname, o.total_points, "
          + "o.shipping_name, o.shipping_phone, o.shipping_address, o.status, "
          + "DATE_FORMAT(o.created_at, '%Y-%m-%d %H:%i') AS created_at "
          + "FROM orders o LEFT JOIN users u ON u.id = o.user_id "
          + "ORDER BY o.created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<OrderSummary> findAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT o.id, o.order_no, o.user_id, u.nickname AS user_nickname, o.total_points, "
          + "o.shipping_name, o.shipping_phone, o.shipping_address, o.status, "
          + "DATE_FORMAT(o.created_at, '%Y-%m-%d %H:%i') AS created_at "
          + "FROM orders o LEFT JOIN users u ON u.id = o.user_id "
          + "WHERE o.status = #{status} "
          + "ORDER BY o.created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<OrderSummary> findByStatus(@Param("status") String status,
                                    @Param("offset") int offset,
                                    @Param("size") int size);

    @Update("UPDATE orders SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
