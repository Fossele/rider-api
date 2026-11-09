package com.fossele.rider.controller;


import com.fossele.rider.entity.User;
import com.fossele.rider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        log.info("users list retrieved");
        return  ResponseEntity.ok(users);
    }


    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody  User userToCreate){
        User createdUser = userService.createUser(userToCreate);
        log.info("user with id {} created", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }
}
