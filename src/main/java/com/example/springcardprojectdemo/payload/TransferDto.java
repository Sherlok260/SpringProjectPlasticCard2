package com.example.springcardprojectdemo.payload;

import lombok.Data;

@Data
public class TransferDto {
    private String sender_card_number;
    private String trans_card_number;
    private int trans_sum;
    private int code;
}
