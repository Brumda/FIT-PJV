package thedrake.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import thedrake.code.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameSetup {
    public static void mainMenu(Stage stage) throws Exception {
        Scene mainMenu = new Scene(
                FXMLLoader.load(Objects.requireNonNull(GameSetup.class.getResource("/thedrake/ui/thedrake.fxml"))));
        stage.setTitle("The Drake");
        stage.setScene(mainMenu);
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.show();
        Button endButton = (Button) mainMenu.lookup("#endButton");
        endButton.setOnAction(e -> {Platform.exit();});
        Button playButton = (Button) mainMenu.lookup("#playButton");
        playButton.setOnAction(e -> {play(stage);});
    }

    public static void play(Stage stage) {
        BorderPane game = new Game(newGame());
        Scene scene = new Scene(game);
        scene.getStylesheets().addAll((GameSetup.class.getResource("/thedrake/css/Style.css")).toExternalForm());
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.setScene(scene);
        stage.show();
    }

    private static GameState newGame() {
        int dimension = 4;
        int numberOfMountains = dimension / 2;
        Board board = new Board(dimension);
        PositionFactory positionFactory = board.positionFactory();

        Set<BoardPos> mountainPositions = new HashSet<>();
        Random random = new Random();

        while (mountainPositions.size() < numberOfMountains) {
            int row = random.nextInt(dimension);
            int col = random.nextInt(dimension);
            mountainPositions.add(new BoardPos(dimension, row, col));
        }

        for (BoardPos pos2 : mountainPositions)
            board = board.withTiles(new Board.TileAt(positionFactory.pos(pos2.i(), pos2.j()), BoardTile.MOUNTAIN));

        return new StandardDrakeSetup().startState(board);
    }
}
