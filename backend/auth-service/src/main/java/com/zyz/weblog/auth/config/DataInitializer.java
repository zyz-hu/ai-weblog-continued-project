package com.zyz.weblog.auth.config;

import com.zyz.weblog.auth.entity.Permission;
import com.zyz.weblog.auth.entity.PermissionType;
import com.zyz.weblog.auth.entity.Role;
import com.zyz.weblog.auth.entity.User;
import com.zyz.weblog.auth.repository.PermissionRepository;
import com.zyz.weblog.auth.repository.RoleRepository;
import com.zyz.weblog.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seed(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // permissions
            Permission adminApi = permissionRepository.findByCode("ADMIN_ALL")
                    .orElseGet(() -> {
                        Permission p = new Permission();
                        p.setCode("ADMIN_ALL");
                        p.setName("Admin API access");
                        p.setType(PermissionType.API);
                        p.setResource("/admin/**");
                        p.setHttpMethod("*");
                        return permissionRepository.save(p);
                    });

            Permission userInfo = permissionRepository.findByCode("USER_INFO")
                    .orElseGet(() -> {
                        Permission p = new Permission();
                        p.setCode("USER_INFO");
                        p.setName("Read user info");
                        p.setType(PermissionType.API);
                        p.setResource("/auth/me");
                        p.setHttpMethod("GET");
                        return permissionRepository.save(p);
                    });

            // roles
            Role adminRole = roleRepository.findByCode("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setCode("ROLE_ADMIN");
                        r.setName("Administrator");
                        r.setPermissions(Set.of(adminApi, userInfo));
                        return roleRepository.save(r);
                    });

            Role userRole = roleRepository.findByCode("ROLE_USER")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setCode("ROLE_USER");
                        r.setName("User");
                        r.setPermissions(Set.of(userInfo));
                        return roleRepository.save(r);
                    });

            // users
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(adminRole));
                userRepository.save(admin);
                log.info("Seeded admin user: admin / admin123");
            }
            if (!userRepository.existsByUsername("user")) {
                User normal = new User();
                normal.setUsername("user");
                normal.setPassword(passwordEncoder.encode("user123"));
                normal.setRoles(Set.of(userRole));
                userRepository.save(normal);
                log.info("Seeded user: user / user123");
            }

            log.info("Seed complete: users={}, roles={}, permissions={}",
                    userRepository.count(), roleRepository.count(), permissionRepository.count());
        };
    }
}
