package com.poetryapp.admin.controller;

import com.poetryapp.admin.dto.*;
import com.poetryapp.admin.service.AdminService;
import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.common.exception.GlobalExceptionHandler;
import com.poetryapp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Import(GlobalExceptionHandler.class)
public class AdminController {

    private final AdminService adminService;

    // ── 权限检查：仅 ADMIN 角色可访问 ──────────────────────
    private void requireAdmin(String role) {
        if (!"ADMIN".equals(role)) throw new BusinessException(403, "无权限访问管理后台");
    }

    // ── 用户管理 ─────────────────────────────────────────────
    @GetMapping("/users")
    public ApiResponse<Page<UserSummary>> listUsers(
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireAdmin(role);
        return ApiResponse.success(adminService.listUsers(page, size));
    }

    // ── 教材管理 ─────────────────────────────────────────────
    @PostMapping("/textbooks")
    public ApiResponse<Void> createTextbook(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody TextbookRequest req) {
        requireAdmin(role);
        adminService.createTextbook(req);
        return ApiResponse.success("教材创建成功", null);
    }

    @PostMapping("/grades")
    public ApiResponse<Void> createGrade(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody GradeRequest req) {
        requireAdmin(role);
        adminService.createGrade(req);
        return ApiResponse.success("年级创建成功", null);
    }

    @PostMapping("/units")
    public ApiResponse<Void> createUnit(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UnitRequest req) {
        requireAdmin(role);
        adminService.createUnit(req);
        return ApiResponse.success("单元创建成功", null);
    }

    // ── 古诗管理 ─────────────────────────────────────────────
    @PostMapping("/poems")
    public ApiResponse<Long> createPoem(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody PoemRequest req) {
        requireAdmin(role);
        return ApiResponse.success(adminService.createPoem(req));
    }

    @PutMapping("/poems/{poemId}")
    public ApiResponse<Void> updatePoem(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long poemId,
            @RequestBody PoemRequest req) {
        requireAdmin(role);
        adminService.updatePoem(poemId, req);
        return ApiResponse.success("古诗更新成功", null);
    }

    @PostMapping("/poems/{poemId}/audio")
    public ApiResponse<String> uploadAudio(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long poemId,
            @RequestParam("file") MultipartFile file) throws IOException {
        requireAdmin(role);
        return ApiResponse.success(adminService.uploadAudio(poemId, file));
    }

    @PostMapping("/poems/{poemId}/animation")
    public ApiResponse<String> uploadAnimation(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long poemId,
            @RequestParam("file") MultipartFile file) throws IOException {
        requireAdmin(role);
        return ApiResponse.success(adminService.uploadAnimation(poemId, file));
    }

    @PostMapping("/units/{unitId}/poems/{poemId}")
    public ApiResponse<Void> addPoemToUnit(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long unitId,
            @PathVariable Long poemId,
            @RequestParam(defaultValue = "0") int orderNum) {
        requireAdmin(role);
        adminService.addPoemToUnit(unitId, poemId, orderNum);
        return ApiResponse.success("已关联到单元", null);
    }

    @PostMapping("/poems/{poemId}/categories")
    public ApiResponse<Void> addPoemCategory(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long poemId,
            @RequestParam String categoryType,
            @RequestParam String categoryValue) {
        requireAdmin(role);
        adminService.addPoemCategory(poemId, categoryType, categoryValue);
        return ApiResponse.success("分类标签已添加", null);
    }

    // ── 商城管理 ─────────────────────────────────────────────
    @PostMapping("/shop/items")
    public ApiResponse<Void> createShopItem(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody ShopItemRequest req) {
        requireAdmin(role);
        adminService.createShopItem(req);
        return ApiResponse.success("商品创建成功", null);
    }

    @PutMapping("/shop/items/{itemId}/status")
    public ApiResponse<Void> updateItemStatus(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long itemId,
            @RequestParam String status) {
        requireAdmin(role);
        adminService.updateItemStatus(itemId, status);
        return ApiResponse.success("商品状态更新成功", null);
    }

    @GetMapping("/shop/orders")
    public ApiResponse<Page<OrderSummary>> listOrders(
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        requireAdmin(role);
        return ApiResponse.success(adminService.listOrders(page, size, status));
    }

    @PutMapping("/shop/orders/{orderId}/status")
    public ApiResponse<Void> updateOrderStatus(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderId,
            @RequestParam String status) {
        requireAdmin(role);
        adminService.updateOrderStatus(orderId, status);
        return ApiResponse.success("订单状态更新成功", null);
    }
}
