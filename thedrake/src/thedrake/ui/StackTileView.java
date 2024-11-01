package thedrake.ui;

import javafx.scene.layout.Pane;
import thedrake.code.PlayingSide;
import thedrake.code.Troop;
import thedrake.code.TroopFace;

public class StackTileView extends Pane {
    public StackTileView(Troop troop, PlayingSide playingSide) {
        setPrefSize(100, 100);
        setMaxSize(100, 100);
        TileBackgrounds tileBackground = new TileBackgrounds();
        setBackground(tileBackground.getTroop(troop, playingSide, TroopFace.AVERS));
    }
}
