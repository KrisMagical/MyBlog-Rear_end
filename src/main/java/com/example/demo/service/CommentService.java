package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.CreateCommentRequest;
import com.example.demo.mapping.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
@Data
@Transactional
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentMapper commentMapper;
    private JavaMailSender mailSender;

    public List<CommentDto> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId);
        if (post != null) {
            return commentMapper.toCommentDtoList(post.getComments());
        } else {
            throw new RuntimeException("Comment Not Found");
        }
    }

    public CommentDto addComment(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new RuntimeException("Post Not Found.");
        }
        Comment comment = commentMapper.toCommentEntity(request);
        comment.setPost(post);
        commentRepository.save(comment);
        sendCommentNotificationEmail(comment);
        return commentMapper.toCommentDto(comment);
    }

    private void sendCommentNotificationEmail(Comment comment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("sunqixian4@gmail.com");
            helper.setSubject("New Comment On Post: " + comment.getPost().getTitle());
            String formattedDate = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String content = String.format(
                    "Name: %s\nEmail: %s\nDate: %s\nContent: %s\nPost Link: http://yourdomain.com/posts/%s",
                    comment.getName(),
                    comment.getEmail(),
                    formattedDate,
                    comment.getContent(),
                    comment.getPost().getSlug()
            );
            helper.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    public void deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment Not Found");
        }
        if (!comment.getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }
        commentRepository.delete(comment);
    }
}
