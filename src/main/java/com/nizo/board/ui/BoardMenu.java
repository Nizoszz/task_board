package com.nizo.board.ui;

import com.nizo.board.persistence.entity.BoardColumnEntity;
import com.nizo.board.persistence.entity.BoardEntity;
import com.nizo.board.service.BoardColumnQueryService;
import com.nizo.board.service.BoardQueryService;
import com.nizo.board.service.CardQueryService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static com.nizo.board.persistence.dao.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu{
    private final BoardEntity boardEntity;
    private final Scanner scanner = new Scanner(System.in);
    public void execute() throws SQLException{
        System.out.printf("Bem-vindo ao board %s, selecione a operação desejada: ", boardEntity.getId());
        var option = -1;
        while (option != 9){
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
            switch (option){
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
    }
    private void createCard() throws SQLException {};
    private void moveCardToNextColumn() throws SQLException {};
    private void blockCard() throws SQLException {};
    private void unblockCard() throws SQLException {};
    private void cancelCard() throws SQLException {};
    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var board = new BoardQueryService(connection).getBoardByIdWithDetails(boardEntity.getId());
            board.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(bc -> {
                    System.out.printf("Coluna %s tipo: [%s] tem %s cards\n", bc.name(), bc.kind(), bc.cardsAmount());
                });
            });
        }
    };
    private void showColumn() throws SQLException {
        var columnIds = boardEntity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = 1L;
        while(!columnIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", boardEntity.getName());
            boardEntity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s] \n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).getById(selectedColumn);
            column.ifPresent(col -> {
                System.out.printf("Coluna %s tipo %s\n", col.getId(), col.getName(), col.getKind());
                col.getCards().forEach(c -> System.out.printf("Card: %s - %s\n Descrição: %s",
                                                              c.getId(), c.getTitle(), c.getDescription()));
            });
        }
    };
    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).getById(selectedCardId).ifPresentOrElse(c -> {
                System.out.printf("Card: %s - %s\n", c.id(), c.title());
                System.out.printf("Descrição: %s\n", c.description());
                System.out.printf(c.blocked() ? "Está bloqueado. Motivo: %s" + c.blockReason() : "Não está bloqueado");
                System.out.printf("Já foi bloqueado %s vezes", c.blocksAmount());
                System.out.printf("Está no momento na coluna %s - %s", c.boadColumnId(), c.columnName());
            },() -> System.out.println("Card não foi encontrado"));
        }
    };

}
