package com.example.springcardprojectdemo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class newPasswordDto {
    private String new_password;
    private long v_code;
}
