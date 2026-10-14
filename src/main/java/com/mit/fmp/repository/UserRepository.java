package com.mit.fmp.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mit.fmp.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}
