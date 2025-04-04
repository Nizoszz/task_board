# 🗂️ Kanban Board CLI

Um projeto de gerenciador de *boards* Kanban via terminal, com funcionalidades para criar, visualizar, mover, bloquear e desbloquear *cards*. A aplicação utiliza JDBC e banco de dados relacional para armazenar as informações.

## 🧱 Estrutura do Banco de Dados

### `tb_boards`
Armazena os *boards* disponíveis no sistema.

| Coluna       | Tipo       | Descrição                        |
|--------------|------------|----------------------------------|
| id           | BIGINT     | Chave primária (auto incremento) |
| name         | VARCHAR    | Nome do board                    |
| created_at   | TIMESTAMP  | Data de criação                  |

---

### `tb_board_columns`
Representa as colunas de um board (como *To Do*, *Doing*, *Done*).

| Coluna             | Tipo       | Descrição                          |
|--------------------|------------|------------------------------------|
| id                 | BIGINT     | Chave primária                     |
| name               | VARCHAR    | Nome da coluna                     |
| kind               | VARCHAR    | Tipo da coluna                     |
| board_column_order | INT        | Ordem da coluna no board           |
| board_id           | BIGINT     | FK para `tb_boards(id)`            |

> Restrições:
> - `board_id + board_column_order` devem ser únicos.
> - Deleção em cascata com o board.

---

### `tb_cards`
Armazena os cartões de tarefas.

| Coluna          | Tipo     | Descrição                    |
|-----------------|----------|------------------------------|
| id              | BIGINT   | Chave primária               |
| title           | VARCHAR  | Título do card               |
| description     | TEXT     | Descrição do card            |
| board_column_id | BIGINT   | FK para `tb_board_columns`   |

---

### `tb_card_blocks`
Registra bloqueios temporários de cards.

| Coluna          | Tipo      | Descrição                                    |
|-----------------|-----------|----------------------------------------------|
| id              | BIGINT    | Chave primária                               |
| blocked_at      | TIMESTAMP | Data/hora do bloqueio                        |
| blocked_reason  | TEXT      | Motivo do bloqueio                           |
| unblocked_at    | TIMESTAMP | Data/hora do desbloqueio (se houver)         |
| unblocked_reason| TEXT      | Motivo do desbloqueio (se houver)            |
| card_id         | BIGINT    | FK para `tb_cards(id)`                       |

---

## 📋 Funcionalidades

### Menu Principal
- **1** - Criar um novo board.
- **2** - Selecionar um board existente.
- **3** - Excluir um board.
- **4** - Sair.

---

### Menu de Board
- **1** - Criar um card.
- **2** - Mover um card para a próxima coluna.
- **3** - Bloquear um card.
- **4** - Desbloquear um card.
- **5** - Cancelar um card.
- **6** - Visualizar board completo.
- **7** - Visualizar coluna com cards.
- **8** - Visualizar todos os cards.
- **9** - Voltar ao menu anterior.
- **10** - Sair.

---

## ⚙️ Tecnologias Utilizadas

- Java (JDBC)
- Banco de Dados Relacional (MySQL, PostgreSQL ou H2)
- Maven (caso use dependências)
- Scanner (entrada do usuário via terminal)

---

## 🏁 Como Executar

1. Clone este repositório
2. Configure seu banco de dados
3. Execute a aplicação via terminal
4. Siga o menu interativo

---
