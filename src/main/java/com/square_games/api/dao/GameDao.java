package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.Collection;


public interface GameDao {
    Collection<Game> findAll();
    Game findById(String gameId);
    void upsert(Game game);
    void delete(String gameId);
}
