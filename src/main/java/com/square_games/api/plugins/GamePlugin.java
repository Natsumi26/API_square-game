package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public interface GamePlugin {
    Game createGame(Integer playerCount, Integer boardSize, Set<UUID> playerIds);
    String getName(Locale locale);
}
