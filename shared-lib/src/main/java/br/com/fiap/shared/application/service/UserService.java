package br.com.fiap.shared.application.service;

import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.UserRequest;
import br.com.fiap.shared.application.dto.UserResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.UserUseCase;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.application.port.out.UserRepository;
import br.com.fiap.shared.domain.LibraryTime;
import br.com.fiap.shared.domain.entity.User;
import br.com.fiap.shared.domain.exception.ResourceInUseException;
import br.com.fiap.shared.domain.exception.UserAlreadyExistsException;
import br.com.fiap.shared.domain.exception.UserNotFoundException;
import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.Name;
import br.com.fiap.shared.domain.valueObject.UserType;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final Clock clock;

    public UserService(UserRepository userRepository, LoanRepository loanRepository, Clock clock) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public UserResponse create(UserRequest request) {
        Email email = new Email(request.email());
        ensureEmailIsFree(email, null);
        User user = UserType.from(request.type())
                .create(UUID.randomUUID(), new Name(request.name()), email, isActive(request));
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public UserResponse getById(UUID id) {
        return UserResponse.from(findUser(id));
    }

    @Override
    public PageResult<UserResponse> list(PageQuery page) {
        return userRepository.findAll(page).map(UserResponse::from);
    }

    @Override
    public UserResponse update(UUID id, UserRequest request) {
        User current = findUser(id);
        Email email = new Email(request.email());
        ensureEmailIsFree(email, id);
        UserType type = request.type() == null ? UserType.from(current.getType()) : UserType.from(request.type());
        boolean active = request.active() == null ? current.isActive() : request.active();
        User updated = type.create(id, new Name(request.name()), email, active);
        return UserResponse.from(userRepository.save(updated));
    }

    @Override
    public void delete(UUID id) {
        findUser(id);
        if (loanRepository.existsByUserId(id)) {
            throw new ResourceInUseException("User %s has loan history and cannot be deleted".formatted(id));
        }
        userRepository.deleteById(id);
    }

    @Override
    public PageResult<LoanResponse> loanHistory(UUID userId, PageQuery page) {
        findUser(userId);
        LocalDateTime now = LibraryTime.now(clock);
        return loanRepository.findByUserId(userId, page).map(loan -> LoanResponse.from(loan, now));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private void ensureEmailIsFree(Email email, UUID currentUserId) {
        userRepository.findByEmail(email.value())
                .filter(existing -> !existing.getId().equals(currentUserId))
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException("A user with email %s already exists".formatted(email.value()));
                });
    }

    private static boolean isActive(UserRequest request) {
        return request.active() == null || request.active();
    }
}
