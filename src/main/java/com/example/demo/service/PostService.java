package com.example.demo.service;

import com.example.demo.dto.PostDetailDto;
import com.example.demo.dto.PostSummaryDto;
import com.example.demo.mapping.PostDetailMapper;
import com.example.demo.mapping.PostSummaryMapper;
import com.example.demo.model.Category;
import com.example.demo.model.Post;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor()
@RequiredArgsConstructor
@Transactional
public class PostService {
    private PostRepository postRepository;
    private CategoryRepository categoryRepository;
    private PostSummaryMapper postSummaryMapper;
    private PostDetailMapper postDetailMapper;

    public List<PostSummaryDto> getPostByCategorySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        if (category != null) {
            return postSummaryMapper.toPostSummaryDtoList(postRepository.findByCategory(category));
        } else {
            throw new RuntimeException("Post Not Found.");
        }
    }

    public PostDetailDto getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            return postDetailMapper.toPostDetailDto(post);
        } else {
            throw new RuntimeException("Post Not Found.");
        }
    }

    public PostDetailDto createPost(PostDetailDto postDetailDto, String categorySlug) {
        Category category = categoryRepository.findBySlug(categorySlug);
        if (category == null) {
            throw new RuntimeException("Category Not Found.");
        }
        Post post = postDetailMapper.toPostEntity(postDetailDto);
        post.setCategory(category);
        postRepository.save(post);
        return postDetailMapper.toPostDetailDto(post);
    }

    public PostDetailDto updatePost(Long id, PostDetailDto updatePostDetailDto, String categorySlug) {
        Post existingPost = postRepository.findById(id);
        if (existingPost == null) {
            throw new RuntimeException("Post Not Found.");
        }
        if (categorySlug != null || !categorySlug.isBlank()) {
            Category category = categoryRepository.findBySlug(categorySlug);
            if (category == null) {
                throw new RuntimeException("Category Not Found");
            }
            existingPost.setCategory(category);
        }
        if (updatePostDetailDto.getTitle() != null) {
            existingPost.setTitle(updatePostDetailDto.getTitle());
        }
        if (updatePostDetailDto.getSlug() != null) {
            existingPost.setSlug(updatePostDetailDto.getSlug());
        }
        if (updatePostDetailDto.getContent() != null) {
            existingPost.setContent(updatePostDetailDto.getContent());
        }
        postRepository.save(existingPost);
        return postDetailMapper.toPostDetailDto(existingPost);
    }

    public PostDetailDto updatePostFromMarkDown(Long id, String mdContext, String categorySlug) {
        Post existingPost = postRepository.findById(id);
        if (existingPost == null) {
            throw new RuntimeException("Post Not Found");
        }
        if (categorySlug != null || !categorySlug.isBlank()) {
            Category category = categoryRepository.findBySlug(categorySlug);
            if (category == null) {
                throw new RuntimeException("category Not Found");
            }
            existingPost.setCategory(category);
        }
        existingPost.setContent(mdContext);
        postRepository.save(existingPost);
        return postDetailMapper.toPostDetailDto(existingPost);
    }

}
