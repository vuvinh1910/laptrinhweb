package com.example.web_nhom_5.dto;

import lombok.Builder;

@Builder
public record EmailBody(String to, String subject, String text) {
}
