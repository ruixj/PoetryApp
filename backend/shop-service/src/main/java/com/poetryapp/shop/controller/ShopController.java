package com.poetryapp.shop.controller;

import com.poetryapp.common.exception.GlobalExceptionHandler;
import com.poetryapp.common.response.ApiResponse;
import com.poetryapp.shop.dto.PlaceOrderRequest;
import com.poetryapp.shop.entity.*;
import com.poetryapp.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@Import(GlobalExceptionHandler.class)
public class ShopController {

    private final ShopService shopService;

    /** 商品列表 */
    @GetMapping("/items")
    public ApiResponse<List<ShopItem>> listItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(shopService.listItems(page, size));
    }

    /** 商品详情 */
    @GetMapping("/items/{itemId}")
    public ApiResponse<ShopItem> getItem(@PathVariable Long itemId) {
        return ApiResponse.success(shopService.getItem(itemId));
    }

    /** 加入购物车 */
    @PostMapping("/cart")
    public ApiResponse<Void> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long itemId,
            @RequestParam(defaultValue = "1") int quantity) {
        shopService.addToCart(userId, itemId, quantity);
        return ApiResponse.success("已添加到购物车", null);
    }

    /** 查看购物车 */
    @GetMapping("/cart")
    public ApiResponse<List<CartItem>> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(shopService.getCart(userId));
    }

    /** 移除购物车商品 */
    @DeleteMapping("/cart/{itemId}")
    public ApiResponse<Void> removeFromCart(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {
        shopService.removeFromCart(userId, itemId);
        return ApiResponse.success("已移除", null);
    }

    /** 下单 */
    @PostMapping("/orders")
    public ApiResponse<Order> placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PlaceOrderRequest req) {
        return ApiResponse.success(shopService.placeOrder(userId, req));
    }

    /** 我的订单 */
    @GetMapping("/orders")
    public ApiResponse<List<Order>> getOrders(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(shopService.getOrders(userId));
    }

    /** 订单详情 */
    @GetMapping("/orders/{orderId}/items")
    public ApiResponse<List<OrderItem>> getOrderItems(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        return ApiResponse.success(shopService.getOrderItems(orderId));
    }
}
