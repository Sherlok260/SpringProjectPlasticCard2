package com.example.springcardprojectdemo.repository;

import com.example.springcardprojectdemo.entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
    Optional<Verify> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("delete from Verify v where v.email = ?1")
    void deleteByEmail(String email);

}
