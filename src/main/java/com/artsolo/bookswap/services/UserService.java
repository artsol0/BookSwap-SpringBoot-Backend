package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String changeUserActivity(User user) {
        if (user != null) {
            if (user.getActivity()) {
                user.setActivity(Boolean.FALSE);
                userRepository.save(user);
                return "User " + user.getNickname() + " with id " + user.getId() + " was banned";
            }
            user.setActivity(Boolean.TRUE);
            userRepository.save(user);
            return "User " + user.getNickname() + " with id " + user.getId() + " was unbanned";
        }
        throw new NullPointerException("User cannot be null");
    }

    public String changeUserPassword(Map<String, String> request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if(passwordEncoder.matches(request.get("old-password"), user.getPassword())) {
            if (!passwordEncoder.matches(request.get("new-password"), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.get("new-password")));
                userRepository.save(user);
                return "Success";
            }
            return "New password must not match previous";
        }
        return "Password not confirmed";
    }
}
