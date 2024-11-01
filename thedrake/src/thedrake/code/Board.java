package thedrake.code;

import java.io.PrintWriter;

public class Board implements JSONSerializable {
    final int dimension;
    private final BoardTile[][] board;

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy
    // BoardTile.EMPTY
    public Board(int dimension) {
        this.dimension = dimension;
        board = new BoardTile[dimension][dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++)
                board[i][j] = BoardTile.EMPTY;
        }
    }

    // Rozměr hrací desky
    public int dimension() {
        return this.dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
        return board[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {
        var newBoard = new Board(this.dimension);
        for (int i = 0; i < this.dimension; i++)
            newBoard.board[i] = board[i].clone();
        for (var tile : ats) {
            newBoard.board[tile.pos.i()][tile.pos.j()] = tile.tile;
        }
        return newBoard;
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        return new PositionFactory(this.dimension);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"dimension\":" + dimension + ",\"tiles\":[");
        boolean first = true;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (!first) writer.printf(",");
                else first = false;
                board[j][i].toJSON(writer);
            }
        }
        writer.printf("]}");
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}
