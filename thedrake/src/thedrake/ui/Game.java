package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import thedrake.code.GameResult;
import thedrake.code.GameState;
import thedrake.code.PlayingSide;

import java.util.Optional;


public class Game extends BorderPane implements GameUpdate {
    private final StackView stack;
    private final CapturedView capturedBlue;
    private final CapturedView capturedOrange;
    Label label = new Label();
    private boolean stackPlacing = false;
    private GameState gameState;

    public Game(GameState gameState) {
        this.gameState = gameState;
        BoardView boardView = new BoardView(gameState, this);
        this.setId("game");
        this.setCenter(boardView);

        this.capturedBlue = new CapturedView(gameState.army(PlayingSide.BLUE).captured(), PlayingSide.ORANGE);
        this.setLeft(capturedBlue.getCapturedDisplay());
        BorderPane.setMargin(capturedBlue.getCapturedDisplay(), new Insets(0, 0, 0, 20));

        this.capturedOrange = new CapturedView(gameState.army(PlayingSide.ORANGE).captured(), PlayingSide.BLUE);
        this.setRight(capturedOrange.getCapturedDisplay());
        BorderPane.setMargin(capturedOrange.getCapturedDisplay(), new Insets(0, 20, 0, 0));

        label.setText(getName(gameState.sideOnTurn()) + " on turn");
        label.setFont(Font.font("PAPYRUS", FontWeight.BOLD, 40));
        label.setTextFill(getColor());
        label.getStyleClass().add("outline");
        label.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(label, Pos.CENTER);
        this.setTop(label);

        BorderPane.setMargin(label, new Insets(20, 0, 0, 0));


        this.stack = new StackView(gameState.armyOnTurn().stack(), gameState.sideOnTurn(), this);

        BorderPane.setMargin(stack.getStackDisplay(), new Insets(0, 0, 20, 0));
        this.setBottom(stack.getStackDisplay());
        stack.getStackDisplay().setOnMouseClicked(e -> {
            try {
                stack.onClick();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    static private String getName(PlayingSide side) {
        return side.name().equals("BLUE") ? "Blue" : "Orange";
    }

    private Color getColor() {
        return (gameState.sideOnTurn().equals(PlayingSide.BLUE) ? Color.DEEPSKYBLUE : Color.DARKORANGE);
    }

    @Override
    public void updateGame(GameState gameState, Boolean stackSelected) throws Exception {
        this.gameState = gameState;
        this.stackPlacing = stackSelected;

        if (new ValidMoves(gameState).allMoves().isEmpty())
            gameState = gameState.resign();


        switch (gameState.result()) {
            case VICTORY -> gameEnd(false);
            case DRAW -> gameEnd(true);
            case IN_PLAY -> update();
        }
    }

    private void update() {
        label.setText((gameState.sideOnTurn().equals(PlayingSide.BLUE) ? "Blue" : "Orange") + " on turn");
        label.setTextFill(getColor());
        stack.update(gameState.armyOnTurn().stack(), gameState.sideOnTurn());
        capturedBlue.update(gameState.army(PlayingSide.BLUE).captured());
        capturedOrange.update(gameState.army(PlayingSide.ORANGE).captured());
    }

    @Override
    public void stackSelected() throws Exception {
        if (!gameState.result().equals(GameResult.IN_PLAY)) return;
        if (!stackPlacing) {
            stackPlacing = true;
            ((BoardView) this.getCenter()).stackSelected(true);
            ((BoardView) getCenter()).showMoves(new ValidMoves(gameState).movesFromStack());

        } else {
            stackPlacing = false;
            ((BoardView) this.getCenter()).stackSelected(false);
            stack.unselect();
            ((BoardView) getCenter()).clearMoves();
        }
    }

    private void gameEnd(Boolean draw) throws Exception {
        String message = getName(gameState.armyOnTurn().side()) +
                (gameState.armyOnTurn().boardTroops().isLeaderPlaced() ? " has no more moves left!" :
                 "'s drake was captured!");

        label.setText(draw ? "Draw!" : getName(gameState.armyNotOnTurn().side()) + " won!");
        ButtonType newGame = new ButtonType("New Game", ButtonBar.ButtonData.OK_DONE);
        ButtonType mainMenu = new ButtonType("Main Menu", ButtonBar.ButtonData.OK_DONE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game ended");
        alert.setHeaderText(message);
        alert.setContentText("Play again?");
        alert.getButtonTypes().setAll(newGame, mainMenu);
        alert.getDialogPane().setGraphic(null);
        Stage stage = (Stage) this.getScene().getWindow();

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == newGame)
                GameSetup.play(stage);

            else if (result.get() == mainMenu)
                GameSetup.mainMenu(stage);
        }
    }
}
