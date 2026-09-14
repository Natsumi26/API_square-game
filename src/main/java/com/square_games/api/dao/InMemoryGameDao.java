package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.Game;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;


public class InMemoryGameDao implements GameDao {

    private final Map<String, Game> games = new HashMap<>();

    @Override
    public Collection<Game> findAll() {
        return games.values();
    }

    @Override
    public Game findById(String gameId) {
        return games.get(gameId);
    }

    @Override
    public void upsert(Game game) {
        games.put(String.valueOf(game.getId()), game);
    }

    @Override
    public void delete(String gameId) {
        games.remove(gameId);
    }
}
