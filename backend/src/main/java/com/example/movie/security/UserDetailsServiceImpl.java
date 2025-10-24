//package com.example.movie.security;
//
//import com.example.movie.repository.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        var u = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(u.getUsername())
//                .password(u.getPassword())       // BCrypt hash lưu trong cột `password`
//                .roles(u.getRole() != null ? u.getRole() : "USER")      // Enum Role: USER/ADMIN -> sẽ thêm tiền tố ROLE_
//                .accountLocked(false)
//                .disabled(false)
//                .build();
//    }
//}
