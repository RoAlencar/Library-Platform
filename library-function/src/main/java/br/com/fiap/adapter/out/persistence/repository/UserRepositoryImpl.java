package br.com.fiap.adapter.out.persistence.repository;

import br.com.fiap.adapter.out.persistence.entity.UserEntity;
import br.com.fiap.adapter.out.persistence.mapper.UserMapper;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.out.UserRepository;
import br.com.fiap.shared.domain.entity.User;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.<UserEntity>findByIdOptional(user.getId()).orElseGet(UserEntity::new);
        UserMapper.copyToEntity(user, entity);
        entity.persistAndFlush();
        return UserMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return UserEntity.<UserEntity>findByIdOptional(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return UserEntity.<UserEntity>find("email", email).firstResultOptional().map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return UserEntity.count("email", email) > 0;
    }

    @Override
    public PageResult<User> findAll(PageQuery page) {
        return PanachePages.fetch(UserEntity.findAll(Sort.by("name").and("id")), page, UserMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        UserEntity.deleteById(id);
    }
}
