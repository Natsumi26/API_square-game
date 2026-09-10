package com.square_games.api.service;

import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class GameCatalogImpl implements GameCatalog {

    private final TicTacToeGameFactory gameFactory;

    public GameCatalogImpl(TicTacToeGameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }


    @Override
    public Collection<String> getAvailableGames() {
        return List.of(gameFactory.getGameFactoryId());
    }
}
