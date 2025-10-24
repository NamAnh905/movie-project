package com.example.movie.controller.auth;

import com.example.movie.dto.request.auth.UpdateProfileRequest;
import com.example.movie.dto.response.user.UserDTO;
import com.example.movie.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AccountService accountService;
    public UserController(AccountService accountService){ this.accountService = accountService; }

    @GetMapping("/me")
    public UserDTO me(Authentication auth){
        return accountService.getMe(auth.getName());
    }

    @PutMapping("/me")
    public UserDTO updateMe(Authentication auth, @Valid @RequestBody UpdateProfileRequest req){
        return accountService.updateMe(auth.getName(), req);
    }
}
