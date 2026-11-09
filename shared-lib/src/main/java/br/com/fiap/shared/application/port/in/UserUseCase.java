package br.com.fiap.shared.application.port.in;

import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.UserRequest;
import br.com.fiap.shared.application.dto.UserResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;

import java.util.UUID;

public interface UserUseCase {

    UserResponse create(UserRequest request);

    UserResponse getById(UUID id);

    PageResult<UserResponse> list(PageQuery page);

    UserResponse update(UUID id, UserRequest request);

    void delete(UUID id);

    PageResult<LoanResponse> loanHistory(UUID userId, PageQuery page);
}
