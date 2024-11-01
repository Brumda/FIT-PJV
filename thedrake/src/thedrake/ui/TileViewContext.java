package thedrake.ui;

import thedrake.code.Move;

public interface TileViewContext {

    void tileViewSelected(TileView tileView) throws Exception;
    void stackUnselect() throws Exception;
    void executeMove(Move move) throws Exception;
}
