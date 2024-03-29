package org.springframework.samples.parchisoca.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.service.BoardFieldService;
import org.springframework.samples.parchisoca.service.ParchisService;
import org.springframework.stereotype.Component;

import java.awt.Color;


@Component
public class StateMove {


    private static final Logger logger = LogManager.getLogger(StateMove.class);

    private static final Integer STARTING_FIELD_YELLOW = 5;
    private static final Integer STARTING_FIELD_BLUE = 22;
    private static final Integer STARTING_FIELD_RED = 39;
    private static final Integer STARTING_FIELD_GREEN = 56;

    private static final Integer REPETITION_DICE_NUMBER= 6;

    private static final Integer YELLOW_ENTRANCE = 68;
    private static final Integer BLUE_ENTRANCE = 17;
    private static final Integer RED_ENTRANCE = 34;
    private static final Integer GREEN_ENTRANCE = 51;

    private static final Integer FIRST_FIELD = 1;
    private static final Integer FIELD_NUMBER_14 = 14;
    private static final Integer FIELD_NUMBER_31 = 31;
    private static final Integer FIELD_NUMBER_65 = 65;
    private static final Integer NUMBER_FIELDS_COURSE = 68;

    private static final Integer YELLOW_END = 175;
    private static final Integer BLUE_END = 124;
    private static final Integer RED_END = 141;
    private static final Integer GREEN_END = 158;

    private static final Integer YELLOW_START_ROW = 168;
    private static final Integer BLUE_START_ROW = 117;
    private static final Integer RED_START_ROW = 134;
    private static final Integer GREEN_START_ROW = 151;

    private static final Integer MAX_PIECES_PER_FIELD = 2;

    private static final Integer NUMBER_PIECES_PER_USER = 4;




    private static BoardFieldService boardFieldService;
    @Autowired
    private BoardFieldService boardFieldService_;

    private static ParchisService parchisService;
    @Autowired
    private ParchisService parchisService_;

    @PostConstruct
    private void initStaticDao () {
       boardFieldService = this.boardFieldService_;
       parchisService = this.parchisService_;
    }




    public static void doAction(Game game){
        game.setTurn_state(TurnState.NEXT);
        GamePiece selec = getMovingPiece(game);
        
        Parchis parchisBoard = (Parchis) game.getGameboard();
        parchisBoard.setKick(false);
        // Moves piece from home if possible
        if (parchisBoard.getOptions().get(0).getText().equals(Option.MOVE_HOME)) {

            movePieceFromHome(game);
            //If you kick a piece that was in your home field you get an extra movement
            if (parchisBoard.isKick()) game.setTurn_state(TurnState.CHOOSEEXTRA);

            
        } //Normal movement
        else if (game.getDice() != REPETITION_DICE_NUMBER && !parchisBoard.getOptions().get(0).getText().equals(Option.PASS)) {

            Integer nextPos =  calcPosition(selec, game.getDice(), game);
            kickPiece(boardFieldService.find(nextPos, game.getGameboard()), selec, game);
            movePiece(nextPos, selec, game);
            //If you kicked a piece or got to the end field with a piece, you get an extra move
            if(parchisBoard.isExtraAction() == false || parchisBoard.isKick()){
                game.setTurn_state(TurnState.CHOOSEEXTRA);
            }

        
        } else if (game.getDice() == REPETITION_DICE_NUMBER) {
            //If dice = 6 but you don´t have options to move a piece
            if (parchisBoard.getOptions().get(0).getText().equals(Option.REPEAT)) {
                game.setTurn_state(TurnState.INIT);

                //If You get 3 sixes in a row, you lose a random piece
            } else if(parchisBoard.getOptions().get(0).getText().equals(Option.LOOSE)){
                List<GamePiece> gamePieces = (game.getCurrent_player().getGamePieces());
                Collections.shuffle(gamePieces);
                for (GamePiece piece: gamePieces){
                    if (piece.getField() != null){
                        piece.getField().getListGamesPiecesPerBoardField().remove(piece);
                        piece.setField(null);
                        break; 
                    }
                }
                //In any other case, normal movement, plus repetition
            }else{
                Integer reps = parchisBoard.getRepetitions();
                Integer nextPos =  calcPosition(selec, game.getDice(), game);
                kickPiece(boardFieldService.find(nextPos, game.getGameboard()), selec, game);
                movePiece(nextPos, selec, game);
                if(reps==null){
                    parchisBoard.setRepetitions(1);
                } else{
                    parchisBoard.setRepetitions(reps+1);
                }
                //If you kicked a piece or got to the end field with a piece, you get an extra move
                if(parchisBoard.isKick() || parchisBoard.isExtraAction() == false){
                    game.setTurn_state(TurnState.CHOOSEEXTRA);
                }else{
                    game.setTurn_state(TurnState.INIT);
                }
            }
            
        }
        
        if(parchisBoard.getGreenFinished() == NUMBER_PIECES_PER_USER || parchisBoard.getRedFinished() == NUMBER_PIECES_PER_USER || 
        parchisBoard.getYellowFinished() == NUMBER_PIECES_PER_USER || parchisBoard.getBlueFinished() == NUMBER_PIECES_PER_USER ){
            game.setTurn_state(TurnState.FINISHED);
        }
        parchisService.handleState(game);

    }

