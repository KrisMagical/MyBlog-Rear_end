package com.example.demo.service;

import com.example.demo.model.Comment;

public interface NotificationService {
    void sendCommentNotification(Comment comment);
}
