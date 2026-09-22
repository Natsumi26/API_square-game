package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import fr.le_campus_numerique.square_games.engine.InvalidPositionException;
import fr.le_campus_numerique.square_games.engine.Token;

import java.util.*;

public class TaquinGameReconstructed implements Game {

    private final UUID id;
    private final UUID playerId;
    private final int boardSize;
    private final List<Tile> tiles;

    public TaquinGameReconstructed(
            UUID id,
            UUID playerId,
            int boardSize,
            Collection<TileData> tokenData
    ) {
        if (boardSize < 3 || boardSize > 8) {
            throw new IllegalArgumentException(
                    "board size must be between 3 and 8"
            );
        }

        this.id = Objects.requireNonNull(id);
        this.playerId = Objects.requireNonNull(playerId);
        this.boardSize = boardSize;

        int expectedTokens = boardSize * boardSize - 1;

        if (tokenData.size() != expectedTokens) {
            throw new IllegalArgumentException(
                    "Expected " + expectedTokens +
                            " tokens but found " + tokenData.size()
            );
        }

        this.tiles = new ArrayList<>(
                Collections.nCopies(boardSize * boardSize, null)
        );

        for (TileData data : tokenData) {

            if (data.x() < 0 || data.x() >= boardSize ||
                    data.y() < 0 || data.y() >= boardSize) {

                throw new IllegalArgumentException(
                        "Invalid token position: "
                                + data.x() + "," + data.y()
                );
            }

            int value;

            try {
                value = Integer.parseInt(data.name());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid token name: " + data.name()
                );
            }

            if (value < 1 || value >= boardSize * boardSize) {
                throw new IllegalArgumentException(
                        "Invalid token value: " + value
                );
            }

            int index = indexOf(data.x(), data.y());

            if (tiles.get(index) != null) {
                throw new IllegalArgumentException(
                        "Several tokens have the same position"
                );
            }

            tiles.set(index, new Tile(value));
        }
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getFactoryId() {
        return "15 puzzle";
    }

    @Override
    public Set<UUID> getPlayerIds() {
        return Set.of(playerId);
    }

    @Override
    public GameStatus getStatus() {
        return tiles.get(tiles.size() - 1) == null
                ? GameStatus.TERMINATED
                : GameStatus.ONGOING;
    }

    @Override
    public UUID getCurrentPlayerId() {
        return playerId;
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public Map<CellPosition, Token> getBoard() {

        Map<CellPosition, Token> result = new LinkedHashMap<>();

        for (int i = 0; i < tiles.size(); i++) {

            Tile tile = tiles.get(i);

            if (tile != null) {
                result.put(indexToPosition(i), tile);
            }
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Collection<Token> getRemainingTokens() {
        return Set.of();
    }

    @Override
    public Collection<Token> getRemovedTokens() {
        return Set.of();
    }

    private int indexOf(int x, int y) {
        return y * boardSize + x;
    }

    private CellPosition indexToPosition(int index) {
        return new CellPosition(
                index % boardSize,
                index / boardSize
        );
    }

    private int indexOfValue(int value) {

        for (int i = 0; i < tiles.size(); i++) {

            Tile tile = tiles.get(i);

            if (tile != null && tile.value == value) {
                return i;
            }
        }

        return -1;
    }

    private CellPosition positionOf(int value) {

        int index = indexOfValue(value);

        if (index < 0) {
            return null;
        }

        return indexToPosition(index);
    }

    private CellPosition unoccupiedPosition() {

        int index = tiles.lastIndexOf(null);

        if (index < 0) {
            throw new IllegalStateException(
                    "No empty position found"
            );
        }

        return indexToPosition(index);
    }

    private boolean areNeighbors(
            CellPosition a,
            CellPosition b
    ) {

        return Math.abs(a.x() - b.x())
                + Math.abs(a.y() - b.y()) == 1;
    }

    private void slideTile(
            CellPosition currentPosition,
            CellPosition destination
    ) {

        int currentIndex =
                indexOf(currentPosition.x(), currentPosition.y());

        int destinationIndex =
                indexOf(destination.x(), destination.y());

        if (tiles.get(currentIndex) == null) {
            throw new IllegalStateException(
                    "No tile at current position"
            );
        }

        if (tiles.get(destinationIndex) != null) {
            throw new IllegalStateException(
                    "Destination is not empty"
            );
        }

        tiles.set(
                destinationIndex,
                tiles.get(currentIndex)
        );

        tiles.set(currentIndex, null);
    }

    private void moveTileTo(
            Tile tile,
            CellPosition position
    ) throws InvalidPositionException {

        CellPosition currentPosition =
                positionOf(tile.value);

        if (currentPosition == null) {
            throw new InvalidPositionException(
                    "Token position not found"
            );
        }

        if (position.x() < 0 ||
                position.y() < 0 ||
                position.x() >= boardSize ||
                position.y() >= boardSize) {

            throw new InvalidPositionException(
                    "invalid position"
            );
        }

        if (!areNeighbors(currentPosition, position)) {

            throw new InvalidPositionException(
                    "invalid position for token"
            );
        }

        int destinationIndex =
                indexOf(position.x(), position.y());

        if (tiles.get(destinationIndex) != null) {

            throw new InvalidPositionException(
                    "destination position is not available"
            );
        }

        slideTile(currentPosition, position);
    }

    public record TileData(
            String name,
            int x,
            int y
    ) {
    }

    private class Tile implements Token {

        private final int value;

        private Tile(int value) {
            this.value = value;
        }

        @Override
        public Optional<UUID> getOwnerId() {
            return Optional.of(playerId);
        }

        @Override
        public String getName() {
            return String.valueOf(value);
        }

        @Override
        public CellPosition getPosition() {
            return positionOf(value);
        }

        @Override
        public Set<CellPosition> getAllowedMoves() {

            CellPosition current =
                    positionOf(value);

            CellPosition empty =
                    unoccupiedPosition();

            if (areNeighbors(current, empty)) {
                return Set.of(empty);
            }

            return Set.of();
        }

        @Override
        public void moveTo(CellPosition position)
                throws InvalidPositionException {

            Objects.requireNonNull(position);

            moveTileTo(this, position);
        }
    }
}