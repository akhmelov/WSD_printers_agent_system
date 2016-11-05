package wsd.printers.agent.springfx.gui;

import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by akhmelov on 10/22/16.
 */
public class DocumentManagePresentation extends Presentation {

    Logger logger = Logger.getLogger(DocumentManagePresentation.class);

    @FXML
    private ChoiceBox<PrinterTypeEnum> typeOfPrinterChoose;

    @FXML
    private ChoiceBox<PaperFormatEnum> paperFormatChoose;

    @FXML
    private ListView<String> returnInfoListView;

    final private FileChooser fileChooser = new FileChooser();

    public DocumentManagePresentation(ScreensConfig config) {
        super(config);
    }

    public void initialize(){
        typeOfPrinterChoose.setItems(FXCollections.observableList(Arrays.asList(PrinterTypeEnum.values())));
        typeOfPrinterChoose.setValue(PrinterTypeEnum.values()[0]);

        paperFormatChoose.setItems(FXCollections.observableList(Arrays.asList(PaperFormatEnum.values())));
        paperFormatChoose.setValue(PaperFormatEnum.values()[0]);
    }

    @FXML
    void loadDocumentButtonAction(ActionEvent event){
        List<File> list =
                fileChooser.showOpenMultipleDialog(config.getStage());
        resetEventInfo();
        if (list != null) {
            for (File file : list) {
                addEventInfo("Loaded file '" + file.getName() + "'");
            }
        }
    }

    public void addEventInfo(String event){
        ObservableList<String> items = returnInfoListView.getItems();
        ObservableList<String> strings = FXCollections.observableArrayList(event);
        strings.addAll(items);
        returnInfoListView.setItems(strings);
    }

    public void resetEventInfo(){
        returnInfoListView.setItems(FXCollections.observableArrayList());
    }
}
