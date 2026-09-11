package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.Locale;

public interface GamePlugin {
    Game createGame(Integer playerCount, Integer boardSize);
    String getName(Locale locale);
}
