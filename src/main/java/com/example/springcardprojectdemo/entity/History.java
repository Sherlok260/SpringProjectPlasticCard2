package com.example.springcardprojectdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender_name;
    private String sender_card_number;
    private String sender_bank_name;
    private String receiver_name;
    private String receiver_card_number;
    private String receiver_bank_name;
    private double commission;
    private double amount;
    private String condition;
    private long transaction_date;
}