    private static List<BoardField> getSaveFields (Game game){
        List<BoardField> saveFields = new ArrayList<BoardField>();
        for(BoardField field : game.getGameboard().getFields()){
            if(field.isParchis_special()==true){
                saveFields.add(field);
            }
        }
        return saveFields;
    }

    public static GamePiece getMovingPiece (Game game){
        BoardField fieldSelec = boardFieldService.find(1, game.getGameboard());
        GamePiece selec = game.getCurrent_player().getGamePieces().get(0);
        for (Option opt: ((Parchis) game.getGameboard()).options) {
            if (opt.getChoosen()) {
                game.addToHistoryBoard(game.getCurrent_player().getFirstname() + ": " + opt.getText());
                fieldSelec = boardFieldService.find(opt.getNumber(), game.getGameboard());
                break;
            }
        }
        for (GamePiece piece: game.getCurrent_player().getGamePieces()) {
            if (piece.getField() == fieldSelec){
                selec = piece;
                break;
            }

        }
        return selec;
    }

    public static void kickPiece (BoardField field, GamePiece piece, Game game){
        List<BoardField> saveFields = getSaveFields(game);
        if (field.getListGamesPiecesPerBoardField().size()==1 && !(saveFields.contains(field))){ 
            GamePiece pieceInField = field.getListGamesPiecesPerBoardField().get(0);
            if(!pieceInField.getTokenColor().equals(piece.getTokenColor())){
                pieceInField.setField(null);
                field.getListGamesPiecesPerBoardField().remove(pieceInField);
                boardFieldService.saveBoardField(field);
                Parchis parchis = (Parchis) game.getGameboard();
                parchis.setKick(true);
                return;
            }
        }
    }

    private static void kickHomePiece(BoardField field, GamePiece myPiece, Game game){
        Color color = myPiece.getTokenColor();
        if(field.getListGamesPiecesPerBoardField().size()==MAX_PIECES_PER_FIELD){
            for(GamePiece piece: field.getListGamesPiecesPerBoardField()){
                if(!piece.getTokenColor().equals(color)){
                    piece.setField(null);
                    field.getListGamesPiecesPerBoardField().remove(piece);
                    boardFieldService.saveBoardField(field);
                    Parchis parchis = (Parchis) game.getGameboard();
                    parchis.setKick(true);
                    break;
                }
            }
        }

    }


