package pl.edu.pw.eiti.wsd.printerweb.user.gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.DocumentStatus;
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

    @FXML
    private TableView<DocumentInfo> scheduledDocumentsView;

    @FXML
    private TableColumn<DocumentInfo, String> nameColumn;

    @FXML
    private TableColumn<DocumentInfo, String> statusColumn;

    @FXML
    private TextField fileNameField;

    @FXML
    private Label userStatusLabel;

    private FileChooser fileChooser = new FileChooser();

    private File choosenFile = null;

    private final UserAgent userAgent;

    private final Map<String, DocumentInfo> scheduledDocuments = new HashMap<>();

    private SimpleStringProperty statusProperty = new SimpleStringProperty("Rozłączony");

    public UserController(UserAgent userAgent) {
        super();
        this.userAgent = userAgent;
    }

    public void show() {
        Platform.runLater(new GuiInitiator(this, userAgent));
    }

    @Override
    public void start(Stage stage) throws IOException {

    }

    public void initialize() {
        typeOfPrinterChoose.setItems(FXCollections.observableList(Arrays.asList(PrinterType.values())));
        typeOfPrinterChoose.setValue(PrinterType.values()[0]);

        paperFormatChoose.setItems(FXCollections.observableList(Arrays.asList(PaperFormat.values())));
        paperFormatChoose.setValue(PaperFormat.values()[0]);

        userStatusLabel.textProperty().bind(statusProperty);

        nameColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DocumentInfo, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentInfo, String> param) {
                        return param.getValue().nameProperty();
                    }
                });

        statusColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DocumentInfo, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentInfo, String> param) {
                        return param.getValue().statusProperty();
                    }
                });

        scheduledDocumentsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        scheduledDocumentsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DocumentInfo>() {

            @Override
            public void changed(ObservableValue<? extends DocumentInfo> observable, DocumentInfo oldValue,
                    DocumentInfo newValue) {
                returnInfoListView.setItems(scheduledDocuments.get(newValue.getDocId()).detailsProperty());
            }
        });
    }

    @FXML
    private void onLoadDocument(ActionEvent event) {
        List<File> list = fileChooser.showOpenMultipleDialog(((Node) event.getTarget()).getScene().getWindow());
        if (list != null) {
            for (File file : list) {
                choosenFile = file;
                fileNameField.setText(file.getName());
            }
        }
    }

    @FXML
    private void onPrintDocument(MouseEvent event) {
        if (choosenFile == null) {
            return;
        }

        Document document = new DocumentImpl(paperFormatChoose.getValue(), choosenFile);
        String docId = userAgent.printDocument(document);
        DocumentInfo docInfo = new DocumentInfo(docId, document);

        scheduledDocuments.put(docId, docInfo);
        scheduledDocumentsView.getItems().add(docInfo);

        choosenFile = null;
        fileNameField.setText("");
    }

    public void addDocumentStatusInfo(String docId, DocumentStatus status, String detail) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                DocumentInfo info = scheduledDocuments.get(docId);
                info.setStatus(status);
                info.addDetail(detail);
            }
        });
    }

    public void addStatusInfo(String status) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                statusProperty.set(status);
            }

        });
    }

    public void dispose() {
    }

    private static final class GuiInitiator implements Runnable {

        private UserController userController;

        private UserAgent userAgent;

        public GuiInitiator(UserController userController, UserAgent userAgent) {
            this.userController = userController;
            this.userAgent = userAgent;
        }

        @Override
        public void run() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_view.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            loader.setController(userController);
            try {
                stage.setScene(new Scene((Pane) loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.setTitle(userAgent.getName());
            stage.show();
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

    private static class DocumentInfo {

        private final String docId;

        private StringProperty nameProperty;

        private StringProperty statusProperty;

        private ObservableList<String> details;

        public DocumentInfo(String docId, Document document) {
            this.docId = docId;
            setName(document.getFile().getName());
            setStatus(DocumentStatus.LOADED);
        }

        public String getDocId() {
            return docId;
        }

        public String getName() {
            return nameProperty().get();
        }

        public void setName(String name) {
            nameProperty().set(name);
        }

        public StringProperty nameProperty() {
            if (nameProperty == null) {
                nameProperty = new SimpleStringProperty(this, "name");
            }
            return nameProperty;
        }

        public void setStatus(DocumentStatus status) {
            statusProperty().set(status.toString());
        }

        public String getStatus() {
            return statusProperty().get();
        }

        public StringProperty statusProperty() {
            if (statusProperty == null) {
                statusProperty = new SimpleStringProperty(this, "status");
            }
            return statusProperty;
        }

        public void addDetail(String detail) {
            detailsProperty().add(detail);
        }

        public ObservableList<String> detailsProperty() {
            if (details == null) {
                details = FXCollections.observableArrayList();
            }

            return details;
        }
    }
}
