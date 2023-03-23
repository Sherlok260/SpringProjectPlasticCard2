package com.example.springcardprojectdemo.repository;

import com.example.springcardprojectdemo.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "select * from cards c where c.number like '__________' || ?1", nativeQuery = true)
    Card findByOverNumber(String number);

    @Query(value = "select * from cards c where c.number = ?1", nativeQuery = true)
    Optional<Card> findByCardNumber(String number);

    @Transactional
    @Modifying
    @Query("update cards c set c.balance = c.balance + ?2 where c.number = ?1")
    void plusMoney(String number, int how_much);

    @Transactional
    @Modifying
    @Query("update cards c set c.balance = c.balance - ?2 where c.number = ?1")
    void minusMoney(String number, int how_much);

    @Transactional
    @Modifying
    @Query(value = "insert into users_cards values(?1, ?2)", nativeQuery = true)
    void addCardToUserWithId(Long user_id, Long card_id);

    @Query(value = "select users_id from users_cards where cards_id = ?1", nativeQuery = true)
    Long getUserIdByCardId(Long cardId);

    @Query(value = " select email from users where id = (select users_id from users_cards where cards_id = (select id from cards where number = ?1))", nativeQuery = true)
    String getEmailWithCardNumber(String number);
}
