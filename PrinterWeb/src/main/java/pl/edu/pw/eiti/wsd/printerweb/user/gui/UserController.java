package pl.edu.pw.eiti.wsd.printerweb.user.gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.PaperFormat;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo.PrinterType;
import pl.edu.pw.eiti.wsd.printerweb.user.UserAgent;

public class UserController extends Application {

    @FXML
    private ChoiceBox<PrinterType> typeOfPrinterChoose;

    @FXML
    private ChoiceBox<PaperFormat> paperFormatChoose;

    @FXML
    private ListView<String> returnInfoListView;

    private FileChooser fileChooser = new FileChooser();

    private final UserAgent userAgent;

    public UserController(UserAgent userAgent) {
        super();
        this.userAgent = userAgent;
    }

    public void show() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user_view.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                loader.setController(UserController.this);
                try {
                    stage.setScene(new Scene((Pane) loader.load()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                stage.setTitle("FXML Welcome");
                stage.show();
            }
        });
    }

    @Override
    public void start(Stage stage) throws IOException {

    }

    public void initialize() {
        typeOfPrinterChoose.setItems(FXCollections.observableList(Arrays.asList(PrinterType.values())));
        typeOfPrinterChoose.setValue(PrinterType.values()[0]);

        paperFormatChoose.setItems(FXCollections.observableList(Arrays.asList(PaperFormat.values())));
        paperFormatChoose.setValue(PaperFormat.values()[0]);
    }

    @FXML
    private void onLoadDocument(ActionEvent event) {
        List<File> list = fileChooser.showOpenMultipleDialog(((Node) event.getTarget()).getScene().getWindow());
        if (list != null) {
            for (File file : list) {
                try {
                    userAgent.schedulePrinting(new DocumentImpl(paperFormatChoose.getValue(), file));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static class DocumentImpl implements Document {

        private static final long serialVersionUID = 8066440564891031262L;

        private PaperFormat format;

        private File file;

        public DocumentImpl(PaperFormat format, File file) {
            this.format = format;
            this.file = file;
        }

        @Override
        public PaperFormat getPaperFormat() {
            return format;
        }

        @Override
        public File getFile() {
            return file;
        }

        @Override
        public int getNumberOfPages() {
            return 50;
        }
    }
}
