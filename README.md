# Library Platform

Sistema de biblioteca online (FIAP Tech Challenge – Fase 2) em **Java 21 + Quarkus 3**, organizado em **arquitetura hexagonal**.

| Módulo | Papel |
|---|---|
| [`shared-lib`](shared-lib) | Núcleo: domínio (`Book`, `User`, `Loan`, `LibraryReport`, value objects, exceções) e aplicação (ports `in`/`out`, use cases, DTOs, paginação). Java puro, sem dependência de Quarkus. |
| [`library-function`](library-function) | Adapters Quarkus: REST (`adapter/in/rest`), persistência Panache/PostgreSQL (`adapter/out/persistence`), mapeamento de exceções, OpenAPI e wiring CDI (`config`). |

## Como rodar

### Docker Compose (app + PostgreSQL)

```bash
docker compose up --build        # ou: docker-compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/q/swagger-ui
- OpenAPI: http://localhost:8080/q/openapi

O build da imagem é feito dentro do Docker (multi-stage, `library-function/src/main/docker/Dockerfile.jvm`, contexto = raiz do repositório). Para usar um mirror Maven: `MAVEN_REPO_URL=https://... docker compose up --build`.

O schema é gerado pelo Hibernate (`update` em produção, `drop-and-create` em dev/test) e os dados ficam no volume `postgres-data` (`docker compose down -v` para limpar).

### Local

```bash
./mvnw verify                                   # testes unitários + @QuarkusTest (H2 em memória)
./mvnw -pl library-function -am quarkus:dev     # dev mode (Dev Services sobe um PostgreSQL via Docker)
```

Variáveis do datasource em produção: `DB_URL`, `DB_USER`, `DB_PASSWORD`.

## Endpoints

Paginação: `?page=` (0-based, padrão 0) e `?size=` (1–100, padrão 10). Respostas paginadas: `{content, page, size, totalElements, totalPages}`.

| Método | Caminho | Descrição |
|---|---|---|
| POST | `/books` | Cadastra livro (`name`, `author`, `description`, `isbn`) |
| GET | `/books?name=&author=&isbn=&page=&size=` | Lista/busca livros (nome e autor por trecho, sem diferenciar maiúsculas; ISBN exato) |
| GET | `/books/{id}` | Detalhe do livro |
| PUT | `/books/{id}` | Atualiza dados do livro |
| DELETE | `/books/{id}` | Remove livro (409 se já tiver empréstimos/reservas no histórico) |
| POST | `/users` | Cadastra usuário (`name`, `email`, `type`: `LIBRARY_MEMBER`/`LIBRARIAN`, `active`) |
| GET | `/users?page=&size=` | Lista usuários |
| GET/PUT/DELETE | `/users/{id}` | Detalhe / atualização / remoção (409 se o usuário tiver histórico de empréstimos) |
| GET | `/users/{id}/loans` | Histórico de empréstimos do usuário |
| POST | `/loans` | Empresta livro (`userId`, `bookId`) – prazo de 14 dias |
| GET | `/loans/{id}` | Detalhe do empréstimo |
| POST | `/loans/{id}/return` | Devolve o livro |
| POST | `/loans/{id}/reserve` | Reserva o livro do empréstimo para outro usuário (`userId`) |
| GET | `/loans/active` | Empréstimos em aberto (ACTIVE/OVERDUE) |
| GET | `/loans/user/{userId}` | Empréstimos do usuário |
| GET | `/reports/most-borrowed` | Top 20 livros mais emprestados |
| GET | `/reports/active-loans` | Livros emprestados no momento com previsão de devolução |

Exemplo de fluxo:

```bash
curl -X POST localhost:8080/users -H 'Content-Type: application/json' \
     -d '{"name":"Ana Souza","email":"ana@fiap.com.br"}'
curl -X POST localhost:8080/books -H 'Content-Type: application/json' \
     -d '{"name":"Dom Casmurro","author":"Machado de Assis","isbn":"978-85-359-0277-5"}'
curl -X POST localhost:8080/loans -H 'Content-Type: application/json' \
     -d '{"userId":"<user-id>","bookId":"<book-id>"}'
curl -X POST localhost:8080/loans/<loan-id>/return
curl localhost:8080/reports/most-borrowed
```

### Erros

Formato: `{timestamp, status, error, message, path}`.

| Status | Quando |
|---|---|
| 400 | Request inválido (Bean Validation, JSON malformado, parâmetros) |
| 404 | Livro, usuário ou empréstimo inexistente |
| 409 | Conflito: livro indisponível, ISBN/e-mail duplicado, estado de empréstimo inválido, recurso em uso |
| 422 | Regra/validação de domínio (value objects, usuário inativo) |

## Regras de negócio

- **Empréstimo**: só com livro `AVAILABLE` e usuário ativo; status `ACTIVE`; `dueDate = loanDate + 14 dias` (`Loan.DEFAULT_LOAN_PERIOD_DAYS`).
- **Atraso**: `OVERDUE` é derivado em tempo de consulta (empréstimo aberto com `dueDate` no passado); a resposta traz `overdue` e `daysOverdue`.
- **Devolução**: status `RETURNED`, grava `returnDate`; o livro volta a `AVAILABLE`, ou fica `RESERVED` se houver reserva pendente.
- **Reserva** (uma por livro): se o livro está emprestado, entra na fila e é separado para o usuário na devolução; se está disponível, é separado imediatamente. O usuário retira a reserva com `POST /loans` (mesmo id, passa a `ACTIVE`). Outros usuários recebem 409.
- **Disponibilidade do livro** (`Availability`): `AVAILABLE`, `BORROWED`, `RESERVED`.
- **Relatórios** (`LibraryReport`, via Streams): top 20 por quantidade de empréstimos (empate por nome) e empréstimos em aberto ordenados pela previsão de devolução.

## Datas e fuso horário

- Fuso oficial: **`America/Sao_Paulo` (GMT-3)**, constante `LibraryTime.ZONE_ID` no domínio.
- O domínio usa `java.time.LocalDateTime` (horário de parede em São Paulo, precisão de segundos), obtido de um `Clock` injetado — o que torna regras de prazo testáveis.
- A API serializa datas em **ISO-8601 com offset**, ex.: `2026-11-09T10:15:30-03:00`.
- Containers rodam com `TZ=America/Sao_Paulo`.

## Decisões de arquitetura

- **Hexagonal**: o domínio e os use cases não conhecem Quarkus/JPA. Os services (`application/service`) dependem apenas dos ports de saída (`BookRepository`, `UserRepository`, `LoanRepository`) e são expostos via ports de entrada (`BookUseCase`, `UserUseCase`, `LoanUseCase`, `ReportUseCase`). O wiring é feito por producers CDI em `UseCaseConfiguration`.
- **Persistência**: entidades Panache separadas do domínio (`BookEntity`, `UserEntity`, `LoanEntity`) com mappers dedicados. ISBN e e-mail únicos; índices em nome/autor do livro, usuário do empréstimo, (livro, status) e (status, dueDate). Consultas de empréstimo usam `join fetch` para evitar N+1 e paginação com `io.quarkus.panache.common.Page` + `count` separado.
- **Transações** nos resources (`@Transactional`), mantendo os use cases livres de framework.
- **Exceções de domínio** com hierarquia `DomainException` → `NotFoundException` / `ConflictException`, traduzidas para HTTP por `DomainExceptionMapper`.
- **Testes**: unitários do domínio no `shared-lib` (JUnit 5) e `@QuarkusTest` + RestAssured no `library-function` com H2 em memória (profile `%test`).
