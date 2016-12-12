package wsd.printers.agent.userAgent.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import wsd.printers.agent.userAgent.BuyerAgent;
import wsd.printers.agent.userAgent.gui.DocumentManagePresentation;
import wsd.printers.agent.userAgent.gui.ScreensConfig;


@Configuration
//@Import(ScreensConfig.class)
@ComponentScan("wsd.printers.agent.userAgent")
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
