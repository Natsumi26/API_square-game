package com.square_games.api.dao;

import com.square_games.api.models.GameTokenEntity;
import fr.le_campus_numerique.square_games.engine.*;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGame;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.*;

public class ConnectFourGameReconstructed implements Game {

    private final UUID id;
    private final ConnectFourGame game;

    public ConnectFourGameReconstructed(UUID id, ConnectFourGame game) {
        this.id = id;
        this.game = game;
    }

    @Override
    public @NotNull UUID getId() {
        return id;
    }

    @Override
    public @NotBlank String getFactoryId() {
        return game.getFactoryId();
    }

    @Override
    public @NotEmpty Set<UUID> getPlayerIds() {
        return game.getPlayerIds();
    }

    @Override
    public @NotNull GameStatus getStatus() {
        return game.getStatus();
    }

    @Override
    public UUID getCurrentPlayerId() {
        return game.getCurrentPlayerId();
    }

    @Override
    public @Min(2L) int getBoardSize() {
        return game.getBoardSize();
    }

    @Override
    public @NotNull Map<CellPosition, Token> getBoard() {
        return game.getBoard();
    }

    @Override
    public @NotNull Collection<Token> getRemainingTokens() {
        return game.getRemainingTokens();
    }

    @Override
    public @NotNull Collection<Token> getRemovedTokens() {
        return game.getRemovedTokens();
    }

    public static ConnectFourGameReconstructed reconstruct(
            UUID gameId,
            Set<UUID> playerIds,
            Collection<GameTokenEntity> tokenEntities
    ) {
        UUID redPlayerId = null;
        UUID yellowPlayerId = null;

        for (GameTokenEntity token : tokenEntities) {

            if ("R".equals(token.name)) {
                redPlayerId = UUID.fromString(token.ownerId);
            }

            if ("Y".equals(token.name)) {
                yellowPlayerId = UUID.fromString(token.ownerId);
            }

        }

// Si on connaît Rouge, l'autre joueur est Jaune
        if (redPlayerId != null && yellowPlayerId == null) {
            UUID finalRedPlayerId = redPlayerId;
            yellowPlayerId = playerIds.stream()
                    .filter(id -> !id.equals(finalRedPlayerId))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Impossible de déterminer le joueur Jaune"
                            )
                    );
        }

// Si on connaît Jaune, l'autre joueur est Rouge
        if (yellowPlayerId != null && redPlayerId == null) {
            UUID finalYellowPlayerId = yellowPlayerId;
            redPlayerId = playerIds.stream()
                    .filter(id -> !id.equals(finalYellowPlayerId))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Impossible de déterminer le joueur Rouge"
                            )
                    );
        }

        // Partie encore vide
        if (redPlayerId == null && yellowPlayerId == null) {
            List<UUID> players = new ArrayList<>(playerIds);

            redPlayerId = players.get(0);
            yellowPlayerId = players.get(1);
        }

        if (redPlayerId == null || yellowPlayerId == null) {
            throw new IllegalStateException(
                    "Impossible de déterminer les joueurs Rouge et Jaune"
            );
        }

        ConnectFourGame game =
                new ConnectFourGame(redPlayerId, yellowPlayerId);

        for (GameTokenEntity tokenEntity : tokenEntities) {

            if (tokenEntity.removed) {
                continue;
            }

            if (tokenEntity.x == null || tokenEntity.y == null) {
                continue;
            }

            System.out.println(
                    "Token BDD : "
                            + tokenEntity.name
                            + " / "
                            + tokenEntity.ownerId
                            + " / position : ("
                            + tokenEntity.x
                            + ","
                            + tokenEntity.y
                            + ")"
            );
            Token gameToken = game.getRemainingTokens()
                    .stream()
                    .filter(token ->
                            token.getOwnerId().isPresent()
                                    && token.getOwnerId().get().equals(
                                    UUID.fromString(tokenEntity.ownerId)
                            )
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Impossible de trouver le token du joueur "
                                            + tokenEntity.ownerId
                            )
                    );
            try{
                gameToken.moveTo(
                        new CellPosition(
                                tokenEntity.x,
                                -1
                        )
                );
            } catch (InvalidPositionException e) {
                throw new IllegalStateException(
                        "Impossible de reconstruire le token "
                                + tokenEntity.name
                                + " en position ("
                                + tokenEntity.x
                                + ", "
                                + tokenEntity.y
                                + ")",
                        e
                );
            }


        }

        return new ConnectFourGameReconstructed(
                gameId,
                game
        );
    }
}
