package br.com.fiap.shared.application.port.out;

import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    PageResult<User> findAll(PageQuery page);

    void deleteById(UUID id);
}
