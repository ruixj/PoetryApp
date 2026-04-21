package com.poetryapp.poetry.service;

import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.poetry.entity.Poem;
import com.poetryapp.poetry.entity.PoemCategory;
import com.poetryapp.poetry.repository.PoemCategoryRepository;
import com.poetryapp.poetry.repository.PoemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PoemService {

    private final PoemRepository poemRepo;
    private final PoemCategoryRepository categoryRepo;

    @Value("${upload.base-path:./uploads}")
    private String uploadBasePath;
    @Value("${upload.base-url:http://localhost:8083}")
    private String uploadBaseUrl;

    public Poem getPoem(Long poemId) {
        return poemRepo.findById(poemId)
                .orElseThrow(() -> new BusinessException("古诗不存在"));
    }

    public List<Poem> getPoemsByUnit(Long unitId) {
        return poemRepo.findByUnitId(unitId);
    }

    public List<Poem> getPoemsByCategory(String type, String value) {
        return poemRepo.findByCategoryTypeAndValue(type, value);
    }

    public List<String> getCategoryValues(String type) {
        return categoryRepo.findDistinctValuesByType(type);
    }

    public List<PoemCategory> getPoemCategories(Long poemId) {
        return categoryRepo.findByPoemId(poemId);
    }

    /** 上传录音文件，返回 URL */
    public String uploadRecording(Long userId, Long poemId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new BusinessException("文件不能为空");
        String ext = getExtension(file.getOriginalFilename());
        String filename = "rec_" + userId + "_" + poemId + "_" + UUID.randomUUID() + ext;
        Path dir = Paths.get(uploadBasePath, "recordings");
        Files.createDirectories(dir);
        Files.write(dir.resolve(filename), file.getBytes(), StandardOpenOption.CREATE);
        return uploadBaseUrl + "/uploads/recordings/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".webm";
        return filename.substring(filename.lastIndexOf("."));
    }
}
