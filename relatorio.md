# 📋 Funcionalidades do Aí 'Se Cream

## 🎯 Visão Geral
Sistema web monolítico para gerenciamento de produção e distribuição de sorvetes de uma fábrica central para as lojas da rede.

---

## 🍦 Funcionalidades por Módulo

### 📌 1. Sabores

| ID | Funcionalidade | Descrição |
|----|---|-----------|
| RF-01 | Cadastrar Sabores | Criar novos sabores com nome, descrição e status ativo/inativo |
| RF-02 | Gerenciar Sabores | Listar, editar e inativar sabores cadastrados |

**Exemplo de Uso:**
```json
POST /api/sabores
{
  "nome": "Chocolate",
  "descricao": "Sorvete sabor chocolate",
  "ativo": true
}