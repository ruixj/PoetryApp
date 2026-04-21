package com.poetryapp.user.controller;

import com.poetryapp.common.response.ApiResponse;
import com.poetryapp.user.dto.UpdateProfileRequest;
import com.poetryapp.user.dto.UserProfileResponse;
import com.poetryapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 获取当前用户个人信息 */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(userService.getProfile(userId));
    }

    /** 更新个人信息（昵称、教材、年级） */
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UpdateProfileRequest req) {
        return ApiResponse.success(userService.updateProfile(userId, req));
    }

    /** 上传头像 */
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = userService.uploadAvatar(userId, file);
        return ApiResponse.success("头像上传成功", url);
    }

    /** 获取任意用户公开信息（游戏页面展示用） */
    @GetMapping("/{userId}/public")
    public ApiResponse<UserProfileResponse> getPublic(@PathVariable Long userId) {
        return ApiResponse.success(userService.getPublicProfile(userId));
    }
}
