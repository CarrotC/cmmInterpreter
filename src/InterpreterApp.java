import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import interpreter.Lexer;
import util.impl.FileHandlerImpl;

import java.io.File;
import java.io.IOException;

public class InterpreterApp extends Application {
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField filePathTF = new TextField();
        filePathTF.setPrefColumnCount(30);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择cmm程序文件");
        File directory = new File(System.getProperty("user.dir") );//当前文件
        fileChooser.setInitialDirectory(directory);

        Button selectFileBtn = new Button("点击选择文件");
        selectFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(new Stage());
                String filePath = file.getPath();
                filePathTF.setText(filePath);
            }
        });



        Rectangle block = new Rectangle(500, 720);
        block.setFill(Color.WHITE);
        block.setArcHeight(10);
        block.setArcWidth(10);

        Label resultText = new Label();

        Button lexerBtn = new Button("词法分析");
        lexerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(filePathTF.getText().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText(null);
                    alert.setContentText("请先选择文件");
                    alert.showAndWait();
                    return;
                }
                FileHandlerImpl fileHandler = new FileHandlerImpl();
                try {
                    String source = fileHandler.FileToString(filePathTF.getText());
                    Lexer lexer = new Lexer(source);
                    String result = lexer.lexicalAnalysis();
                    resultText.setText(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button parseBtn = new Button("语法分析");
        parseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("功能尚在开发中……");
                alert.showAndWait();
            }
        });
        Button sematicBtn = new Button("语义分析");
        sematicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("功能尚在开发中……");
                alert.showAndWait();
            }
        });

        StackPane sp = new StackPane();
        sp.getChildren().addAll(block, resultText);

        HBox buttonH = new HBox();
        buttonH.setSpacing(20);
        buttonH.getChildren().addAll(lexerBtn, parseBtn, sematicBtn);

        //创建面板，面板包含控件
        GridPane gridPane = new GridPane();
        gridPane.setMinHeight(780);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.add(selectFileBtn, 1, 0);
        gridPane.add(filePathTF, 0, 0);
        gridPane.add(buttonH, 0,1);
        gridPane.add(sp, 0, 2);


        //创建场景，场景包含面板
        Scene scene = new Scene(gridPane);

        //将场景放入舞台中
        primaryStage.setScene(scene);
        primaryStage.setTitle("cmm语言解释器");
        primaryStage.show();


    }

    public static void main(String[] args){
        //GUI程序必须从入口的main方法进入并使用
        //launch是Application中的，调用它则可以启动GUI程序
        launch(args);
    }
}
