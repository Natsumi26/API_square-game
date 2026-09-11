package com.square_games.api.services;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final TicTacToeGameFactory gameFactory;

    @Value("${game.tictactoe.default-player-count}")
    private int defaultPlayerCount;

    @Value("${game.tictactoe.default-board-size}")
    private int defaultBoardSize;

    public TicTacToePlugin(TicTacToeGameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }

    @Override
    public Game createGame(Integer playerCount, Integer boardSize) {
        if (playerCount == null) {
            playerCount = defaultPlayerCount;
        }

        if(boardSize == null) {
            boardSize = defaultBoardSize;
        }

        return gameFactory.createGame(playerCount, boardSize);
    }

    @Override
    public String getName(Locale locale) {
        if(locale.getLanguage().equals(Locale.FRENCH.getLanguage())) {
            return "Morpion";
        }
        return "Tic Tac Toe";
    }
}
