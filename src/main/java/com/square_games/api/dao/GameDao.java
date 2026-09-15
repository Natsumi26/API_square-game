package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.Collection;
import java.util.UUID;


public interface GameDao {
    Collection<Game> findAll(UUID userId);
    Game findById(UUID userId, String gameId);
    void upsert(UUID userId, Game game);
    void delete(UUID userId, String gameId);
}
