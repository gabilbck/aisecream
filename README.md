
# Aí 'Se Cream

Sistema web monolítico para gerenciamento de produção e distribuição de sorvetes de uma fábrica central para as lojas da rede.
 
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

### Sabores

| ID | Descrição |
|----|-----------|
| RF-01 | Cadastrar sabores com nome, descrição e status ativo/inativo |
| RF-02 | Listar, editar e inativar sabores cadastrados |

### Lotes de Produção

| ID | Descrição |
|----|-----------|
| RF-03 | Registrar lote de produção informando sabor, quantidade e data |
| RF-04 | Exibir quantidade disponível de cada lote em tempo real |
| RF-05 | Listar lotes com filtros por sabor, status e período |

### Lojas

| ID | Descrição |
|----|-----------|
| RF-06 | Cadastrar lojas com nome, endereço e status ativo |
| RF-07 | Listar e editar lojas cadastradas |

### Distribuição

| ID | Descrição |
|----|-----------|
| RF-08 | Registrar distribuição associando lote, loja e quantidade |
| RF-09 | Impedir distribuição quando a quantidade exceder o saldo do lote |
| RF-10 | Listar histórico de distribuições com filtros por loja, lote e período |
| RF-11 | Cancelar distribuição e estornar saldo ao lote de origem |

### Estoque das Lojas

| ID | Descrição |
|----|-----------|
| RF-15 | Registrar baixa manual de estoque informando loja, lote e quantidade consumida |
| RF-16 | Exibir saldo atual da loja por sabor (total recebido − total de baixas) |
| RF-17 | Listar histórico de baixas por loja e período |

### Controle de Acesso

| ID | Descrição |
|----|-----------|
| RF-12 | Autenticar usuários com e-mail e senha, retornando token JWT |
| RF-13 | Perfil ADMIN: acesso total ao sistema |
| RF-14 | Perfil OPERADOR: registro de lotes, baixas e consultas |
 
---

## Requisitos Não Funcionais

| ID | Descrição | Categoria |
|----|-----------|-----------|
| RNF-01 | Respostas de listagem em até 1s para até 10.000 registros | Performance |
| RNF-02 | Operações de escrita concluídas em até 500ms | Performance |
| RNF-03 | Senhas armazenadas com hash BCrypt (fator mínimo 10) | Segurança |
| RNF-04 | Tokens JWT com expiração configurável (padrão 8h) | Segurança |
| RNF-05 | Endpoints protegidos por autorização baseada em roles (RBAC) | Segurança |
| RNF-06 | Operações de distribuição e baixa atômicas (transação única no banco) | Confiabilidade |
| RNF-07 | Erros de validação com mensagens descritivas no corpo da resposta | Usabilidade |
| RNF-08 | Código organizado em camadas: Controller → Service → Repository | Manutenibilidade |
| RNF-09 | Migrações de banco gerenciadas por Flyway | Manutenibilidade |
 
---

## Tecnologias

### Java 21

Versão LTS com suporte garantido até 2031. Traz virtual threads (Project Loom), melhorando throughput em operações de I/O. Ecossistema maduro, tipagem forte e ampla base de profissionais.

### Spring Boot 3.x

Framework principal da aplicação. Oferece configuração automática, servidor Tomcat embutido e integração nativa com Spring Data JPA, Spring Security e Bean Validation. Reduz boilerplate e acelera o desenvolvimento de APIs REST — ideal para um monolito bem estruturado em camadas.

### MariaDB

Banco de dados relacional open-source, compatível com MySQL e com suporte completo a transações ACID. Adequado para o modelo relacional do domínio (lotes, lojas, distribuições). Menor custo operacional e fácil de hospedar em diferentes ambientes.

### Spring Data JPA (Hibernate)

Abstração sobre Hibernate que elimina SQL repetitivo via `JpaRepository`. Suporte a queries derivadas, JPQL e à anotação `@Transactional` — essencial para garantir atomicidade nas operações de distribuição e baixa de estoque.

### Spring Security + JWT

Autenticação stateless via tokens JWT, sem necessidade de manter sessão no servidor. Spring Security simplifica a configuração de controle de acesso por roles (ADMIN / OPERADOR) com filtros e anotações como `@PreAuthorize`.

### Flyway

Gerenciamento de versionamento do schema do banco de dados por scripts SQL numerados. Garante que todos os ambientes (desenvolvimento, homologação, produção) estejam sempre com a mesma estrutura de banco.

### Maven Wrapper (mvnw)

O projeto utiliza o Maven Wrapper (`mvnw`), que baixa e executa o Maven automaticamente sem necessidade de instalação manual. Basta usar `.\mvnw` no Windows (ou `./mvnw` no Linux/Mac) no lugar do comando `mvn`.

### Bean Validation (Jakarta Validation 3)

Anotações como `@NotNull`, `@Min` e `@Positive` nos DTOs garantem validação declarativa sem lógica manual nos services. Integrado nativamente com Spring MVC via `@Valid`.
 
---

## Arquitetura

O sistema segue arquitetura monolítica com separação em camadas:

```
Controller (REST)
    ↓
Service (regras de negócio + @Transactional)
    ↓
Repository (Spring Data JPA)
    ↓
MariaDB
```

A camada de segurança atua transversalmente, interceptando requisições antes de chegarem aos controllers via filtros do Spring Security.
 
---

## Como Rodar o Projeto

### Pré-requisitos

- Java 21 instalado
- MariaDB rodando localmente
- IntelliJ IDEA (recomendado)

### Passos

1. Clone o repositório
2. Configure o banco no `src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:mariadb://localhost:3306/aisecrean
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```
3. Baixe as dependências:
   ```
   .\mvnw dependency:resolve -U
   ```
4. Rode a aplicação:
   ```
   .\mvnw spring-boot:run
   ```
 


