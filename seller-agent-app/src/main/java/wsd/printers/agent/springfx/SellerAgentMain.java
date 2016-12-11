package wsd.printers.agent.springfx;

import jade.core.Agent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wsd.printers.agent.springfx.config.AppConfig;

/**
 * Created by akhmelov on 12/11/16.
 */
public class SellerAgentMain extends Agent {

    private static final Logger logger = LogManager.getLogger(SellerAgentMain.class);

    @Override
    public void setup(){;
        logger.error("=======================================================================================================================================================Start command");
        final String otherAgentName = (String) this.getArguments()[0];
        logger.info("Agent is starting ---------------------------------------------------------------------");
        Thread thread = new Thread(() -> Main.launch(Main.class));
        thread.setDaemon(true);
        thread.start();
        logger.error("====================================================================================================================================================xy=Ended command");
    }
}
