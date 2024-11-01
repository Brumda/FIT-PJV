package thedrake.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.code.BoardPos;
import thedrake.code.Move;
import thedrake.code.Tile;

public class TileView extends Pane {
    private final TileBackgrounds backgrounds = new TileBackgrounds();
    private final BoardPos position;
    private final Border selectionBorder = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)));

    private final ImageView moveImage;
    private final TileViewContext context;
    private Tile tile;
    private Move move;
    private boolean stackSelected = false;

    public TileView(Tile tile, BoardPos boardPos, TileViewContext context) {
        this.tile = tile;
        this.position = boardPos;
        this.context = context;
        setPrefSize(100, 100);
        update();
        setOnMouseClicked(e -> {
            try {
                onClick();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        moveImage = new ImageView((getClass().getResource("/assets/move.png")).toString());
        moveImage.setVisible(false);
        getChildren().add(moveImage);
    }

    public void setStackSelected(boolean stackSelected) {
        this.stackSelected = stackSelected;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        update();
    }

    private void onClick() throws Exception {
        if (move != null)
            context.executeMove(move);
        else if (tile.hasTroop()) {
            if (stackSelected)
                context.stackUnselect();
            select();
        }

    }

    private void select() throws Exception {
        setBorder(selectionBorder);
        context.tileViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    private void update() {
        setBackground(backgrounds.get(tile));
    }

    public BoardPos position() {
        return position;
    }

    public void setMove(Move move) {
        this.move = move;
        moveImage.setVisible(true);
    }

    public void clearMove() {
        this.move = null;
        moveImage.setVisible(false);
    }
}
