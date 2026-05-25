# Aí 'Se Cream

Sistema web monolítico para gerenciamento de produção e distribuição de sorvetes de uma fábrica central para as lojas da rede.

A interface principal é **server-side** (Spring MVC + Thymeleaf), com autenticação JWT (cookie HTTP-only e `POST /auth/login` em JSON). O restante dos fluxos ocorre em páginas HTML autenticadas.

---

## Descrição do Projeto

Uma rede de sorveterias possui uma fábrica central responsável pela produção dos sabores. Após a produção, os lotes precisam ser distribuídos entre as lojas da rede. O sistema permite registrar lotes de produção, distribuir quantidades para lojas cadastradas e manter rastreabilidade de todas as movimentações.

---

## Escopo do Sistema

O sistema cobre o ciclo de **produção → distribuição → controle de estoque das lojas** por baixa manual.

O sistema **não** gerencia vendas, clientes, preços ou pagamentos. O controle de estoque das lojas é feito por baixa manual: o operador registra as quantidades consumidas pela loja, e o sistema calcula o saldo atual com base no total recebido via distribuição menos as baixas informadas.

---

## Domínio do Problema

Atualmente a rede não possui controle estruturado para:

- Registrar as quantidades produzidas por lote e sabor
- Controlar a quantidade disponível em cada lote após distribuições parciais
- Registrar e auditar as distribuições realizadas
- Garantir que não sejam distribuídas quantidades superiores às disponíveis
- Acompanhar o estoque atual de cada loja
- Diferenciar permissões entre administradores e operadores de produção

---

## Requisitos Funcionais

Legenda de status na implementação atual:

| Status | Significado |
|--------|-------------|
| ✅ | Atendido no código |
| ⚠️ | Atendido em parte |
| ❌ | Ainda não implementado |

### Sabores

| ID | Descrição | Status |
|----|-----------|--------|
| RF-01 | Cadastrar sabores com nome, descrição e status ativo/inativo | ✅ |
| RF-02 | Listar, editar e inativar sabores cadastrados | ✅ |

### Lotes de Produção

| ID | Descrição | Status |
|----|-----------|--------|
| RF-03 | Registrar lote de produção informando sabor, quantidade e data | ✅ |
| RF-04 | Exibir quantidade disponível de cada lote em tempo real | ✅ |
| RF-05 | Listar lotes com filtros por sabor, status e período | ⚠️ Listagem completa sem filtros |

### Lojas

| ID | Descrição | Status |
|----|-----------|--------|
| RF-06 | Cadastrar lojas com nome, endereço e status ativo | ✅ Endereço estruturado (CEP, UF, cidade, logradouro, número, complemento) |
| RF-07 | Listar e editar lojas cadastradas | ✅ Inativação incluída |

### Distribuição

| ID | Descrição | Status |
|----|-----------|--------|
| RF-08 | Registrar distribuição associando lote, loja e quantidade | ✅ Apenas perfil ADMIN |
| RF-09 | Impedir distribuição quando a quantidade exceder o saldo do lote | ✅ |
| RF-10 | Listar histórico de distribuições com filtros por loja, lote e período | ⚠️ Histórico completo sem filtros |
| RF-11 | Cancelar distribuição e estornar saldo ao lote de origem | ✅ |

### Estoque das Lojas

| ID | Descrição | Status |
|----|-----------|--------|
| RF-15 | Registrar baixa manual de estoque informando loja, lote e quantidade consumida | ✅ |
| RF-16 | Exibir saldo atual da loja por sabor (total recebido − total de baixas) | ✅ Tela **Estoque atual** (`/estoque`) e fluxo de baixa |
| RF-17 | Listar histórico de baixas por loja e período | ⚠️ Filtro por loja; sem filtro por período |

### Controle de Acesso

| ID | Descrição | Status |
|----|-----------|--------|
| RF-12 | Autenticar usuários com e-mail e senha, retornando token JWT | ✅ |
| RF-13 | Perfil ADMIN: acesso total ao sistema | ✅ |
| RF-14 | Perfil OPERADOR: registro de lotes, baixas e consultas | ✅ Sem lojas, distribuições nem CRUD de sabores |

---

## Requisitos Não Funcionais

