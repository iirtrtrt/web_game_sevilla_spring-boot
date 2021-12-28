package org.springframework.samples.parchisoca.game.AI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.game.Game;
import org.springframework.samples.parchisoca.game.GameService;
import org.springframework.samples.parchisoca.game.Option;
import org.springframework.samples.parchisoca.game.Parchis;
import org.springframework.samples.parchisoca.game.ParchisService;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.stereotype.Service;


@Service
public class AIService {

       GameService gameService;

       @Autowired
       AIService(GameService gameservice){
              gameService = gameservice;
       }
       
	public void choosePlay(Game game, ParchisService parchisservice) {
              Parchis parchis = (Parchis) game.getGameboard();
              List<Option> options = parchis.getOptions();
              User ai = game.getCurrent_player();
              options.get(0).setChoosen(true);
              //Mre Logic here
              game.setTurn_state(TurnState.MOVE);
              gameService.saveGame(game);
              parchisservice.handleState(game);

	}


    
}
