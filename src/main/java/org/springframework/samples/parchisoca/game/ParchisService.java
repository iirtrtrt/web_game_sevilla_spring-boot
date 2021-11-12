package org.springframework.samples.parchisoca.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisoca.enums.FieldType;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ParchisService {

    @Autowired
	ParchisRepository parchisRepo;

    @Autowired
    BoardFieldRepository boardFieldRepository;

    @Autowired
    BoardFieldService boardFieldService;

    GameRepository gameRepository;

    GameBoardRepository gameBoardRepository;
    public static final String STANDARD_FILL_COLOR  = "#fef9e7" ;
    public static final String GREEN_END  = "#26ca0c" ;
    public static final String RED_END  = "#e32908" ;
    public static final String BLUE_END  = "#0890e3" ;
    public static final String YELLOW_END = "#dbe117";

    public static final Integer FIELD_WIDTH  = 2;
    public static final Integer FIELD_HEIGHT  = 1;

    public Optional<Parchis> findById(Integer id){
		return parchisRepo.findById(id);
	}

    @Autowired
    public ParchisService(ParchisRepository parchisRepository,
                      GameRepository gameRepository, GameBoardRepository gameBoardRepository, BoardFieldRepository boardRepo, BoardFieldService boardFieldService) {
        this.parchisRepo = parchisRepository;
        this.gameRepository = gameRepository;
        this.gameBoardRepository = gameBoardRepository;
        this.boardFieldRepository = boardRepo;
        this.boardFieldService = boardFieldService;
    }



    public void initGameBoard(Game game){
        //Todo: should not be hard coded
        Parchis gameBoard = new Parchis();
        gameBoard.background = "resources/images/background_board.jpg";
        gameBoard.height = 800;
        gameBoard.width = 800;

        //Create Game fields
        System.out.println("creating gameFields");

        gameBoard.fields = new ArrayList<BoardField>();
        this.createGameFields(gameBoard);
        System.out.println("finished creating gameFields");


        System.out.println("setting gameboard");
        gameBoard.setGame(game);
        game.setGameboard(gameBoard);

        try
        {
            this.gameBoardRepository.save(gameBoard);
        }
        catch (Exception e )
        {
            System.out.println("exception: " + e.getMessage());
        }

        for (BoardField field : gameBoard.getFields()){
            field.setBoard(gameBoard);
            boardFieldService.saveBoardField(field);
        }


        /*only for test purposes begin
        Integer r = 1;
        for(GamePiece piece : game.getCreator().getGamePieces()){
            piece.setField(gameBoard.getFields().get(r));
            r++;
        }   
        only for test purposes end*/




    }

    public void setNextFields(GameBoard board){
        for(BoardField field : board.getFields()){
            BoardField next = null;
            if(field.getNumber() == 68) next = boardFieldService.find(field.getNumber() + 1, board);
            else if(field.getNumber() == 174 || field.getNumber() == 157 || field.getNumber() == 140 || field.getNumber() == 123){}
            else next = boardFieldService.find(field.getNumber() + 1, board);
            field.setNext_field(next);
        }
    }



    //Calculates all the Board Field entities that are needed
    public void createGameFields(GameBoard board){
        int id;
        int column = 7;
        int row = 0;

        // BoardField[][] field_array = new BoardField[20][20];  unfortunately this does not work with oneToMany relationship

        //create all base fields

        //ids 35 to 43 and 59 to 67
        id = 35;
        for(row = 0; row < 20; row++) {
            if(row == 9 || row == 10){
                id = 59;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));
            id++;
        }

        //fields 34 and 68
        column = 9;
        row = 0;
        id = 34;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));
        row = 19;
        id = 68;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));


        //ids 1-9 and 25-33
        column = 11;
        id = 33;
        for(row = 0; row < 20; row++) {
            if(row == 9 || row == 10){
                id = 9;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));
            id--;
        }

        //ids 50 to 44 and 24 to 18
        row = 7;
        id = 50;
        for(column = 0; column < 20; column++) {
            if(column > 6 && column < 13){
                id = 24;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));
            id--;
        }

        //ids 52 to 58 and 10 to 16
        row = 11;
        id = 52;
        for(column = 0; column < 20; column++) {
            if(column > 6 && column < 13){
                id = 10;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));
            id++;
        }

        //ids 51 and 17
        column = 0;
        row = 9;
        id = 51;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR,FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));
        column = 19;
        id = 17;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));


        //create the end fields

        //green end fields
        row =  9;
        id = 151; //Todo: not sure what ids for the end fields
        for(column = 1; column < 8; column++) {
            board.fields.add(new BoardField(id, GREEN_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));
            id++;
        }


         //blue end fields
         row =  9;
         id = 123; //Todo: not sure what ids for the end fields
         for(column = 12; column < 19; column++) {
            board.fields.add(new BoardField(id, BLUE_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH ));
             id--;
         }


         //ids red end fields
        column = 9;
        id = 134;
        for(row = 1; row < 8; row++) {
            board.fields.add(new BoardField(id, RED_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));
            id++;
        }


        //ids yellow end fields
        column = 9;
        id = 174;
        for(row = 12; row < 19; row++) {
            board.fields.add(new BoardField(id, YELLOW_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT ));
            id--;
        }

    }
    @Transactional
    public void saveParchis(Parchis parchis) throws DataAccessException {
        parchisRepo.save(parchis);
    }





}
