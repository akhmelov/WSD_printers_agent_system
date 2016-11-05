package wsd.printers.agent.springfx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.springfx.service.AgentStateService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by akhmelov on 11/5/16.
 */
public class AgentStatePresentation extends Presentation {

    Logger logger = Logger.getLogger(AgentStatePresentation.class);

    @Autowired
    private AgentStateService agentStateService;

    final private FileChooser fileChooser = new FileChooser();

    public AgentStatePresentation(ScreensConfig config) {
        super(config);
    }

    @FXML
    void chooseAgentButtonAction(ActionEvent event){
        config.loadSpecifyAgent();
    }

    @FXML
    private void documentManageButtonClick(ActionEvent event){
        config.loadDocumentManage();
    }

    @FXML
    private void loadScenarioButtonAction(ActionEvent event){
        List<File> list =
                fileChooser.showOpenMultipleDialog(config.getStage());
        if (list != null) {
            for (File file : list) {
            }
        }
    }
}
