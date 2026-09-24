package com.square_games.api.services;

import com.square_games.api.DTO.GameTypeDto;

import java.util.Collection;
import java.util.Locale;

public interface GameCatalogService {


    Collection<GameTypeDto> getAvailableGames(Locale locale);
}