package com.nizo.board.ui;

import com.nizo.board.persistence.entity.BoardColumnEntity;
import com.nizo.board.persistence.entity.BoardColumnKind;
import com.nizo.board.persistence.entity.BoardEntity;
import com.nizo.board.service.BoardQueryService;
import com.nizo.board.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.nizo.board.persistence.dao.ConnectionConfig.getConnection;

public class MainMenu{
    private final Scanner scanner = new Scanner(System.in);
    public void execute() throws SQLException{
        System.out.println("Bem-vindo ao gerenciador de boards, escolha a opção desejada: ");
        var option = -1;
        while (true){
            System.out.println("1 - Criar um novo board.");
            System.out.println("2 - Selecionar um board existente.");
            System.out.println("3 - Excluir um board.");
            System.out.println("4 - Sair.");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
    public void createBoard() throws SQLException{
        var entity = new BoardEntity();

        System.out.println("Informe o nome do board: ");
        entity.setName(scanner.next());

        System.out.println("Seu board haverá colunas além das 3 padrões? Se sim, informe quantas ou digite 0: ");
        var additionalColumns = scanner.nextInt();
        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome inicial da coluna do board: ");
        var initColumnName = scanner.next();
        var initColumn = this.factoryColumnBoard(initColumnName, BoardColumnKind.INIT, 0);
        columns.add(initColumn);

        for (int i = 0; i < additionalColumns; i++){
            System.out.println("Informe o nome da coluna de tarefa pendente: ");
            var pendingColumnName = scanner.next();
            var pendingColumn = this.factoryColumnBoard(pendingColumnName, BoardColumnKind.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final: ");
        var finalColumnName = scanner.next();
        var finalColumn = this.factoryColumnBoard(finalColumnName, BoardColumnKind.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome coluna de cancelamento do board: ");
        var cancelColumnName = scanner.next();
        var cancelColumn = this.factoryColumnBoard(cancelColumnName, BoardColumnKind.CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.createBoard(entity);

        }
    }

    public void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar: ");
        var boardId = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var boadExists = queryService.getBoardById(boardId);
            if(boadExists == null){
                System.out.println("Board não encontrado.");
                return;
            }
            var boardMenu = new BoardMenu(boadExists);
            boardMenu.execute();
        }
    }

    public void deleteBoard() throws SQLException{
        System.out.println("Informe o id do board que deseja deletar: ");
        var boardId = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if(!service.deleteBoardById(boardId)){
                System.out.println("Board não encontrado.");
                return;
            }
        }
        System.out.println("Board excluído com sucesso.");
    }
    private BoardColumnEntity factoryColumnBoard(String name,BoardColumnKind kind, int order){
        var boardColumn = BoardColumnEntity.builder().name(name).kind(kind).boardColumnOrder(order);
        return boardColumn.build();
    }
}
