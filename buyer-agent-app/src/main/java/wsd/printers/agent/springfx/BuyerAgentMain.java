package wsd.printers.agent.springfx;

import jade.core.Agent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by akhmelov on 12/11/16.
 */
public class BuyerAgentMain extends Agent {

    private static final Logger logger = LogManager.getLogger(BuyerAgentMain.class);

    @Override
    public void setup(){;
        logger.info("=======================================================================================================================================================Start command");
        final String otherAgentName = (String) this.getArguments()[0];
        logger.info("Agent is starting ---------------------------------------------------------------------");
        Thread thread = new Thread(() -> Main.launch(Main.class));
        thread.setDaemon(true);
        thread.start();
        logger.info("====================================================================================================================================================xy=Ended command");

    }
}
