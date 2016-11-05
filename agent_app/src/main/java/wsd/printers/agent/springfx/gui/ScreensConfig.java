package wsd.printers.agent.springfx.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
public class ScreensConfig {
    private static final Logger logger = LogManager.getLogger(ScreensConfig.class);

    public static final int WIDTH = 680;
    public static final int HEIGHT = 420;
    public static final String STYLE_FILE = "main.css";

    private Stage stage;
    private Scene scene;
    private StackPane root;

    @Autowired
    private SpecifyAgentPresentation specifyAgentPresentation;

    @Autowired
    private DocumentManagePresentation documentManagePresentation;

    @Autowired
    private AgentStatePresentation agentStatePresentation;


    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }



    public void showMainScreen() {
        root = new StackPane();
        root.getStylesheets().add(STYLE_FILE);
        root.getStyleClass().add("main-window");
        stage.setTitle("SpringFX");
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnHiding(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.exit(0);
                // TODO you could add code to open an "are you sure you want to exit?" dialog
            }
        });

        stage.show();
    }

    private void setNode(Node node) {
        root.getChildren().setAll(node);
    }

    private void setNodeOnTop(Node node) {
        root.getChildren().add(node);
    }

    public void removeNode(Node node) {
        root.getChildren().remove(node);
    }

    public void loadSpecifyAgent() {
        setNode(getNode(specifyAgentPresentation, getClass().getResource("SpecifyAgent.fxml")));
    }

    public void loadDocumentManage() {
        setNode(getNode(documentManagePresentation, getClass().getResource("DocumentManage.fxml")));
    }

    public void loadAgentState(){
        setNode(getNode(agentStatePresentation, getClass().getResource("AgentState.fxml")));
    }

    private Node getNode(final Presentation control, URL location) {
        FXMLLoader loader = new FXMLLoader(location, ResourceBundle.getBundle("lang", new Locale("en")));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            public Object call(Class<?> aClass) {
                return control;
            }
        });

        try {
            return (Node) loader.load();
        } catch (Exception e) {
            logger.error("Error casting node", e);
            return null;
        }
    }

    public Stage getStage() {
        return stage;
    }
}
