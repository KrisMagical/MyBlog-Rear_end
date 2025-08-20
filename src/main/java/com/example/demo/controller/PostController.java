package com.example.demo.controller;

import com.example.demo.dto.PostDetailDto;
import com.example.demo.dto.PostSummaryDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    @Value("${upload.image.path}")
    private String imageUploadPath;

    @Value("${upload.video.path}")
    private String videoUploadPath;
    private final PostService postService;

    @GetMapping("/category/{slug}")
    public ResponseEntity<List<PostSummaryDto>> getPostByCategory(@PathVariable String slug) {
        List<PostSummaryDto> postSummaryDto = postService.getPostByCategorySlug(slug);
        if (postSummaryDto == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(postSummaryDto, HttpStatus.OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable String slug) throws RuntimeException {
        PostDetailDto postDetailDto = postService.getPostBySlug(slug);
        if (postDetailDto == null) {
            throw new RuntimeException("Slug Not Found.");
        }
        return new ResponseEntity<>(postDetailDto, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<PostDetailDto> createPost(@RequestParam String categorySlug, @RequestBody PostDetailDto postDetailDto) {
        PostDetailDto postDetailDto_create = postService.createPost(postDetailDto, categorySlug);
        if (postDetailDto_create == null) {
            throw new RuntimeException("Create Failed");
        }
        return new ResponseEntity<>(postDetailDto_create, HttpStatus.CREATED);
    }

    @PostMapping(value = "/create-md", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDetailDto> createPostFromMarkdown(@RequestParam String categorySlug, @RequestParam("file") MultipartFile mdFile) throws IOException {
        String mdContent = new String(mdFile.getBytes(), StandardCharsets.UTF_8);
        PostDetailDto dto = new PostDetailDto();
        dto.setContent(mdContent);
        return new ResponseEntity<>(postService.createPost(dto, categorySlug), HttpStatus.OK);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("File is empty", HttpStatus.NOT_FOUND);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(imageUploadPath, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            String fileUrl = "/images/" + filename;// 前端访问路径
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
        /*
            图片保存路径
            upload.image.path=/var/www/blog/image → /images/{filename}s
        */
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PostDetailDto> updatePost(@PathVariable Long id, @RequestParam(required = false) String categorySlug, @RequestBody PostDetailDto postDetailDto) {
        PostDetailDto updatedPost = postService.updatePost(id, postDetailDto, categorySlug);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PutMapping(value = "/update-md/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDetailDto> updatePostFromMarkdown(@PathVariable Long id, @RequestParam(required = false) String categorySlug, @RequestParam("file") MultipartFile mdFile) throws IOException {
        String mdContent = new String(mdFile.getBytes(), StandardCharsets.UTF_8);
        PostDetailDto updatedPost = postService.updatePostFromMarkDown(id, mdContent, categorySlug);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PostMapping("/upload/video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
            }
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";
            if (!List.of("mp4", "webm", "ogg").contains(ext.toLowerCase())) {
                return new ResponseEntity<>("Unsupported video format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }

            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path filepath = Paths.get(videoUploadPath, "Videos", filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            String fileUrl = "/videos/" + filename;// 前端访问路径
            return new ResponseEntity<>(fileUrl,HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload Failed");
        }
    }
    /*
    Markdown 文件上传时
    因为/create-md 和 /update-md 已经把 Markdown 内容读进数据库了，所以只要 Markdown 文件里包含视频 URL，比如：
    @[video](/videos/test.mp4)
    @[video](https://www.youtube.com/embed/abc123)
    前端渲染时就能识别。后端这里 不需要特殊解析，因为 content 就是 Markdown 原文，交由前端去渲染 <video> 或 <iframe>。
    视频文件夹：/var/www/blog/videos → /videos/{filename}
     */
}
