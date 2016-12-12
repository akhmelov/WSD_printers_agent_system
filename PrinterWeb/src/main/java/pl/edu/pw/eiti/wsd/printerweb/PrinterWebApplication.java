package pl.edu.pw.eiti.wsd.printerweb;

import jade.Boot;
import javafx.application.Application;
import javafx.stage.Stage;
import pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent;
import pl.edu.pw.eiti.wsd.printerweb.user.UserAgent;

public class PrinterWebApplication {

    public static void main(String[] args) {
        new Thread(() -> MainApp.main(null)).start();

        String[] jadeArgs = new String[] { "-gui", "userAgent:" + UserAgent.class.getName() + ";printManager:"
                + PrinterAgent.class.getName() + ";negotiator:" + PrinterAgent.class.getName() };
        Boot.main(jadeArgs);
    }

    public static class MainApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}
