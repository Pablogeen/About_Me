package com.ben.my_portfolio.users.domain;

import com.ben.my_portfolio.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    Optional<String> findEmailById(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET is_verified = true WHERE email = :email", nativeQuery = true)
    void verifyUser(@Param("email") String email);
}
