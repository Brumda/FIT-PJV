package thedrake.code;

import java.io.PrintWriter;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(Board board, Army blueArmy, Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(Board board, Army blueArmy, Army orangeArmy, PlayingSide sideOnTurn, GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE) return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if (blueArmy.boardTroops().troopPositions().contains(pos)) return blueArmy.boardTroops().at(pos).get();
        if (orangeArmy.boardTroops().troopPositions().contains(pos)) return orangeArmy.boardTroops().at(pos).get();
        return board.at(pos);
    }

    private boolean inPLay() {
        return result.equals(GameResult.IN_PLAY);
    }

    private boolean offBoard(TilePos target) {
        return target.equals(TilePos.OFF_BOARD);
    }

    private boolean canStepFrom(TilePos origin) {
        return !offBoard(origin) && inPLay() && armyOnTurn().boardTroops().at(
                origin).isPresent() && armyNotOnTurn().boardTroops().at(
                origin).isEmpty() && !armyOnTurn().boardTroops().isPlacingGuards() && armyOnTurn().boardTroops().isLeaderPlaced();
    }

    private boolean canStepTo(TilePos target) {
        return !offBoard(target) && inPLay() && tileAt(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        return inPLay() && armyNotOnTurn().boardTroops().at(target).isPresent();
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (armyOnTurn().stack().isEmpty() || !canStepTo(target)) return false;

        if (!armyOnTurn().boardTroops().isLeaderPlaced()) return armyOnTurn().side().equals(
                PlayingSide.BLUE) ? target.row() == 1 : target.row() == board().dimension();

        if (armyOnTurn().boardTroops().isPlacingGuards())
            return target.isNextTo(armyOnTurn().boardTroops().leaderPosition());

        for (var n : target.neighbours()) {
            if (armyOnTurn().boardTroops().at(n).isPresent()) return true;
        }
        return false;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(armyNotOnTurn(), armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target)) newResult = GameResult.VICTORY;

            return createNewGameState(armyNotOnTurn().removeTroop(target),
                                      armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target)) newResult = GameResult.VICTORY;

            return createNewGameState(armyNotOnTurn().removeTroop(target),
                                      armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(armyNotOnTurn(), armyOnTurn().placeFromStack(target), GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(armyNotOnTurn(), armyOnTurn(), GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(armyOnTurn(), armyNotOnTurn(), GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"result\":");
        result.toJSON(writer);
        writer.printf(",\"board\":");
        board.toJSON(writer);
        writer.printf(",\"blueArmy\":");
        blueArmy.toJSON(writer);
        writer.printf(",\"orangeArmy\":");
        orangeArmy.toJSON(writer);
        writer.printf("}");
    }
}
