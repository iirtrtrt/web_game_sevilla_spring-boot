package org.springframework.samples.parchisoca.model.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.model.user.User;
import org.springframework.samples.parchisoca.service.BoardFieldService;
import org.springframework.samples.parchisoca.service.OptionService;
import org.springframework.stereotype.Component;

@Component
public class StateChoosePlay {

    private static OptionService optionService;
    @Autowired
    private OptionService optionService_;

    private static BoardFieldService boardFieldService;
    @Autowired
    private BoardFieldService boardFieldService_;




    @PostConstruct
    private void initStaticDao () {
        optionService = this.optionService_;
        boardFieldService = this.boardFieldService_;
    }


    public static void doAction(Game game){
        Parchis parchis = (Parchis) game.getGameboard();
        parchis.options = new ArrayList<>();
        BoardField startField = null;
        Color currentColor = game.getColorOfCurrentPlayer();
        if(currentColor.equals(Color.GREEN)) startField = boardFieldService.find(56, game.getGameboard());
        else if(currentColor.equals(Color.RED)) startField = boardFieldService.find(39, game.getGameboard());
        else if(currentColor.equals(Color.BLUE)) startField = boardFieldService.find(22, game.getGameboard());
        else if(currentColor.equals(Color.YELLOW)) startField = boardFieldService.find(5, game.getGameboard());
        optionCreator(game.getCurrent_player().getGamePieces(), game);
        if(parchis.getOptions().size() == 0){
            if (game.getDice() == 5  && checkHomePieces(game.getCurrent_player())) {
                Option op = new Option(1, Option.MOVE_HOME);
                optionService.saveOption(op);
                parchis.options.add(op);
            }else if(game.getDice() == 6){
                Option op = new Option(1, Option.REPEAT);
                optionService.saveOption(op);
                parchis.options.add(op);
            }else{
                Option op = new Option(1, Option.PASS);
                optionService.saveOption(op);
                parchis.options.add(op);
            }

        }else if(game.getDice()==5 && checkHomePieces(game.getCurrent_player())&& startFieldAvailable(startField, game.getColorOfCurrentPlayer() )){ //If this fulfills you have to move a piece from home to start
            parchis.options = new ArrayList<>();
            Option op = new Option(1, Option.MOVE_HOME);
            op.setNumber(1);
            op.setText(Option.MOVE_HOME);
            optionService.saveOption(op);
            parchis.options.add(op);
        } else if(game.getDice()==6 && parchis.getRepetitions()==2){
                parchis.options = new ArrayList<>();
                Option op = new Option(1, Option.LOOSE);
                optionService.saveOption(op);
                parchis.options.add(op);
        }
    }


    public static void optionCreator(List <GamePiece> pieces, Game game) {
        Parchis parchis = (Parchis) game.getGameboard();
        for (GamePiece piece: pieces) {
            if (piece.getField() != null) {

               if(piece.getField().getNext_field().getListGamesPiecesPerBoardField().size()!=2){
                    Integer fieldNumber = piece.getField().getNumber();
                    Option op = new Option(fieldNumber, Option.MOVE + String.valueOf(fieldNumber));
                    optionService.saveOption(op);
                    parchis.options.add(op);
                }
            }
        }
    }



    private static Boolean startFieldAvailable (BoardField field, Color color){
        Boolean res = false;
        if(field.getListGamesPiecesPerBoardField().size()<2){
            res = true;
        }else{
            for(GamePiece piece: field.getListGamesPiecesPerBoardField()){
                if(!piece.getTokenColor().equals(color)){
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    private static Boolean checkHomePieces(User user){
        Boolean res = false;
        for(GamePiece piece: user.getGamePieces()){
            if (piece.getField()==null){
                res = true;
                break;
            }
        }
        return res;
    }


    }
