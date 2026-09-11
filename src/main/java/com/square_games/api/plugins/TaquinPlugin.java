package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TaquinPlugin implements GamePlugin {

    private final MessageSource messageSource;

    private final TaquinGameFactory gameFactory;

    @Value("${game.taquin.default-player-count}")
    private Integer defaultPlayerCount;

    @Value("${game.taquin.default-board-size}")
    private Integer defaultBoardSize;

    public TaquinPlugin(MessageSource messageSource, TaquinGameFactory gameFactory) {
        this.messageSource = messageSource;
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
        return messageSource.getMessage("game.taquin.name", null, locale);
    }
}
