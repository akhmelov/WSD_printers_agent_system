package wsd.printers.agent.springfx.service;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.model.DocumentModelTest;
import wsd.printers.agent.springfx.model.TestScenarioPrints;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by akhmelov on 11/5/16.
 */
@Service
public class AgentStateService {

    @Autowired
    private PrinterService printerService;

    public void loadScenario(String filename) throws FileNotFoundException, YamlException {
        YamlReader yamlReader = new YamlReader(new FileReader(filename));
        TestScenarioPrints testScenarioPrints = yamlReader.read(TestScenarioPrints.class);
        for (DocumentModelTest documentModelTest: testScenarioPrints.getDocumentModelTests()){
//            printerService.addToQueue(documentModelTest);
        }
    }
}
