package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.*;


public class InMemoryGameDao implements GameDao {

    private final Map<String, Game> games = new HashMap<>();

    @Override
    public Collection<Game> findAll(UUID userId) {
        return games.values();
    }

    @Override
    public Game findById(UUID userId, String gameId) {
        return games.get(gameId);
    }

    @Override
    public void upsert(UUID userId, Game game) {
        games.put(String.valueOf(game.getId()), game);
    }

    @Override
    public void delete(UUID userId, String gameId) {
        games.remove(gameId);
    }
}
