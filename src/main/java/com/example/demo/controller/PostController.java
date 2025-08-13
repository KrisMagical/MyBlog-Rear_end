package com.example.demo.controller;

import com.example.demo.dto.PostDetailDto;
import com.example.demo.dto.PostSummaryDto;
import com.example.demo.service.PostService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor()
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT}, allowCredentials = "true", maxAge = 3600)
public class PostController {
    @Value("${upload.image.path}")
    private String uploadPath;
    private PostService postService;

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
            Path filepath = Paths.get(uploadPath, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            String fileUrl = "/images/" + filename;// 前端访问路径
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
        /*
            图片保存路径
            upload.image.path=/var/www/blog/images
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
}
