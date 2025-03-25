package com.example.web_nhom_5.dto.request;

import lombok.Data;

@Data
public class ChangePassword {
    private String Password;
    private String RepeatPassword;
}
