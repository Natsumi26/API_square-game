package com.square_games.api.models;

import fr.le_campus_numerique.square_games.engine.*;

import jakarta.persistence.*;

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
        entity.playerIds = game.getPlayerIds()
                .stream() // transforme en flux
                .map(UUID::toString)// transforme le type UUId en string
                .collect(Collectors.joining(",")); // recupere les données et défini la "," comme séparateur

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
                    return tokenEntity;
                })
                .toList();


        return entity;
    }



}