    public static Integer calcPosition(GamePiece piece, Integer moves, Game game){
        Integer x = piece.getField().getNext_field().getNumber();
        Integer nextPos =  x+moves-1;
        if((nextPos>YELLOW_ENTRANCE || x==FIRST_FIELD) && piece.getTokenColor().equals(Color.YELLOW) ) nextPos = nextPos%NUMBER_FIELDS_COURSE + YELLOW_START_ROW-1;
        else if(nextPos>GREEN_ENTRANCE && piece.getField().getNumber()>FIELD_NUMBER_31 && piece.getField().getNumber()<=GREEN_ENTRANCE  && piece.getTokenColor().equals(Color.GREEN) ) nextPos = nextPos - GREEN_ENTRANCE + GREEN_START_ROW-1;
        else if(nextPos>RED_ENTRANCE && piece.getField().getNumber()>FIELD_NUMBER_14 && piece.getField().getNumber()<=RED_ENTRANCE  && piece.getTokenColor().equals(Color.RED) ) nextPos = nextPos - RED_ENTRANCE + RED_START_ROW-1;
        else if(nextPos%NUMBER_FIELDS_COURSE>BLUE_ENTRANCE && ((piece.getField().getNumber()>FIRST_FIELD && piece.getField().getNumber()<=BLUE_ENTRANCE) || (piece.getField().getNumber()>FIELD_NUMBER_65 && piece.getField().getNumber()<=NUMBER_FIELDS_COURSE)) && piece.getTokenColor().equals(Color.BLUE))  nextPos = nextPos%NUMBER_FIELDS_COURSE - BLUE_ENTRANCE + BLUE_START_ROW-1;
        else if (nextPos!=NUMBER_FIELDS_COURSE){ nextPos = nextPos%NUMBER_FIELDS_COURSE; }

        if(x>NUMBER_FIELDS_COURSE){ nextPos = x+moves-1; }

        //When a piece gets a position bigger than the end position it continuous moving back and forth through its end row
        nextPos = getPositionIfBiggerThanEnd(nextPos, piece);

        //Calculates if there are 2 pieces in a same field in the fields between the actual position of the piece and the supposed next position, then the position is the one before the block
        nextPos = getPositionIfBlock(nextPos, piece, game);
       
        logger.info("Next position " + Integer.toString(nextPos));
        return nextPos;
    }

    public static void movePiece (Integer nextPos, GamePiece piece, Game game) {
        Parchis parchisBoard = (Parchis) game.getGameboard();
        BoardField nextField = boardFieldService.find(nextPos, game.getGameboard());
            piece.getField().getListGamesPiecesPerBoardField().remove(piece);
            if(nextField.getListGamesPiecesPerBoardField().size()==0){
                nextField.setListGamesPiecesPerBoardField(new ArrayList<GamePiece>());
                nextField.getListGamesPiecesPerBoardField().add(piece);
            }else{
                GamePiece pieceInField = nextField.getListGamesPiecesPerBoardField().get(0);
                nextField.setListGamesPiecesPerBoardField(new ArrayList<GamePiece>());
                nextField.getListGamesPiecesPerBoardField().add(pieceInField);
                nextField.getListGamesPiecesPerBoardField().add(piece);
            }
            
            piece.setField(nextField);
            boardFieldService.saveBoardField(nextField);

            if(piece.getField().getNumber()==GREEN_END ){
                Integer ended = parchisBoard.getGreenFinished();
                parchisBoard.setGreenFinished(ended +1);
                parchisService.deleteSinglePiece(game,piece);
                parchisBoard.setExtraAction(false);
                }
            else if(piece.getField().getNumber()==YELLOW_END){
                Integer ended = parchisBoard.getYellowFinished();
                parchisBoard.setYellowFinished(ended +1);
                parchisService.deleteSinglePiece(game,piece);
                parchisBoard.setExtraAction(false);
            }
            else if(piece.getField().getNumber()==BLUE_END){
                Integer ended = parchisBoard.getBlueFinished();
                parchisBoard.setBlueFinished(ended +1);
                parchisService.deleteSinglePiece(game,piece);
                parchisBoard.setExtraAction(false);
            }
            else if(piece.getField().getNumber()==RED_END){
                Integer ended = parchisBoard.getRedFinished();
                parchisBoard.setRedFinished(ended + 1);
                parchisService.deleteSinglePiece(game,piece);
                parchisBoard.setExtraAction(false);
            }



    }

