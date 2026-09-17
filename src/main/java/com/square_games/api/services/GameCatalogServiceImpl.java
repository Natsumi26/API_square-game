package com.square_games.api.services;

import com.square_games.api.plugins.GamePlugin;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


@Service
public class GameCatalogServiceImpl implements GameCatalogService {

    private final List<GamePlugin> gamePlugins;

    public GameCatalogServiceImpl(List<GamePlugin> gamePlugins) {
        this.gamePlugins = gamePlugins;
    }


    @Override
    public Collection<String> getAvailableGames(Locale locale) {

        return gamePlugins.stream()
                .map(plugin -> plugin.getName(locale))
                .toList();
    }
}