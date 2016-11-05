package wsd.printers.agent.springfx.gui;

import com.esotericsoftware.yamlbeans.YamlException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.springfx.model.AgentConfModel;
import wsd.printers.agent.springfx.service.SpecifyAgentService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by akhmelov on 10/21/16.
 */
public class SpecifyAgentPresentation extends Presentation {

    Logger logger = Logger.getLogger(SpecifyAgentPresentation.class);

    @Autowired
    private SpecifyAgentService specifyAgentService;

    final FileChooser fileChooser = new FileChooser();


    public SpecifyAgentPresentation(ScreensConfig config) {
        super(config);
    }

    @FXML
    private Button nextViewButton;

    @FXML
    void nextView(ActionEvent event) {
        config.loadDocumentManage();
    }

    @FXML
    void configChooseButtonClick(ActionEvent event){
        List<File> list =
                fileChooser.showOpenMultipleDialog(config.getStage());
        if (list != null) {
            for (File file : list) {
                try {
                    AgentConfModel agentConfModel = specifyAgentService.loadAgent(file.getAbsolutePath());
                    nextViewButton.setDisable(false);

                } catch (FileNotFoundException | YamlException e) {
                    logger.error(e);
                }
            }
        }
    }
}
