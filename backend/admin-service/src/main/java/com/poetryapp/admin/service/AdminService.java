package com.poetryapp.admin.service;

import com.poetryapp.admin.dto.*;
import com.poetryapp.admin.entity.PoemAdmin;
import com.poetryapp.admin.entity.ShopItemAdmin;
import com.poetryapp.admin.mapper.*;
import com.poetryapp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserAdminMapper userMapper;
    private final PoemAdminMapper poemMapper;
    private final ShopAdminMapper shopMapper;
    private final OrderAdminMapper orderMapper;
    private final TextbookAdminMapper textbookMapper;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    // ── 用户管理 ───────────────────────────────────────────────────
    public List<UserSummary> listUsers(int page, int size) {
        return userMapper.findAll(page * size, size);
    }

    // ── 教材/年级/单元 ─────────────────────────────────────────────
    public void createTextbook(TextbookRequest req) {
        textbookMapper.insertTextbook(req.getName(), req.getDescription(), req.getOrderNum());
    }

    public void createGrade(GradeRequest req) {
        textbookMapper.insertGrade(req.getName(), req.getTextbookId(), req.getOrderNum());
    }

    public void createUnit(UnitRequest req) {
        textbookMapper.insertUnit(req.getName(), req.getGradeId(), req.getOrderNum());
    }

    // ── 古诗管理 ───────────────────────────────────────────────────
    @Transactional
    public Long createPoem(PoemRequest req) {
        PoemAdmin poem = new PoemAdmin();
        poem.setTitle(req.getTitle());
        poem.setDynasty(req.getDynasty());
        poem.setAuthor(req.getAuthor());
        poem.setContent(req.getContent());
        poem.setPinyin(req.getPinyin());
        poem.setTranslation(req.getTranslation());
        poem.setBackground(req.getBackground());
        poem.setAuthorIntro(req.getAuthorIntro());
        poem.setMindmapData(req.getMindmapData());
        poem.setDifficultyWords(req.getDifficultyWords());
        poemMapper.insert(poem);
        return poem.getId();
    }

    public void updatePoem(Long poemId, PoemRequest req) {
        PoemAdmin poem = poemMapper.findById(poemId)
                .orElseThrow(() -> new BusinessException("古诗不存在"));
        poem.setTitle(req.getTitle());
        poem.setDynasty(req.getDynasty());
        poem.setAuthor(req.getAuthor());
        poem.setContent(req.getContent());
        poem.setPinyin(req.getPinyin());
        poem.setTranslation(req.getTranslation());
        poem.setBackground(req.getBackground());
        poem.setAuthorIntro(req.getAuthorIntro());
        poem.setMindmapData(req.getMindmapData());
        poem.setDifficultyWords(req.getDifficultyWords());
        poemMapper.update(poem);
    }

    public String uploadAudio(Long poemId, MultipartFile file) throws IOException {
        poemMapper.findById(poemId).orElseThrow(() -> new BusinessException("古诗不存在"));
        String url = saveFile(file, "audio");
        poemMapper.updateAudioUrl(poemId, url);
        return url;
    }

    public String uploadAnimation(Long poemId, MultipartFile file) throws IOException {
        poemMapper.findById(poemId).orElseThrow(() -> new BusinessException("古诗不存在"));
        String url = saveFile(file, "animation");
        poemMapper.updateAnimationUrl(poemId, url);
        return url;
    }

    public void addPoemToUnit(Long unitId, Long poemId, int orderNum) {
        textbookMapper.linkPoemToUnit(unitId, poemId, orderNum);
    }

    public void addPoemCategory(Long poemId, String categoryType, String categoryValue) {
        textbookMapper.insertPoemCategory(poemId, categoryType, categoryValue);
    }

    public List<PoemAdmin> listPoems(int page, int size) {
        return poemMapper.findAll(page * size, size);
    }

    public void deletePoem(Long poemId) {
        poemMapper.deleteById(poemId);
    }

    // ── 商城管理 ───────────────────────────────────────────────────
    public void createShopItem(ShopItemRequest req) {
        ShopItemAdmin item = new ShopItemAdmin();
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setImageUrl(req.getImageUrl());
        item.setPointsCost(req.getPointsCost());
        item.setStock(req.getStock());
        shopMapper.insertItem(item);
    }

    public void updateItemStatus(Long itemId, String status) {
        shopMapper.findItemById(itemId).orElseThrow(() -> new BusinessException("商品不存在"));
        shopMapper.updateItemStatus(itemId, status);
    }

    public List<ShopItemAdmin> listShopItems(int page, int size) {
        return shopMapper.findAllItems(page * size, size);
    }

    // ── 订单管理 ───────────────────────────────────────────────────
    public List<OrderSummary> listOrders(int page, int size, String status) {
        if (status != null && !status.isBlank()) {
            return orderMapper.findByStatus(status, page * size, size);
        }
        return orderMapper.findAll(page * size, size);
    }

    public void updateOrderStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }

    // ── 文件存储 ───────────────────────────────────────────────────
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) throw new BusinessException("文件不能为空");
        String ext = getExtension(file.getOriginalFilename());
        String filename = subDir + "_" + UUID.randomUUID() + ext;
        Path dir = Paths.get(uploadPath, subDir);
        Files.createDirectories(dir);
        Files.write(dir.resolve(filename), file.getBytes(), StandardOpenOption.CREATE);
        return "/uploads/" + subDir + "/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
