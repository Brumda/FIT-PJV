package thedrake.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import thedrake.code.PlayingSide;
import thedrake.code.Troop;

import java.util.List;

public class CapturedView extends VBox {
    private final VBox capturedDisplay;
    private final PlayingSide capturedSide;
    Label label = new Label("Captured: ");

    public CapturedView(List<Troop> captured, PlayingSide side) {
        this.capturedSide = side;
        capturedDisplay = new VBox();
        capturedDisplay.setSpacing(10);
        capturedDisplay.setMaxWidth(Double.MAX_VALUE);
        capturedDisplay.setAlignment(Pos.CENTER);
        label.setFont(Font.font("PAPYRUS", FontWeight.BOLD, 35));
        label.setTextFill(side.equals(PlayingSide.BLUE) ? Color.DARKORANGE : Color.DEEPSKYBLUE);
        label.getStyleClass().add("outline");
        update(captured);

    }

    public VBox getCapturedDisplay() {
        return capturedDisplay;
    }

    public void update(List<Troop> captured) {
        capturedDisplay.getChildren().clear();
        capturedDisplay.getChildren().add(label);
        for (Troop troop : captured) {
            Pane troopTile = new StackTileView(troop, capturedSide);
            capturedDisplay.getChildren().add(troopTile);
        }
        capturedDisplay.autosize();
    }
}
