package com.square_games.api.services;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TaquinPlugin implements GamePlugin{

    private final TaquinGameFactory gameFactory;

    @Value("${game.taquin.default-player-count}")
    private int defaultPlayerCount;

    @Value("${game.taquin.default-board-size}")
    private int defaultBoardSize;

    public TaquinPlugin(TaquinGameFactory gameFactory) {
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
        return "Taquin";
    }
}
