package wsd.printers.agent.springfx.gui;

import com.esotericsoftware.yamlbeans.YamlException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by akhmelov on 10/22/16.
 */
public class DocumentManagePresentation extends Presentation {

    Logger logger = Logger.getLogger(DocumentManagePresentation.class);

    final FileChooser fileChooser = new FileChooser();

    public DocumentManagePresentation(ScreensConfig config) {
        super(config);
    }

    @FXML
    void loadDocumentButtonAction(ActionEvent event){
        List<File> list =
                fileChooser.showOpenMultipleDialog(config.getStage());
        if (list != null) {
            for (File file : list) {

            }
        }
    }
}
