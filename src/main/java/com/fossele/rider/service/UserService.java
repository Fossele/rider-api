package com.fossele.rider.service;

import com.fossele.rider.entity.User;
import com.fossele.rider.repository.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User userToCreate){
        User user = User.builder()
                .name(userToCreate.getName())
                .email(userToCreate.getEmail())
                .build();
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
