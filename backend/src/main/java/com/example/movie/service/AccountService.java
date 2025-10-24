package com.example.movie.service;

import com.example.movie.model.user.User;
import com.example.movie.dto.request.auth.UpdateProfileRequest;
import com.example.movie.dto.response.user.UserDTO;
import com.example.movie.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final UserRepository userRepo;
    public AccountService(UserRepository userRepo){ this.userRepo = userRepo; }

    @Transactional(readOnly = true)
    public UserDTO getMe(String username){
        User u = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return toDTO(u);
    }

    @Transactional
    public UserDTO updateMe(String username, UpdateProfileRequest req){
        User u = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (req.fullName()!=null) u.setFullName(req.fullName());
        if (req.email()!=null) u.setEmail(req.email());
        return toDTO(u);
    }

    public static UserDTO toDTO(User u){
        return new UserDTO(
                u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                u.getStatus(), u.getRole(), u.getEnabled(), u.getCreatedAt()
        );
    }
}
