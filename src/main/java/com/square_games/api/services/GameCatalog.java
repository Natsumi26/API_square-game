package com.square_games.api.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface GameCatalog {


    Collection<String> getAvailableGames(Locale locale);
}