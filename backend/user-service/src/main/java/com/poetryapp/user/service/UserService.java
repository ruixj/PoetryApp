package com.poetryapp.user.service;

import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.user.dto.UpdateProfileRequest;
import com.poetryapp.user.dto.UserProfileResponse;
import com.poetryapp.user.entity.User;
import com.poetryapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    @Value("${upload.base-path:./uploads}")
    private String uploadBasePath;

    @Value("${upload.base-url:http://localhost:8082}")
    private String uploadBaseUrl;

    public UserProfileResponse getProfile(Long userId) {
        User user = getUser(userId);
        return toResponse(user, 0); // completedPoems 由 poetry-service 提供
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = getUser(userId);
        if (req.getNickname() != null && !req.getNickname().isBlank()) {
            user.setNickname(req.getNickname());
        }
        if (req.getTextbookId() != null) {
            user.setTextbookId(req.getTextbookId());
        }
        if (req.getGradeId() != null) {
            user.setGradeId(req.getGradeId());
        }
        // 首次设置完成后标记
        user.setIsFirstLogin(false);
        userRepo.update(user);
        return toResponse(user, 0);
    }

    @Transactional
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new BusinessException("文件不能为空");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + ext;
        Path dir = Paths.get(uploadBasePath, "avatars");
        Files.createDirectories(dir);
        Files.write(dir.resolve(filename), file.getBytes(), StandardOpenOption.CREATE);

        String url = uploadBaseUrl + "/uploads/avatars/" + filename;
        User user = getUser(userId);
        user.setAvatarUrl(url);
        userRepo.update(user);

        log.info("头像上传成功: userId={}, url={}", userId, url);
        return url;
    }

    public UserProfileResponse getPublicProfile(Long userId) {
        return getProfile(userId);
    }

    private User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private UserProfileResponse toResponse(User user, int completedPoems) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setId(user.getId());
        resp.setPhone(mask(user.getPhone()));
        resp.setNickname(user.getNickname());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setYuanbaoPoints(user.getYuanbaoPoints());
        resp.setTotalStudyMinutes(user.getTotalStudyMinutes());
        resp.setRole(user.getRole());
        resp.setIsFirstLogin(user.getIsFirstLogin());
        resp.setTextbookId(user.getTextbookId());
        resp.setGradeId(user.getGradeId());
        resp.setCompletedPoems(completedPoems);
        resp.setStudyLevel(calcLevel(completedPoems));
        return resp;
    }

    private String calcLevel(int count) {
        if (count < 10)  return "童生";
        if (count < 30)  return "秀才";
        if (count < 50)  return "举人";
        if (count < 70)  return "贡士";
        return "进士";
    }

    private String mask(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
