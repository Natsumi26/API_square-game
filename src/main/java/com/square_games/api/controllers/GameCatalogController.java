package com.square_games.api.controllers;

import com.square_games.api.services.GameCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Locale;

@Tag(name = "Listegames", description = "Liste des jeux")
@RestController
@RequestMapping("/listegames")
public class GameCatalogController {

    private final GameCatalogService gameCatalogService;

    public GameCatalogController(GameCatalogService gameCatalogService) {
        this.gameCatalogService = gameCatalogService;
    }

    @Operation(
            summary = "Lister les differents jeux",
            description = "Retourne les differents jeux auxquelles on peut jouer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux récupérée"),
    })
    @GetMapping
    public Collection<String> getGames(Locale locale) {
        return gameCatalogService.getAvailableGames(locale);
    }
}