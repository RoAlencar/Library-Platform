package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.domain.entity.User;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, String type, boolean active) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName().value(), user.getEmail().value(), user.getType(),
                user.isActive());
    }
}
