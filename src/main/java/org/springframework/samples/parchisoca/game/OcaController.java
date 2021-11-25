package org.springframework.samples.parchisoca.game;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.configuration.GenericIdToEntityConverter;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.samples.parchisoca.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.samples.parchisoca.configuration.GenericIdToEntityConverter;
import org.springframework.samples.parchisoca.user.User;

import java.util.Optional;

@Controller
@RequestMapping("/game/oca")
public class OcaController {


    private static final Logger logger = LoggerFactory.getLogger(OcaController.class);


    @Autowired
    OcaService ocaService;
    @Autowired
    GameService gameService;
    @Autowired
    UserService userService;


    private static final String VIEWS_GAME = "game/ocaGame";
    private static final String VIEWS_JOIN_GAME_OCA = "game/oca/join/";

    @Autowired
    public OcaController(GameService gameService, OcaService ocaService, UserService userservice){
        this.ocaService = ocaService;
        this.gameService = gameService;
        this.userService = userservice;
    }

    @GetMapping(value = "{gameid}")
    public String initCanvasForm(@PathVariable("gameid") int gameid, ModelMap model, HttpServletResponse response) {
        Game game = this.gameService.findGamebyID(gameid).get();

        ocaService.initGameBoard(game);

        System.out.println("game width:  " + game.getGameboard().getWidth());
        System.out.println("game height:  " + game.getGameboard().getHeight());

        return "redirect:/" + VIEWS_JOIN_GAME_OCA + gameid;
    }

    @GetMapping(value = "/join/{gameid}")
    public String joinOca(@PathVariable("gameid") int gameid, ModelMap model, HttpServletResponse response) {
        Optional < Game > gameOptional = this.gameService.findGamebyID(gameid);
        Game game = gameOptional.orElseThrow(EntityNotFoundException::new);
        User user  = userService.getCurrentUser().get();

        GamePiece piezas = user.getGamePieces().get(0);
        if(piezas.getField() == null){
            piezas.setField(game.getStartField());
        }

        logger.info("gamePiece: " + user.getGamePieces().get(0).getField().isNew());
        logger.info("gamePiece field: " + user.getGamePieces().get(0).getField().getNumber());
        ocaService.handleState(game);
        userService.saveUser(user);


        model.addAttribute("currentuser", userService.getCurrentUser().get());
        model.put("game",game);

        return VIEWS_GAME;
    }

    @GetMapping(value = "/join/{gameid}/dice")
    public String diceRole(@PathVariable("gameid") int gameid, ModelMap model, HttpServletResponse response) {
        //check if this is the current user
        System.out.println("inDice");
        Optional < Game > gameOptional = this.gameService.findGamebyID(gameid);
        Game game = gameOptional.orElseThrow(EntityNotFoundException::new);
        game.setTurn_state(TurnState.ROLLDICE);
        gameService.saveGame(game);

        //parchisService.handleState(game);

        return "redirect:/" + VIEWS_JOIN_GAME_OCA + gameid;
    }
}
