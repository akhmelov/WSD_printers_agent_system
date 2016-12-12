package wsd.printers.agent.springfx.config;

import jade.core.Agent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import wsd.printers.agent.springfx.BuyerAgent;
import wsd.printers.agent.springfx.gui.DocumentManagePresentation;
import wsd.printers.agent.springfx.gui.ScreensConfig;
import wsd.printers.agent.springfx.service.AgentBuyerService;


@Configuration
//@Import(ScreensConfig.class)
@ComponentScan("wsd.printers.agent.springfx")
public class AppConfig {

    private Logger logger = Logger.getLogger(AppConfig.class);

    //beans
    @Bean
    ScreensConfig screensConfig(){
        return new ScreensConfig();
    }

    @Bean
    @Autowired
    DocumentManagePresentation documentManagePresentation(ScreensConfig screensConfig) {
        return new DocumentManagePresentation(screensConfig);
    }

    @Bean
    BuyerAgent getAgentBuyer(){
        BuyerAgent instance = BuyerAgent.getInstance();
        while (instance == null){
            logger.info("Waiting for agent instance");
            try {
                Thread.sleep(1000);
                instance = BuyerAgent.getInstance();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        return instance;
    }
}
