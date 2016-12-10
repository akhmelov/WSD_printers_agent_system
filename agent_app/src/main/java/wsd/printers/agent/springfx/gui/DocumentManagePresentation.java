package wsd.printers.agent.springfx.gui;

import com.esotericsoftware.yamlbeans.YamlException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;
import wsd.printers.agent.springfx.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.springfx.service.AgentPrinter;
import wsd.printers.agent.springfx.service.AgentStateService;
import wsd.printers.agent.springfx.service.DocumentManageService;
import wsd.printers.agent.springfx.service.PrinterService;

import java.io.File;
import java.io.FileNotFoundException;
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

    @Autowired
    private AgentStateService agentStateService;

    @Autowired
    private PrinterService printService;

    @FXML
    private Button pauseButton;

    @FXML
    private Button noPaperButton;

    @FXML
    private Button noInkButton;

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

    @FXML
    void chooseAgentButtonAction(ActionEvent event){
        config.loadSpecifyAgent();
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

    @FXML
    public void pauseButtonAction(ActionEvent event){
        Effect effect = pauseButton.getEffect();
        if(effect == null) {
            try {
                printService.pausePrinting();
                pauseButton.setEffect(new ColorAdjust(1, 0, 0, 0));
            } catch (InterruptedException e) {
                logger.error(e);
            }
        } else {
            printService.resumePrinting();
            pauseButton.setEffect(null);
        }
    }

    @FXML
    public void noPaperButtonAction(ActionEvent event){
        Effect effect = noPaperButton.getEffect();
        if(effect == null) {
            printService.setPaperPresent(false);
            noPaperButton.setEffect(new ColorAdjust(1, 0, 0, 0));
        } else {
            printService.setPaperPresent(true);
            noPaperButton.setEffect(null);
        }
    }

    @FXML
    public void noInkButtonAction(ActionEvent event){
        Effect effect = noInkButton.getEffect();
        if(effect == null) {
            printService.setInkPresent(false);
            noInkButton.setEffect(new ColorAdjust(1, 0, 0, 0));
        } else {
            printService.setInkPresent(true);
            noInkButton.setEffect(null);
        }
    }

    @FXML
    void loadScenarioButtonAction(ActionEvent event){
        List<File> list =
                fileChooser.showOpenMultipleDialog(config.getStage());
        if (list != null) {
            for (File file : list) {
                try {
                    agentStateService.loadScenario(file.getAbsolutePath());
                } catch (FileNotFoundException | YamlException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
