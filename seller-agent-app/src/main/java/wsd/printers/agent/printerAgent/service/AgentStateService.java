package wsd.printers.agent.printerAgent.service;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.printerAgent.enums.PaperFormatEnum;
import wsd.printers.agent.printerAgent.enums.PrinterTypeEnum;
import wsd.printers.agent.printerAgent.model.DocumentModelTest;
import wsd.printers.agent.printerAgent.model.TestScenarioPrints;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

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
        for (HashMap hashMap: testScenarioPrints.getDocumentModelTests()){
            DocumentModelTest documentModelTest
                    = new DocumentModelTest(Integer.valueOf((String) hashMap.get("pagesNumber")),
                    PrinterTypeEnum.valueOf((String) hashMap.get("printerTypeEnum")),
                    PaperFormatEnum.valueOf((String) hashMap.get("paperFormatEnum")));
            printerService.addToQueue(documentModelTest);
        }
    }
}
