package wsd.printers.agent.userAgent.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.userAgent.enums.PaperFormatEnum;
import wsd.printers.agent.userAgent.enums.PrinterTypeEnum;
import wsd.printers.agent.userAgent.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.userAgent.service.DocumentManageService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by akhmelov on 10/22/16.
 */
public class DocumentManagePresentation extends Presentation {

    Logger logger = Logger.getLogger(DocumentManagePresentation.class);

    @Autowired
    private DocumentManageService documentManageService;

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
                try {
                    documentManageService.sayAgentAboutDocument(file, typeOfPrinterChoose.getValue(), paperFormatChoose.getValue());
                } catch (IOException | UnsupportedParametersPresentException e) {
                    addEventInfo("Plik jest niepoprawny " + e.getClass());
                }
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
