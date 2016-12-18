package pl.edu.pw.eiti.wsd.printerweb;

import java.util.ArrayList;
import java.util.List;

import jade.Boot;
import javafx.application.Application;
import javafx.stage.Stage;
import pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent;
import pl.edu.pw.eiti.wsd.printerweb.user.UserAgent;

public class PrinterWebApplication {

    public static void main(String[] args) {
        new Thread(() -> MainApp.main(null)).start();

        List<String> jadeArgs = new ArrayList<>(7);
        jadeArgs.add("-gui");
        
        String agents = "userAgent:" + UserAgent.class.getName(); 
        for(int i = 0; i < 5; ++i) {
            agents += ";negotiator" + i + ":" + PrinterAgent.class.getName();
        }
        jadeArgs.add(agents);
        
        Boot.main(jadeArgs.toArray(new String[jadeArgs.size()]));
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
