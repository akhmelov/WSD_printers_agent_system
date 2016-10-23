package wsd.printers.agent.springfx.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import wsd.printers.agent.springfx.control.LanguageController;
import wsd.printers.agent.springfx.model.LanguageModel;
import wsd.printers.agent.springfx.model.MessageModel;

/**
 * Created by akhmelov on 10/21/16.
 */
public class SpecifyAgentPresentation extends Presentation {

    @Autowired
    private MessageModel model;

    public SpecifyAgentPresentation(ScreensConfig config) {
        super(config);
    }

    @FXML
    ChoiceBox agentsList;

    @FXML
    RadioButton engRadio, romRadio;

    @FXML
    ToggleGroup langGroup;

    @Autowired
    private LanguageController langCtr;

    @FXML
    void nextView(ActionEvent event) {
        config.loadSecond();
    }

    @FXML
    void initialize() {
        //set language
        if (LanguageModel.Language.RO.equals(langCtr.getLanguage())) {
            engRadio.setSelected(false);
            romRadio.setSelected(true);
        }
        langGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                changeLanguage();
            }
        });

        //set availible agent list
        agentsList.setItems(FXCollections.observableArrayList("First", "Second", "Third"));
    }

    private void changeLanguage() {
        if (engRadio.isSelected())
            langCtr.toEnglish();
        else
            langCtr.toRomanian();
    }
}
