package thedrake.ui;

import thedrake.code.GameState;

public interface GameUpdate {

    void updateGame(GameState gameState, Boolean stackSelected) throws Exception;

    void stackSelected() throws Exception;
}
