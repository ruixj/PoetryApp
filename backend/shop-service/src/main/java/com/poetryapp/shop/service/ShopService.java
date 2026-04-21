package com.poetryapp.shop.service;

import com.poetryapp.common.constant.ResponseCode;
import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.shop.dto.PlaceOrderRequest;
import com.poetryapp.shop.entity.*;
import com.poetryapp.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository itemRepo;
    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final UserShopRefRepository userRepo;

    // ── 商品 ──────────────────────────────────────────
    public Page<ShopItem> listItems(int page, int size) {
        return itemRepo.findByStatus("ON_SHELF", PageRequest.of(page, size));
    }

    public ShopItem getItem(Long itemId) {
        return itemRepo.findById(itemId)
                .filter(i -> "ON_SHELF".equals(i.getStatus()))
                .orElseThrow(() -> new BusinessException("商品不存在或已下架"));
    }

    // ── 购物车 ────────────────────────────────────────
    @Transactional
    public void addToCart(Long userId, Long itemId, int quantity) {
        ShopItem item = getItem(itemId);
        if (item.getStock() <= 0) throw new BusinessException(ResponseCode.STOCK_NOT_ENOUGH, "商品库存不足");

        cartRepo.findByUserIdAndItemId(userId, itemId)
                .ifPresentOrElse(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    cartRepo.save(existing);
                }, () -> {
                    CartItem cart = new CartItem();
                    cart.setUserId(userId);
                    cart.setItemId(itemId);
                    cart.setQuantity(quantity);
                    cartRepo.save(cart);
                });
    }

    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        cartRepo.deleteByUserIdAndItemId(userId, itemId);
    }

    public List<CartItem> getCart(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    // ── 下单 ──────────────────────────────────────────
    @Transactional
    public Order placeOrder(Long userId, PlaceOrderRequest req) {
        List<CartItem> cartItems = cartRepo.findByUserId(userId);
        if (cartItems.isEmpty()) throw new BusinessException("购物车为空");

        // 计算总积分
        int totalPoints = 0;
        for (CartItem ci : cartItems) {
            ShopItem item = itemRepo.findById(ci.getItemId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            if (!"ON_SHELF".equals(item.getStatus()))
                throw new BusinessException("商品「" + item.getName() + "」已下架");
            if (item.getStock() < ci.getQuantity())
                throw new BusinessException(ResponseCode.STOCK_NOT_ENOUGH,
                        "商品「" + item.getName() + "」库存不足");
            totalPoints += item.getPointsCost() * ci.getQuantity();
        }

        // 检查用户积分
        UserShopRef user = userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (user.getYuanbaoPoints() < totalPoints) {
            throw new BusinessException(ResponseCode.POINTS_NOT_ENOUGH,
                    "元宝积分不足，需要 " + totalPoints + " 元宝，当前只有 " + user.getYuanbaoPoints() + " 元宝");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setUserId(userId);
        order.setTotalPoints(totalPoints);
        order.setShippingName(req.getShippingName());
        order.setShippingPhone(req.getShippingPhone());
        order.setShippingAddress(req.getShippingAddress());
        orderRepo.save(order);

        // 创建订单项，扣减库存
        for (CartItem ci : cartItems) {
            ShopItem item = itemRepo.findById(ci.getItemId()).get();
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setItemId(ci.getItemId());
            oi.setItemName(item.getName());
            oi.setItemImage(item.getImageUrl());
            oi.setQuantity(ci.getQuantity());
            oi.setPointsCost(item.getPointsCost());
            orderItemRepo.save(oi);

            item.setStock(item.getStock() - ci.getQuantity());
            itemRepo.save(item);
        }

        // 扣减积分
        userRepo.deductPoints(userId, totalPoints);

        // 清空购物车
        cartRepo.deleteByUserId(userId);

        log.info("订单创建成功: userId={}, orderNo={}, totalPoints={}", userId, order.getOrderNo(), totalPoints);
        return order;
    }

    public List<Order> getOrders(Long userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }
}
