package com.zyz.weblog.auth.repository;

import com.zyz.weblog.auth.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @EntityGraph(attributePaths = {"permissions"})
    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);
}
