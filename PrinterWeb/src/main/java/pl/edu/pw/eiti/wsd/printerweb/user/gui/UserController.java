package pl.edu.pw.eiti.wsd.printerweb.user.gui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;
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
    private TextField minResolution;

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

    @FXML
    private DatePicker preferredDate;

    @FXML
    private CheckBox doubleSided;

    @FXML
    private TextField numberOfCopies;

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

        UnaryOperator<Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        numberOfCopies.setTextFormatter(new TextFormatter<>(filter));
        minResolution.setTextFormatter(new TextFormatter<>(filter));
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

        Integer numbersCopy;
        Integer resolution;
        try {
            numbersCopy = Integer.valueOf(numberOfCopies.getText());
            resolution = Integer.valueOf(minResolution.getText());

            if (resolution <= 0) {
                resolution = 1;
            }
        } catch (NumberFormatException e) {
            numbersCopy = 1;
            resolution = 1;
        }

        if (numbersCopy > 0) {
            Document document = new DocumentImpl(typeOfPrinterChoose.getValue(), paperFormatChoose.getValue(), choosenFile,
                    numbersCopy, preferredDate.getValue(), doubleSided.isSelected(), resolution, userAgent.getCurrentLocation());
            String docId = userAgent.printDocument(document);
            DocumentInfo docInfo = new DocumentInfo(docId, document);

            scheduledDocuments.put(docId, docInfo);
            scheduledDocumentsView.getItems().add(docInfo);

            choosenFile = null;
            fileNameField.setText("");
        }
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

        private final PaperFormat format;

        private final File file;

        private final PrinterType printerType;

        private final int pages = (int) (System.currentTimeMillis() % 16 + 8);

        private final int numberOfCopies;

        private final LocalDate preferredDate;

        private final boolean doubleSided;

        private final int minResolution;

        private final Location location;

        public DocumentImpl(PrinterType printerType, PaperFormat format, File file, int numberOfCopies, LocalDate preferredDate,
                boolean doubleSided, Integer minResolution, Location location) {
            this.printerType = printerType;
            this.format = format;
            this.file = file;
            this.numberOfCopies = numberOfCopies;
            this.preferredDate = preferredDate;
            this.doubleSided = doubleSided;
            this.minResolution = minResolution;
            this.location = location;
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
            return pages;
        }

        @Override
        public PrinterType getPrinterType() {
            return printerType;
        }

        @Override
        public int getNumberOfCopies() {
            return numberOfCopies;
        }

        @Override
        public LocalDate getPreferredDate() {
            return preferredDate;
        }

        @Override
        public boolean isDoubleSided() {
            return doubleSided;
        }

        @Override
        public int getMinResolution() {
            return minResolution;
        }

        @Override
        public Location getSourceLocation() {
            return location;
        }
    }

    private static class DocumentInfo {

        private final String docId;

        private StringProperty nameProperty;

        private StringProperty statusProperty;

        private ObservableList<String> details;

        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

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
            detailsProperty().add(dateFormatter.format(new Date()) + ": " + detail);
        }

        public ObservableList<String> detailsProperty() {
            if (details == null) {
                details = FXCollections.observableArrayList();
            }

            return details;
        }
    }
}
