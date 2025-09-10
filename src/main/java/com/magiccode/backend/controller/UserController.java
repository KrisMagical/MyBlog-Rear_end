package com.magiccode.backend.controller;

import com.magiccode.backend.model.User;
import com.magiccode.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.verify(user);
    }
}
