package wsd.printers.agent.springfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.config.AppConfig;
import wsd.printers.agent.springfx.gui.ScreensConfig;

@Service
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Starting application");

        Platform.setImplicitExit(true);

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScreensConfig screens = context.getBean(ScreensConfig.class);

        screens.setPrimaryStage(stage);
        screens.showMainScreen();
        screens.loadSpecifyAgent();
    }
}