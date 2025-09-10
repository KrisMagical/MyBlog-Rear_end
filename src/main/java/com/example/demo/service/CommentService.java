package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.CreateCommentRequest;
import com.example.demo.mapping.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Data
@Transactional
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentMapper commentMapper;
    private NotificationService notificationService;

    public List<CommentDto> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post Not Found"));
        if (post != null) {
            return commentMapper.toCommentDtoList(post.getComments());
        } else {
            throw new RuntimeException("Comment Not Found");
        }
    }

    public CommentDto addComment(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post Not Found"));
        if (post == null) {
            throw new RuntimeException("Post Not Found.");
        }
        Comment comment = commentMapper.toCommentEntity(request);
        comment.setPost(post);
        commentRepository.save(comment);
        notificationService.sendCommentNotification(comment);
        return commentMapper.toCommentDto(comment);
    }

    public CommentDto deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment Not Found"));
        if (comment == null) {
            throw new RuntimeException("Comment Not Found");
        }
        if (!comment.getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }
        commentRepository.delete(comment);
        return commentMapper.toCommentDto(comment);
    }
}
