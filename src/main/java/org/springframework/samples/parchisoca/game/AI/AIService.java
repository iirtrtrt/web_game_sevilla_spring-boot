package org.springframework.samples.parchisoca.game.AI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.game.BoardFieldService;
import org.springframework.samples.parchisoca.game.Game;
import org.springframework.samples.parchisoca.game.GameService;
import org.springframework.samples.parchisoca.game.Oca;
import org.springframework.samples.parchisoca.game.OcaService;
import org.springframework.samples.parchisoca.game.Option;
import org.springframework.samples.parchisoca.game.OptionService;
import org.springframework.samples.parchisoca.game.Parchis;
import org.springframework.samples.parchisoca.game.ParchisService;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.stereotype.Service;


@Service
public class AIService {

       GameService gameService;
       OptionService optionService;
       BoardFieldService boardFieldService;

       @Autowired
       private StrategyFactory strategyFactory;

       @Autowired
       AIService(GameService gameservice,  StrategyFactory strategyFactory, BoardFieldService boardFieldService, OptionService optionService){
              this.gameService = gameservice;
              this.strategyFactory = strategyFactory;
              this.boardFieldService = boardFieldService;
              this.optionService = optionService;
       }
       
	public void choosePlay(Game game, ParchisService parchisService) {
              Parchis parchis = (Parchis) game.getGameboard();
              List<Option> options = parchis.getOptions();
             // User ai = game.getCurrent_player();

             strategyFactory.findStrategy(options, game, boardFieldService, optionService);


              if(game.getTurn_state().equals(TurnState.DIRECTPASS)){
                     game.setTurn_state(TurnState.PASSMOVE);
              }
              else{
                     game.setTurn_state(TurnState.MOVE);
              }
              gameService.saveGame(game);
              parchisService.handleState(game);

	}

       public void choosePlay(Game game, OcaService ocaservice) {
              Oca oca = (Oca) game.getGameboard();
              List<Option> options = oca.getOptions();
              User ai = game.getCurrent_player();
              options.get(0).setChoosen(true);

              //More Logic here

              if(game.getTurn_state().equals(TurnState.DIRECTPASS)){
                     game.setTurn_state(TurnState.PASSMOVE);
              }
              else{
                     game.setTurn_state(TurnState.MOVE);
              }
              gameService.saveGame(game);
              ocaservice.handleState(game);
	}


    
}
