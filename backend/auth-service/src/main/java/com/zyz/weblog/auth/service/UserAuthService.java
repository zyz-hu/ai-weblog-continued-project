package com.zyz.weblog.auth.service;

import com.zyz.weblog.auth.entity.Permission;
import com.zyz.weblog.auth.entity.Role;
import com.zyz.weblog.auth.entity.User;
import com.zyz.weblog.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> validateCredentials(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(User::isEnabled)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
    }

    public Optional<User> findActiveUser(String username) {
        return userRepository.findByUsername(username)
                .filter(User::isEnabled);
    }

    public List<String> extractRoleCodes(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getRoles().stream()
                .map(Role::getCode)
                .toList();
    }

    public List<String> extractPermissionResources(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getRoles().stream()
                .map(Role::getPermissions)
                .flatMap(Set::stream)
                .map(Permission::getResource)
                .distinct()
                .toList();
    }
}
