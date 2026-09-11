package com.square_games.api.services;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ConnectFourPlugin implements GamePlugin {

    private final ConnectFourGameFactory gameFactory;

    @Value("${game.connectfour.default-player-count}")
    private int defaultPlayerCount;

    @Value("${game.connectfour.default-board-size}")
    private int defaultBoardSize;

    public ConnectFourPlugin(ConnectFourGameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }

    @Override
    public Game createGame(Integer playerCount, Integer boardSize) {
        if(playerCount == null){
            playerCount = defaultPlayerCount;
        }
        if(boardSize == null){
            boardSize = defaultBoardSize;
        }
        return gameFactory.createGame(playerCount, boardSize);
    }

    @Override
    public String getName(Locale locale) {
        if(locale.getLanguage().equals(Locale.FRENCH.getLanguage())){
            return "Puissance 4";
        }
        return "Connect Four";
    }
}
