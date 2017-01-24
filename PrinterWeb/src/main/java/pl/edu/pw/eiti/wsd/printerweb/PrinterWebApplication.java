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
//            for(int i = 0; i < 2; ++i) {
            agents += ";negotiator" + 0 + ":" + PrinterAgent.class.getName() + "(paper:A1-A5-A7,location:2-3-5,type:BLACK,double:yes)";
            agents += ";negotiator" + 1 + ":" + PrinterAgent.class.getName() + "(paper:A1-A5-A7,location:4-5-5,type:COLOR,double:yes)";
            agents += ";negotiator" + 2 + ":" + PrinterAgent.class.getName() + "(paper:A1-A5-A7,location:8-7-5,type:COLOR,double:yes)";
//        }
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
