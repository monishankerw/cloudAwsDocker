package com.cloudAwsDocker.repository;

import com.cloudAwsDocker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.mobile = :identifier")
    Optional<User> findByEmailOrMobile(@Param("identifier") String identifier);
}
