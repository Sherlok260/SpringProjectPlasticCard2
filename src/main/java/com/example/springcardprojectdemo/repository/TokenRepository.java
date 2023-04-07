package com.example.springcardprojectdemo.repository;

import com.example.springcardprojectdemo.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<List<Token>> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "delete from Token where email = ?1", nativeQuery = true)
    void deleteTemporaryTokenByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "delete from Token where token = ?1", nativeQuery = true)
    void deleteTemporaryTokenByToken(String token);


//    @Query(value = "select level from Token where email = ?1", nativeQuery = true)
//    String findTokenLevelByEmil(String email);
}