    private static Integer getPositionIfBiggerThanEnd (Integer nextPos, GamePiece piece){
        if (nextPos>BLUE_END && piece.getTokenColor().equals(Color.BLUE)){
            nextPos = -(nextPos -BLUE_END -BLUE_END);
            if (nextPos < BLUE_START_ROW){
                nextPos = BLUE_START_ROW-nextPos + BLUE_START_ROW;
                if(nextPos > BLUE_END) nextPos = -(nextPos -BLUE_END-BLUE_END);
            }
        } else if (nextPos>YELLOW_END && piece.getTokenColor().equals(Color.YELLOW)){
            nextPos = -(nextPos -YELLOW_END -YELLOW_END);  
            if (nextPos < YELLOW_START_ROW){
                nextPos = YELLOW_START_ROW-nextPos + YELLOW_START_ROW;
                if(nextPos > YELLOW_END) nextPos = -(nextPos -YELLOW_END-YELLOW_END);
            }
        } else if (nextPos>GREEN_END && piece.getTokenColor().equals(Color.GREEN)){
            nextPos = -(nextPos -GREEN_END -GREEN_END);
            if (nextPos < GREEN_START_ROW){
                nextPos = GREEN_START_ROW-nextPos + GREEN_START_ROW;
                if(nextPos > GREEN_END) nextPos = -(nextPos -GREEN_END-GREEN_END);
            }
        } else if (nextPos>RED_END && piece.getTokenColor().equals(Color.RED)){
            nextPos = -(nextPos -RED_END -RED_END);
            if (nextPos < RED_START_ROW){
                nextPos = RED_START_ROW-nextPos + RED_START_ROW;
                if(nextPos > RED_END) nextPos = -(nextPos -RED_END-RED_END);
            }
        }
        
        return nextPos;
    }

    private static Integer getPositionIfBlock(Integer nextPos, GamePiece piece, Game game){
        if(nextPos <= NUMBER_FIELDS_COURSE){
            //BoardField field = boardFieldService.find(nextPos, game.getGameboard());
            Integer numberField = piece.getField().getNumber();
            while(numberField!= nextPos){
                BoardField midField = boardFieldService.find(numberField, game.getGameboard());
                Integer numberOfPiecesInField = midField.getNext_field().getListGamesPiecesPerBoardField().size();
                if (numberOfPiecesInField==MAX_PIECES_PER_FIELD){
                    nextPos = numberField;
                    break;
                }
                Integer nextFieldNumber = midField.getNext_field().getNumber();
                numberField = nextFieldNumber;
            }

        }else{
            Integer endfor = YELLOW_ENTRANCE;
            if (piece.getTokenColor().equals(Color.GREEN)) endfor = GREEN_ENTRANCE;
            else if (piece.getTokenColor().equals(Color.RED)) endfor = RED_ENTRANCE;
            else if (piece.getTokenColor().equals(Color.BLUE)) endfor = BLUE_ENTRANCE;
            Integer startfor = YELLOW_START_ROW;
            if (piece.getTokenColor().equals(Color.GREEN)) startfor = GREEN_START_ROW;
            else if (piece.getTokenColor().equals(Color.RED)) startfor = RED_START_ROW;
            else if (piece.getTokenColor().equals(Color.BLUE)) startfor = BLUE_START_ROW;

            if(piece.getField().getNumber()!= endfor){
                for(int i = piece.getField().getNext_field().getNumber(); i<=endfor ;i++){
                    BoardField midField = boardFieldService.find(i, game.getGameboard());
                    if (midField.getListGamesPiecesPerBoardField().size()==MAX_PIECES_PER_FIELD){
                        nextPos = i-1;
                        break;
                    }
                }
            }

            for(int i = startfor; i<=nextPos ;i++){
                BoardField midField = boardFieldService.find(i, game.getGameboard());
                if (midField.getListGamesPiecesPerBoardField().size()==MAX_PIECES_PER_FIELD){
                    nextPos = i-1;
                    break;
                }
            }
        }

        return nextPos;
    }

    public static void movePieceFromHome (Game game){
        BoardField dependant = null;
            for (GamePiece piece: game.getCurrent_player().getGamePieces()) {
                if (piece.getField() == null) {
                    if (piece.getTokenColor().equals(Color.GREEN)) dependant = boardFieldService.find(STARTING_FIELD_GREEN, game.getGameboard());
                    else if (piece.getTokenColor().equals(Color.RED)) dependant = boardFieldService.find(STARTING_FIELD_RED, game.getGameboard());
                    else if (piece.getTokenColor().equals(Color.BLUE)) dependant = boardFieldService.find(STARTING_FIELD_BLUE, game.getGameboard());
                    else if (piece.getTokenColor().equals(Color.YELLOW)) dependant = boardFieldService.find(STARTING_FIELD_YELLOW, game.getGameboard());
                    kickHomePiece(dependant, piece, game);
                    dependant.getListGamesPiecesPerBoardField().add(piece);
                    piece.setField(dependant);
                    break;
                }
            }
    }
}
