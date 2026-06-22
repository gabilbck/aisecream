# Funcionalidades do Aí 'Se Cream

## Visão geral

Sistema web monolítico para gerenciamento de produção e distribuição de sorvetes de uma fábrica central para as lojas da rede. A interface é **server-side** (Spring MVC + Thymeleaf), com autenticação JWT via cookie HTTP-only e endpoint JSON `POST /auth/login` para o formulário de login.

---

## Funcionalidades por módulo

### 1. Sabores (`/sabores`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-01 | Cadastrar sabores | Nome, descrição e status ativo/inativo (**ADMIN**) |
| RF-02 | Gerenciar sabores | Listar, editar e inativar (**ADMIN**); operadores apenas visualizam |

### 2. Lotes de produção (`/lotes`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-03 | Registrar lote | Sabor, quantidade e data de produção (**ADMIN**) |
| RF-04 | Saldo do lote | Quantidade disponível no CD atualizada após distribuições |
| RF-05 | Listar lotes | Listagem completa; pesquisa instantânea por qualquer coluna da tabela |

### 3. Lojas (`/lojas`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-06 | Cadastrar lojas | Nome, endereço estruturado (CEP, UF, cidade, logradouro, número, complemento), telefone e status (**ADMIN**) |
| RF-07 | Gerenciar lojas | Listar, editar e inativar (**ADMIN**); operadores apenas visualizam |

### 4. Distribuições (`/distribuicoes`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-08 | Registrar distribuição | Lote, loja e quantidade (**ADMIN** e **OPERADOR**) |
| RF-09 | Validar saldo | Impede distribuição acima do disponível no lote |
| RF-10 | Histórico | Listagem completa; pesquisa instantânea por qualquer coluna |
| RF-11 | Cancelar distribuição | Estorna saldo ao lote (**ADMIN**) |

### 5. Baixas de estoque (`/baixas`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-15 | Registrar baixa | Loja, lote e quantidade consumida (**ADMIN** e **OPERADOR**) |
| RF-17 | Histórico de baixas | Filtro por loja (servidor) + pesquisa por qualquer coluna (cliente) |

### 6. Estoque atual (`/`, `/estoque`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-16 | Visão consolidada | Saldo do CD, saldo por loja e totais gerais |

### 7. Operadores (`/usuarios`)

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| — | Gerenciar operadores | Cadastrar e editar usuários com perfil OPERADOR (**ADMIN**) |

### 8. Pesquisa nas listagens

| ID | Funcionalidade | Descrição |
|----|----------------|-----------|
| RF-18 | Pesquisa em tabelas | Campo **Pesquisar** em Sabores, Lojas, Lotes, Distribuições, Baixas e Operadores; filtra em tempo real por qualquer valor visível na tabela |

---

## Controle de acesso

| Recurso | ADMIN | OPERADOR |
|---------|-------|----------|
| Estoque atual | Visualizar; criar lote | Visualizar |
| Sabores | CRUD completo | Visualizar |
| Lojas | CRUD completo | Visualizar |
| Lotes | Criar e listar | Visualizar |
| Distribuições | Criar, listar e cancelar | Criar e listar |
| Baixas | Criar e listar | Criar e listar |
| Operadores | CRUD | Sem acesso |

Autenticação: e-mail + senha → JWT (expiração padrão 8 h). Senhas com BCrypt (fator 10).

**Usuário seed (desenvolvimento):** `admin@aisecream.com` / `admin123`

---

## Interface web

- Navegação principal: Estoque atual, Sabores, Lojas, Lotes, Distribuições, Baixas e Operadores (este último só para ADMIN).
- Formulários Thymeleaf com validação Bean Validation.
- Mensagens de sucesso/erro via flash attributes nas telas MVC.
- Fragmento reutilizável `fragments/table-search.html` para pesquisa client-side nas listagens.

---

## Tecnologias

Java 21 · Spring Boot 4.0.3 · Spring MVC · Thymeleaf · Spring Security · JWT · Spring Data JPA · MariaDB · Flyway · Maven Wrapper

---

## Rotas principais

| Rota | Descrição |
|------|-----------|
| `/login`, `/auth/login` | Autenticação |
| `/`, `/estoque` | Estoque atual |
| `/sabores` | Sabores |
| `/lotes` | Lotes |
| `/lojas` | Lojas |
| `/distribuicoes` | Distribuições |
| `/baixas` | Baixas |
| `/usuarios` | Operadores (ADMIN) |
| `/acesso-negado` | Página de acesso negado |
