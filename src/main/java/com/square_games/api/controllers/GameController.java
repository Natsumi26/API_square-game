package com.square_games.api.controllers;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.DTO.GameResponseDto;
import com.square_games.api.DTO.MoveParams;
import com.square_games.api.services.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Games", description = "Gestion des parties")
@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(
            summary = "Lister les parties",
            description = "Retourne les parties auxquelles participe l'utilisateur connecté."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des parties récupérée"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu")
    })
    @GetMapping
    public Collection<Game> getGames() {
        UUID userId = getCurrentUserId();
        return gameService.getGames(userId);
    }


    @Operation(
            summary = "Créer une partie",
            description = "Crée une nouvelle partie pour l'utilisateur indiqué dans X-UserId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partie créée"),
            @ApiResponse(responseCode = "400", description = "Paramètres de création invalides"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu")
    })
    @PostMapping
    public Game createGame(@RequestBody GameCreationParams params) {
        UUID userId = getCurrentUserId();
        return gameService.createGame(userId, params);
    }

    @Operation(
            summary = "Récupérer une partie",
            description = "Retourne les informations d'une partie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partie trouvée"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu"),
            @ApiResponse(responseCode = "404", description = "Partie inconnue")
    })

    @GetMapping("/{gameId}")
    public GameResponseDto getGame(@PathVariable String gameId) {
        UUID userId = getCurrentUserId();
        return gameService.getGameById(userId, gameId);
    }

    @Operation(
            summary = "Récupérer le status d'une partie",
            description = "Retourne le status d'une partie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status trouvée"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu"),
            @ApiResponse(responseCode = "404", description = "Partie inconnue")
    })
    @GetMapping("/status/{gameId}")
    public GameStatus getGameStatus(@PathVariable String gameId) {
        UUID userId = getCurrentUserId();
        return gameService.getGameStatus(userId,gameId);
    }

    @Operation(
            summary = "Lister les parties en cours",
            description = "Retourne les parties en cours auxquelles participe l'utilisateur connecté."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des parties en cours récupérée"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu")
    })
    @GetMapping("/ongoing")
    public Collection<Game> getOngoingGames() {
        UUID userId = getCurrentUserId();
        return gameService.getOngoingGames(userId);
    }

    @Operation(
            summary = "Supprimer une partie",
            description = "Supprime une partie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partie supprimée"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu"),
            @ApiResponse(responseCode = "404", description = "Partie inconnue")
    })
    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable String gameId) {
        UUID userId = getCurrentUserId();
        gameService.deleteGameById(userId, gameId);
    }

    @Operation(
            summary = "Lister les coups possibles",
            description = "Retourne les positions auxquelles le joueur courant peut jouer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coups possibles récupérés"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu"),
            @ApiResponse(responseCode = "404", description = "Partie inconnue")
    })
    @GetMapping("/{gameId}/possiblemoves")
    public Set<CellPosition> getAllowedMoves( @PathVariable String gameId) {
        UUID userId = getCurrentUserId();
        return gameService.getAllowedMoves(
                userId,
                gameId,
                null
        );
    }

    @Operation(
            summary = "Lister les coups possibles d'un jeton",
            description = "Retourne les positions accessibles par le jeton situé à la position indiquée."
    )
    @GetMapping("/{gameId}/tokens/{x}/{y}/possiblemoves")
    public Set<CellPosition> getAllowedMoves( @PathVariable String gameId, @PathVariable int x, @PathVariable int y) {
        CellPosition position = new CellPosition(x, y);
        UUID userId = getCurrentUserId();
        return gameService.getAllowedMoves(
                userId,
                gameId,
                position
        );
    }

    @Operation(
            summary = "Jouer un coup",
            description = "Joue un coup dans une partie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coup joué"),
            @ApiResponse(responseCode = "400", description = "Coup invalide"),
            @ApiResponse(responseCode = "401", description = "Utilisateur inconnu"),
            @ApiResponse(responseCode = "403", description = "Ce n'est pas le tour du joueur"),
            @ApiResponse(responseCode = "404", description = "Partie inconnue")
    })
    @PostMapping("/{gameId}/moves")
    public void playMove( @PathVariable String gameId, @RequestBody MoveParams params) {
        UUID userId = getCurrentUserId();
        gameService.playMove(
                userId,
                gameId,
                params.getFrom(),
                params.getTo()
        );
    }

    private UUID getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (UUID) authentication.getPrincipal();
    }

}
