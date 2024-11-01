package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.code.BoardPos;
import thedrake.code.GameState;
import thedrake.code.Move;
import thedrake.code.PositionFactory;

import java.util.List;

public class BoardView extends GridPane implements TileViewContext {
    private final GameUpdate game;
    private GameState gameState;
    private TileView selected;
    private ValidMoves validMoves;

    public BoardView(GameState gameState, GameUpdate game) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);
        this.game = game;
        PositionFactory positionFactory = gameState.board().positionFactory();

        for (int y = 0; y < positionFactory.dimension(); y++) {
            for (int x = 0; x < positionFactory.dimension(); x++) {
                BoardPos boardPos = positionFactory.pos(x, getInvertedJ(y));
                add(new TileView(gameState.tileAt(boardPos), boardPos, this), x, y);
            }
        }
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    public void stackSelected(Boolean isSelected) {
        if (isSelected) {
            setStackSelected(true);
            if (selected != null) {
                selected.unselect();
                clearMoves();
            }
        } else
            setStackSelected(false);
    }

    private void updateTiles() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
        }
    }

    @Override
    public void tileViewSelected(TileView tileView) throws Exception {
        if (selected != null && selected != tileView)
            selected.unselect();

        selected = tileView;
        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));

    }

    @Override
    public void stackUnselect() throws Exception {
        game.stackSelected();
    }

    @Override
    public void executeMove(Move move) throws Exception {
        if (selected != null) {
            selected.unselect();
            selected = null;
        }
        clearMoves();
        gameState = move.execute(gameState);
        setStackSelected(false);
        validMoves = new ValidMoves(gameState);
        updateTiles();
        game.updateGame(gameState, false);
    }

    private void setStackSelected(Boolean selected) {
        for (Node node : getChildren())
            ((TileView) node).setStackSelected(selected);
    }

    public void showMoves(List<Move> moves) throws Exception {
        for (Move move : moves)
            tileViewAt(move.target()).setMove(move);
    }

    public void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private TileView tileViewAt(BoardPos target) {
        int index = getInvertedJ(target.j()) * gameState.board().dimension() + target.i();
        return (TileView) getChildren().get(index);
    }

    private int getInvertedJ(int wrongJ) {
        return gameState.board().dimension() - 1 - wrongJ;
    }
}
