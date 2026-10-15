package com.gec.backend.admin.dto;

public record CreateUserResponse(
    AdminUserResponse user,
    String tempPassword
) {}
