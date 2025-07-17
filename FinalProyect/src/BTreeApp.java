import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BTreeApp extends Application {
    private BTree tree = new BTree(3);
    private Canvas canvas = new Canvas(800, 600);

    @Override
    public void start(Stage primaryStage) {
        BorderPane rootPane = new BorderPane();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawTree(gc);

        TextField inputField = new TextField();
        inputField.setPromptText("Ingrese clave");

        Button insertButton = new Button("Insertar");
        insertButton.setOnAction(e -> {
            try {
                int key = Integer.parseInt(inputField.getText());
                tree.insert(key);
                inputField.clear();
                drawTree(gc);
            } catch (NumberFormatException ex) {
                inputField.setText("Número inválido");
            }
        });

        Button clearButton = new Button("Limpiar");
        clearButton.setOnAction(e -> {
            tree = new BTree(3);
            drawTree(gc);
        });

        HBox controls = new HBox(10, inputField, insertButton, clearButton);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 10px;");

        rootPane.setTop(controls);
        rootPane.setCenter(canvas);

        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Árbol B Visual");
        primaryStage.show();
    }

    private void drawTree(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (tree.getRoot() != null) {
            drawNode(gc, tree.getRoot(), canvas.getWidth() / 2, 30, canvas.getWidth() / 4);
        }
    }

    private void drawNode(GraphicsContext gc, BTreeNode node, double x, double y, double offsetX) {
        int totalKeys = node.n;
        double nodeWidth = totalKeys * 30 + (totalKeys - 1) * 5;

        double startX = x - nodeWidth / 2;

        for (int i = 0; i < totalKeys; i++) {
            double keyX = startX + i * 35;
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(keyX, y, 30, 30);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(keyX, y, 30, 30);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(14));
            gc.fillText(Integer.toString(node.keys[i]), keyX + 10, y + 20);
        }

        if (!node.leaf) {
            for (int i = 0; i <= node.n; i++) {
                double childX = x - offsetX + i * (2 * offsetX / (node.n));
                double childY = y + 70;
                gc.setStroke(Color.BLACK);
                gc.strokeLine(x, y + 30, childX, childY);
                drawNode(gc, node.children[i], childX, childY, offsetX / 2);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
