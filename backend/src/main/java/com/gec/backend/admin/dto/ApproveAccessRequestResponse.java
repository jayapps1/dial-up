package com.gec.backend.admin.dto;

public record ApproveAccessRequestResponse(
    AdminAccessRequestResponse request,
    AdminUserResponse createdUser,
    String temporaryPassword
) {}
