# Aí 'Se Cream

Sistema web monolítico para controle de produção, estoque no centro de distribuição (CD), distribuição para lojas e baixas de estoque nas lojas da rede de sorveterias.

A interface é **server-side** (Spring MVC + Thymeleaf). A API REST exposta hoje é essencialmente o endpoint de login (`POST /auth/login`); o restante do fluxo ocorre por páginas HTML autenticadas com JWT em cookie HTTP-only.

---

## O que o sistema faz (implementado)

| Módulo | Rota principal | Descrição |
|--------|----------------|-----------|
| **Estoque atual** | `/` ou `/estoque` | Tela inicial: resumo de unidades no CD e nas lojas, lotes com saldo no CD e saldo por loja (distribuições ativas − baixas). |
| **Sabores** | `/sabores` | Listagem para todos os usuários autenticados. Cadastro, edição e inativação apenas para **ADMIN**. |
| **Lotes de produção** | `/lotes` | Listagem de todos os lotes e cadastro de novo lote (sabor ativo, quantidade, data). O saldo no CD é `quantidade_disponivel`, decrementado nas distribuições e reintegrado no cancelamento. |
| **Lojas** | `/lojas` | CRUD e inativação (**ADMIN**). Endereço estruturado: CEP, UF, cidade, logradouro, número e complemento (busca de CEP via ViaCEP no formulário). |
| **Distribuições** | `/distribuicoes` | Registrar distribuição lote → loja, listar histórico e cancelar (estorna saldo ao lote). Apenas **ADMIN**. Valida quantidade contra o disponível no CD. |
| **Baixas de estoque** | `/baixas` | Registrar consumo na loja (loja → lote com saldo → quantidade). Listagem com filtro opcional por loja. Impede baixa maior que o saldo na loja. |
| **Operadores** | `/usuarios` | Cadastro e edição de usuários com perfil OPERADOR (**ADMIN**). |
| **Autenticação** | `/login`, `/auth/login` | Login por e-mail e senha; JWT em cookie + resposta JSON no login AJAX. Logout em `/logout`. |

### O que o sistema **não** cobre

- Vendas, clientes, preços ou pagamentos
- Filtros avançados (por período, sabor, status) em lotes, distribuições ou baixas — hoje há listagem completa ou filtro simples de baixa por loja
- API REST completa para os demais recursos (apenas login em JSON)

---

## Perfis de acesso

| Perfil | Permissões principais |
|--------|----------------------|
| **ADMIN** | Sabores (CRUD), lojas, distribuições, operadores; demais telas |
| **OPERADOR** | Estoque atual, listagem de sabores, lotes (criar/listar), baixas; **sem** lojas, distribuições nem cadastro de sabores |

Rotas sensíveis são protegidas em `SecurityConfig` e, no caso de distribuições, com `@PreAuthorize("hasRole('ADMIN')")`.

---

## Regras de negócio em uso

- **CD:** cada lote inicia com `quantidade_disponivel = quantidade_produzida`; distribuições ativas reduzem esse valor; cancelamento de distribuição devolve a quantidade ao lote.
- **Loja:** saldo = soma das distribuições **ATIVAS** para aquele lote/loja − soma das baixas registradas.
- **Distribuição cancelada** não entra no saldo da loja nem consome estoque do CD (após estorno).
- Sabores e lojas **inativos** não entram em novos lotes/distribuições/baixas conforme validação nos services.

---

## Stack tecnológica

| Tecnologia | Uso no projeto |
|------------|----------------|
| **Java 21** | Linguagem e runtime |
| **Spring Boot 4.0.3** | Aplicação monolítica, Tomcat embutido |
| **Spring MVC + Thymeleaf** | Páginas HTML e formulários |
| **Spring Data JPA (Hibernate 7)** | Persistência |
| **Spring Security + JWT (jjwt)** | Autenticação; cookie `access_token` + filtro `JwtAuthFilter` |
| **BCrypt (fator 10)** | Hash de senhas |
| **Bean Validation** | DTOs, entidades e formulários (`@Valid`) |
| **MariaDB** | Banco relacional (`aisecream`) |
| **Flyway** | Migrações em `src/main/resources/db/migration` |
| **Maven Wrapper** | Build e execução via `mvnw` / `mvnw.cmd` |
| **Lombok** | Entidades e DTOs |
| **Spring Boot DevTools** | Recarregamento em desenvolvimento |

---

## Arquitetura

Monolito em camadas:

```
Browser (Thymeleaf)
    ↓
Controller (@Controller / @RestController)
    ↓
Service (@Transactional, regras de negócio)
    ↓
Repository (JpaRepository)
    ↓
MariaDB (schema Flyway)
```

Diagramas C4 (referência) em `src/architecture/` (`c4-nivel2-containers.puml`, `c4-nivel3-components.puml`).

---

## Banco de dados

Schema versionado pelo Flyway:

| Versão | Conteúdo |
|--------|----------|
| V1 | Tabelas: `usuario`, `sabor`, `loja`, `lote_producao`, `distribuicao`, `baixa_estoque` |
| V2 | Usuário admin inicial (desenvolvimento) |
| V3 | Campos históricos em `distribuicao` (`quantidade_lote_inicial`, `saldo_disponivel_cd_apos`) |
| V4 | Endereço estruturado em `loja` (remove coluna única `endereco`) |

`spring.jpa.hibernate.ddl-auto=validate` — o Hibernate não altera o schema; apenas o Flyway.

---

## Como rodar

### Pré-requisitos

- **Java 21**
- **MariaDB** em execução (ex.: porta 3306)
- Banco `aisecream` criado (ou deixe o Flyway criar/usar conforme sua instalação)

### Configuração

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/aisecream
spring.datasource.username=root
spring.datasource.password=
```

JWT (desenvolvimento):

```properties
jwt.secret=aisecream-dev-secret-key-mudar-em-producao-32bytes-min!!
jwt.expiration-ms=28800000
```

### Execução

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Aplicação em **http://localhost:8080** (porta padrão).

### Usuário inicial (após migrações)

| Campo | Valor |
|-------|--------|
| E-mail | `admin@aisecream.com` |
| Senha | `admin123` |

Altere em produção.

### Flyway (manutenção)

Se uma migração falhar no histórico:

```bash
.\mvnw.cmd flyway:repair
```

---

## Estrutura do código (resumo)

```
src/main/java/com/aisecream/
├── config/          # SecurityConfig
├── controller/      # MVC + AuthController (REST login)
├── dto/             # Formulários e views (ex.: EstoqueAtualView)
├── exception/       # ApiExceptionHandler (REST)
├── model/           # Entidades JPA e enums
├── repository/      # Spring Data JPA
├── security/        # JWT filter e serviço
└── service/         # Regras de negócio

src/main/resources/
├── application.properties
├── db/migration/    # Scripts Flyway
└── templates/       # Thymeleaf (estoque, sabor, lote, loja, …)
```

Testes: apenas `AisecreanApplicationTests` (carga de contexto Spring Boot).

---

## Portas e conflitos

Se aparecer *Port 8080 was already in use*, encerre o processo anterior ou defina `server.port=8081` em `application.properties`.
