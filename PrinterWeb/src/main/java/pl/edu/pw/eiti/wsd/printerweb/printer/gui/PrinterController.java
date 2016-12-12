package pl.edu.pw.eiti.wsd.printerweb.printer.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;

public class PrinterController extends Application {

    @FXML
    private Button pauseButton;

    @FXML
    private Button noPaperButton;

    @FXML
    private Button noInkButton;

    private final PrinterDriver printer;

    public PrinterController(PrinterDriver printer) {
        super();
        this.printer = printer;
    }

    public void show() {
        Platform.runLater(new GuiInitiator(this, printer.toString()));
    }

    @Override
    public void start(Stage stage) throws IOException {
    }

    @FXML
    public void pauseButtonAction(ActionEvent event) {
        Effect effect = pauseButton.getEffect();
        if (effect == null) {
            printer.setCrashed(true);
            pauseButton.setEffect(new ColorAdjust(1, 0, 0, 0));
        } else {
            printer.setCrashed(false);
            pauseButton.setEffect(null);
        }
    }

    @FXML
    public void noPaperButtonAction(ActionEvent event) {
        Effect effect = noPaperButton.getEffect();
        if (effect == null) {
            printer.setNoPaper(true);
            noPaperButton.setEffect(new ColorAdjust(1, 0, 0, 0));
        } else {
            printer.setNoPaper(false);
            noPaperButton.setEffect(null);
        }
    }

    @FXML
    public void noInkButtonAction(ActionEvent event) {
        Effect effect = noInkButton.getEffect();
        if (effect == null) {
            printer.setNoInk(true);
            noInkButton.setEffect(new ColorAdjust(1, 0, 0, 0));
        } else {
            printer.setNoInk(false);
            noInkButton.setEffect(null);
        }
    }

    private static final class GuiInitiator implements Runnable {

        private final PrinterController printerController;

        private final String name;

        public GuiInitiator(PrinterController printerController, String name) {
            this.printerController = printerController;
            this.name = name;
        }

        @Override
        public void run() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("printer_view.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            loader.setController(printerController);
            try {
                stage.setScene(new Scene((Pane) loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.setTitle(name);
            stage.show();
        }
    }
}
