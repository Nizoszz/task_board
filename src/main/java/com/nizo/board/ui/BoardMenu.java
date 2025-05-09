package com.nizo.board.ui;

import com.nizo.board.dto.BoardColumnInfoDto;
import com.nizo.board.persistence.entity.BoardColumnEntity;
import com.nizo.board.persistence.entity.BoardColumnKind;
import com.nizo.board.persistence.entity.BoardEntity;
import com.nizo.board.persistence.entity.CardEntity;
import com.nizo.board.service.BoardColumnQueryService;
import com.nizo.board.service.BoardQueryService;
import com.nizo.board.service.CardQueryService;
import com.nizo.board.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static com.nizo.board.persistence.dao.ConnectionConfig.getConnection;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class BoardMenu{
    private final BoardEntity boardEntity;
    private final Scanner scanner = new Scanner(System.in);
    public void execute(){
        try {
            System.out.printf("Bem-vindo ao board %s, selecione a operação desejada: \n",boardEntity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card.");
                System.out.println("2 - Mover um card.");
                System.out.println("3 - Bloquear um card.");
                System.out.println("4 - Desbloquear um card.");
                System.out.println("5 - Cancelar um card.");
                System.out.println("6 - Visualizar board.");
                System.out.println("7 - Visualizar coluna com cards.");
                System.out.println("8 - Visualizar cards.");
                System.out.println("9 - Voltar para o menu anterior.");
                System.out.println("10 - Sair.");
                option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o menor anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }
        }catch (SQLException ex){
            System.out.println("Ocorreu um erro ao acessar o banco de dados: " + ex.getMessage());
        }
    }
    private void createCard() throws SQLException {
        var card = CardEntity.builder();
        System.out.println("Informe o título do card: ");
        card.title(scanner.nextLine());
        System.out.println("Informe a descrição do card: ");
        card.description(scanner.nextLine());
        card.boardColumn(boardEntity.getInitColumn());
        try(var connection = getConnection()){
            new CardService(connection).createCard(card.build());
            System.out.println("Card criado com sucesso!");
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna:");
        var cardId = scanner.nextLong();
        var boardColumnInfo = boardEntity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDto(bc.getId(), bc.getBoardColumnOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId,boardEntity.getId(),boardColumnInfo);
        } catch (RuntimeException ex){
            System.out.println("Ocorreu um erro ao mover o card: " + ex.getMessage());
        }

    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que deseja bloquear: ");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Informa o motivo do bloqueio do card: ");
        var reason = scanner.nextLine();
        System.out.println(reason);
        var boardColumnInfo = boardEntity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDto(bc.getId(), bc.getBoardColumnOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, boardEntity.getId(), reason, boardColumnInfo);
            System.out.println("Bloqueado com sucesso!");
        }
        catch (RuntimeException ex) {
            System.out.println("Ocorreu um erro ao bloquear o card: " + ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que deseja desbloquear: ");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Informa o motivo do desbloqueio do card: ");
        var reason = scanner.nextLine();
        System.out.println(reason);
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, boardEntity.getId(), reason);
            System.out.println("Desloqueado com sucesso!");
        }
        catch (RuntimeException ex) {
            System.out.println("Ocorreu um erro ao desbloquear o card: " + ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento: ");
        var cardId = scanner.nextLong();
        var cancelColumn = boardEntity.getCancelColumn();
        var boardColumnInfo = boardEntity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDto(bc.getId(), bc.getBoardColumnOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, boardEntity.getId(), cancelColumn.getId(), boardColumnInfo);
            System.out.println("Cancelado com sucesso!");
        } catch (RuntimeException ex){
            System.out.println("Ocorreu um erro ao cancelar o card: " + ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var board = new BoardQueryService(connection).getBoardByIdWithDetails(boardEntity.getId());
            board.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(bc -> System.out.printf("Coluna %s tipo: [%s] tem %s cards\n",bc.name(),bc.kind(),bc.cardsAmount()));
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnIds = boardEntity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while(!columnIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", boardEntity.getName());
            boardEntity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).getById(selectedColumn);
            column.ifPresent(col -> {
                System.out.printf("Coluna %s tipo %s\n", col.getName(), col.getKind());
                if(!nonNull(col.getCards())){
                    System.out.printf("Esta não há cards na coluna de %s\n", col.getName());
                    return;
                }
                col.getCards().forEach(c -> System.out.printf("Card: %s - %s\nDescrição: %s\n",
                                                              c.getId(), c.getTitle(), c.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).getById(selectedCardId, boardEntity.getId()).ifPresentOrElse(c -> {
                System.out.printf("Card: %s - %s\n", c.id(), c.title());
                System.out.printf("Descrição: %s\n", c.description());
                System.out.println(c.blocked() ? String.format("Bloqueio: Está bloqueado.\nMotivo: %s", c.blockReason()) : "Bloqueio: Não está bloqueado");
                System.out.println(
                        c.blocksAmount() > 0
                                ? String.format("Já foi bloqueado %d %s", c.blocksAmount(), c.blocksAmount() == 1 ? "vez" : "vezes")
                                : "O card não foi bloqueado nenhuma vez"
                );
                System.out.printf("Está no momento na coluna %s - %s\n", c.boadColumnId(), c.columnName());
            },() -> System.out.println("Card não foi encontrado"));
        }
    }
}
