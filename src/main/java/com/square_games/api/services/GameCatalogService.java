package com.square_games.api.services;

import java.util.Collection;
import java.util.Locale;

public interface GameCatalogService {


    Collection<String> getAvailableGames(Locale locale);
}