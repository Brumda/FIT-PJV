package thedrake.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import thedrake.code.PlayingSide;
import thedrake.code.Troop;

import java.util.List;

public class StackView extends HBox {

    private final Border troopBorder = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)));
    private final HBox stackDisplay;
    private final GameUpdate game;
    Label label = new Label("Troop stack: ");
    private List<Troop> stack;

    public StackView(List<Troop> stack, PlayingSide playingSide, GameUpdate game) {
        this.game = game;
        stackDisplay = new HBox();
        stackDisplay.setSpacing(5);
        stackDisplay.setAlignment(Pos.CENTER);
        label.setFont(Font.font("PAPYRUS", FontWeight.BOLD, 35));
        label.setTextFill(Color.WHITESMOKE);
        label.getStyleClass().add("outline");
        update(stack, playingSide);
    }

    public void onClick() throws Exception {
        if (!stack.isEmpty())
            select();
    }

    private void select() throws Exception {
        ((Pane) stackDisplay.getChildren().get(1)).setBorder(troopBorder);
        game.stackSelected();
    }

    public void unselect() {
        ((Pane) stackDisplay.getChildren().get(1)).setBorder(null);
    }

    public HBox getStackDisplay() {
        return stackDisplay;
    }

    public void update(List<Troop> stack, PlayingSide playingSide) {
        this.stack = stack;
        stackDisplay.getChildren().clear();
        stackDisplay.getChildren().add(label);

        for (Troop troop : stack) {
            Pane troopTile = new StackTileView(troop, playingSide);
            stackDisplay.getChildren().add(troopTile);
        }
        stackDisplay.autosize();
    }
}
