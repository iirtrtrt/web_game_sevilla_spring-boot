package org.springframework.samples.parchisoca.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateDirectPass {

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
        Option op = new Option();
        op.setNumber(0);
        op.setText("Let's decide the order!!");
        optionService.saveOption(op);
        parchis.options.add(op);
    }


    


}
