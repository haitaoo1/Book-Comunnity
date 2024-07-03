package com.haitao.book.repositories;

import com.haitao.book.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("")
    Optional<Role> findByname(String role);

}
