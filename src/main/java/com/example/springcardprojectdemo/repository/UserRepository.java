package com.example.springcardprojectdemo.repository;

import com.example.springcardprojectdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("delete from users u where u.email = ?1")
    void deleteByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "delete from users_roles where users_id=?1", nativeQuery = true)
    void deleteFromRole(Long id);

    @Transactional
    @Modifying
    @Query(value = "update users_roles set roles_id = 2 where users_id = ?1", nativeQuery = true)
    void updateVerifyAndRoleWithEmail(Long id);

    @Transactional
    @Modifying
    @Query(value = "update users set verified = true where email = ?1", nativeQuery = true)
    void updateVerifyTrue(String email);

    @Transactional
    @Modifying
    @Query(value = "update users set password = ?2 where email = ?1", nativeQuery = true)
    void updatePassword(String email, String new_password);

    @Transactional
    @Modifying
    @Query(value = "insert into users_histories values(?1, ?2)", nativeQuery = true)
    void addHistoryToUser(Long user_id, Long history_id);
}