| ID | Descrição | Categoria | Status |
|----|-----------|-----------|--------|
| RNF-03 | Senhas armazenadas com hash BCrypt (fator mínimo 10) | Segurança | ✅ |
| RNF-04 | Tokens JWT com expiração configurável (padrão 8h) | Segurança | ✅ `jwt.expiration-ms` |
| RNF-05 | Endpoints protegidos por autorização baseada em roles (RBAC) | Segurança | ✅ |
| RNF-06 | Operações de distribuição e baixa atômicas (transação única no banco) | Confiabilidade | ✅ `@Transactional` nos services |
| RNF-07 | Erros de validação com mensagens descritivas no corpo da resposta | Usabilidade | ⚠️ JSON em `/auth/login`; telas MVC usam mensagens na página |
| RNF-08 | Código organizado em camadas: Controller → Service → Repository | Manutenibilidade | ✅ |
| RNF-09 | Migrações de banco gerenciadas por Flyway | Manutenibilidade | ✅ |

---

## Implementação atual (resumo)

| Módulo | Rota | Observação |
|--------|------|------------|
| Estoque atual | `/`, `/estoque` | Página inicial — CD + lojas |
| Sabores | `/sabores` | CRUD/inativar: ADMIN |
| Lotes | `/lotes` | Criar e listar |
| Lojas | `/lojas` | ADMIN |
| Distribuições | `/distribuicoes` | ADMIN |
| Baixas | `/baixas` | Filtro opcional por loja |
| Operadores | `/usuarios` | ADMIN |
| Login | `/login`, `/auth/login` | JWT em cookie |

**Usuário seed (dev):** `admin@aisecream.com` / `admin123`

---

## Tecnologias

### Java 21

Versão LTS. Runtime e linguagem do projeto.

### Spring Boot 4.0.3

Framework principal: servidor Tomcat embutido, Spring Data JPA, Spring Security, Bean Validation e Thymeleaf para a interface web.

### MariaDB

Banco relacional (`aisecream`), transações ACID, compatível com o ecossistema MySQL/MariaDB.

### Spring Data JPA (Hibernate)

Persistência via `JpaRepository`, `@Transactional` nas operações de escrita (distribuição, baixa, etc.).

### Spring Security + JWT

Autenticação com JWT (jjwt), filtro `JwtAuthFilter`, roles **ADMIN** e **OPERADOR**, senhas com BCrypt.

### Flyway

Versionamento do schema em `src/main/resources/db/migration` (V1 a V4).

### Maven Wrapper (`mvnw`)

Build e execução sem instalar Maven globalmente: `.\mvnw.cmd` (Windows) ou `./mvnw` (Linux/macOS).

### Bean Validation (Jakarta Validation)

Validação em entidades, DTOs e formulários com `@Valid`.

---

## Arquitetura

Monolito em camadas:

```
Browser (Thymeleaf + login AJAX)
    ↓
Controller (@Controller / @RestController)
    ↓
Service (regras de negócio + @Transactional)
    ↓
Repository (Spring Data JPA)
    ↓
MariaDB (Flyway)
```

A segurança intercepta requisições via `JwtAuthFilter` e regras em `SecurityConfig`.

Diagramas C4 em `src/architecture/` (`c4-nivel2-containers.puml`, `c4-nivel3-components.puml`).

---

## Banco de dados (Flyway)

| Versão | Conteúdo |
|--------|----------|
| V1 | Tabelas base do domínio |
| V2 | Usuário administrador inicial |
| V3 | Campos históricos em `distribuicao` |
| V4 | Endereço estruturado em `loja` |

`spring.jpa.hibernate.ddl-auto=validate` — alterações de schema apenas via Flyway.

---

## Como rodar o projeto

### Pré-requisitos

- Java 21
- MariaDB em execução (ex.: `localhost:3306`)
- IntelliJ IDEA ou outra IDE (opcional)

### Passos

1. Clone o repositório
2. Configure o banco em `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/aisecream
   spring.datasource.username=root
   spring.datasource.password=
   ```

3. Baixe dependências (se necessário):

   ```bash
   .\mvnw.cmd dependency:resolve -U
   ```

4. Execute a aplicação:

   ```bash
   .\mvnw.cmd spring-boot:run
   ```

5. Acesse **http://localhost:8080** (redireciona para o estoque atual após login).

### Flyway

Se uma migração falhar no histórico:

```bash
.\mvnw.cmd flyway:repair
```

### Porta em uso

Se `Port 8080 was already in use`, encerre o processo anterior ou use `server.port=8081` em `application.properties`.

---

## Estrutura do código

```
src/main/java/com/aisecream/
├── config/          # SecurityConfig
├── controller/      # MVC + AuthController
├── dto/
├── exception/       # ApiExceptionHandler (REST)
├── model/
├── repository/
├── security/
└── service/

src/main/resources/
├── application.properties
├── db/migration/
└── templates/
```

Testes automatizados: `AisecreanApplicationTests` (carga de contexto).
