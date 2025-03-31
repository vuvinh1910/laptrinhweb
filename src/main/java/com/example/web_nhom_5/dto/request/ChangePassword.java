package com.example.web_nhom_5.dto.request;

import lombok.Data;

@Data
public class ChangePassword {
    private String password;
    private String repeatPassword;
}
