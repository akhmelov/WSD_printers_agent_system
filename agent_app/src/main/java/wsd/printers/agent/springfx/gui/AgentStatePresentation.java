package wsd.printers.agent.springfx.gui;

import com.esotericsoftware.yamlbeans.YamlException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.springfx.service.AgentStateService;
import wsd.printers.agent.springfx.service.PrinterService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by akhmelov on 11/5/16.
 */
public class AgentStatePresentation extends Presentation {

    Logger logger = Logger.getLogger(AgentStatePresentation.class);

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

    public AgentStatePresentation(ScreensConfig config) {
        super(config);
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
    void chooseAgentButtonAction(ActionEvent event){
        config.loadSpecifyAgent();
    }

    @FXML
    void documentManageButtonClick(ActionEvent event){
        config.loadDocumentManage();
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
