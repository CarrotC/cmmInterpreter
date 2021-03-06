import interpreter.Parser;
import interpreter.Sematics;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import interpreter.Lexer;
import util.impl.FileHandlerImpl;
import vo.Token;
import vo.TreeNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    final TextField filePathTF = new TextField();

    @Override
    public void start(Stage primaryStage) throws Exception {
        filePathTF.setPrefColumnCount(30);

        final FileChooser fileChooser = new FileChooser();
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


        final TextArea resultText = new TextArea();

        Button lexerBtn = new Button("词法分析");
        lexerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!hasSelectFile()){
                    return;
                }
                FileHandlerImpl fileHandler = new FileHandlerImpl();
                try {
                    String source = fileHandler.FileToString(filePathTF.getText());
                    Lexer lexer = new Lexer(source);
                    String lexerResult = lexer.lexicalAnalysis();
                    resultText.setText(lexerResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button parseBtn = new Button("语法分析");
        parseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!hasSelectFile()){
                    return;
                }
                FileHandlerImpl fileHandler = new FileHandlerImpl();
                try {
                    String source = fileHandler.FileToString(filePathTF.getText());
                    Lexer lexer = new Lexer(source);
                    String result = lexer.lexicalAnalysis();
                    Parser parser = new Parser(lexer.getTokenList());
                    String parserResult = parser.grammaticalAnalysis();
//                    System.out.print(parserResult);

                    resultText.setText(parserResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button sematicBtn = new Button("语义分析");
        sematicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!hasSelectFile()){
                    return;
                }
                FileHandlerImpl fileHandler = new FileHandlerImpl();
                try {
                    String source = fileHandler.FileToString(filePathTF.getText());
                    Lexer lexer = new Lexer(source);
                    String result = lexer.lexicalAnalysis();
                    Parser parser = new Parser(lexer.getTokenList());
                    String parserResult = parser.grammaticalAnalysis();
                    if (parser.getErrorInfo() != ""){
                        resultText.setText("程序存在语法错误！请先排除！\n" + parser.getErrorInfo());
                        return;
                    }
//                    System.out.print(parserResult);
                    Sematics sematics = new Sematics(parser.getProgramNode());
                    String sematicResult = sematics.sematicAnalyse();
                    resultText.setText(sematicResult);

                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public boolean hasSelectFile(){
        if(filePathTF.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("请先选择文件");
            alert.showAndWait();
            return false;
        }else {
            return true;
        }
    }

    public static void main(String[] args){
        //GUI程序必须从入口的main方法进入并使用
        //launch是Application中的，调用它则可以启动GUI程序
        launch(args);
    }
}
