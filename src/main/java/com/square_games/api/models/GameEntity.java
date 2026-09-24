package com.square_games.api.models;

import fr.le_campus_numerique.square_games.engine.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table
public class GameEntity {
    @Id
    public String id;
    public String factoryId;
    public int boardSize;
    public String playerIds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<GameTokenEntity> tokens;


    public static GameEntity fromGame(Game game) {
        GameEntity entity = new GameEntity();

        entity.id = game.getId().toString();
        entity.factoryId = game.getFactoryId();
        entity.boardSize = game.getBoardSize();

        System.out.println("===== JOUEURS SAUVEGARDÉS =====");
        System.out.println(game.getPlayerIds());
        System.out.println("===============================");
        System.out.println("Factory : " + game.getFactoryId());
        System.out.println("Players : " + game.getPlayerIds());
        System.out.println("Current : " + game.getCurrentPlayerId());

        List<UUID> playerIds = new ArrayList<>(game.getPlayerIds());

        if ("connectfour".equals(game.getFactoryId())) {
            UUID currentPlayerId = game.getCurrentPlayerId();

            if (currentPlayerId != null && currentPlayerId.equals(playerIds.get(0))) {
                // getPlayerIds() retourne le joueur courant en premier.
                // Pour Connect Four, on veut conserver l'ordre R puis Y.
                playerIds = playerIds.reversed();
            }
        }
        entity.playerIds = playerIds.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));

        entity.tokens = game.getBoard() // retourne <CellPosition, token>
                .values() // recupere que les tokens
                .stream() //transforme en flux
                .map(token -> { //Transforme les tokens en tokenEntity
                    GameTokenEntity tokenEntity = new GameTokenEntity();

                    tokenEntity.ownerId = token.getOwnerId()
                            .map(UUID::toString)
                            .orElse(null);

                    tokenEntity.name = token.getName();

                    CellPosition position = token.getPosition();

                    tokenEntity.x = position.x();
                    tokenEntity.y = position.y();

                    tokenEntity.removed = false;
                    System.out.println(
                            "TOKEN SAUVEGARDÉ : "
                                    + tokenEntity.name
                                    + " | owner=" + tokenEntity.ownerId
                                    + " | position=("
                                    + tokenEntity.x
                                    + ","
                                    + tokenEntity.y
                                    + ")"
                    );
                    return tokenEntity;
                })
                .toList();


        return entity;
    }



}
