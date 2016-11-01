package wsd.printers.agent.springfx.service;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.model.AgentConfModel;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by akhmelov on 11/1/16.
 */
@Service
public class SpecifyAgentService {

    Logger logger = Logger.getLogger(SpecifyAgentService.class);

    public AgentConfModel loadAgent(String filename) throws FileNotFoundException, YamlException {
        YamlReader yamlReader = new YamlReader(new FileReader(filename));
        AgentConfModel agentConfModel = yamlReader.read(AgentConfModel.class);
        logger.debug("Parsered '" + filename + "' to: " + agentConfModel);
        return agentConfModel;
    }
}
