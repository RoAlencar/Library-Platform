package br.com.fiap.adapter.out.persistence.mapper;

import br.com.fiap.adapter.out.persistence.entity.UserEntity;
import br.com.fiap.shared.domain.entity.User;
import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.Name;
import br.com.fiap.shared.domain.valueObject.UserType;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserEntity entity) {
        return UserType.from(entity.type).create(entity.id, new Name(entity.name), new Email(entity.email), entity.active);
    }

    public static void copyToEntity(User user, UserEntity entity) {
        entity.id = user.getId();
        entity.name = user.getName().value();
        entity.email = user.getEmail().value();
        entity.type = user.getType();
        entity.active = user.isActive();
    }
}
