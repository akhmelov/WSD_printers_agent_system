package wsd.printers.agent.springfx.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import wsd.printers.agent.springfx.SellerAgentMain;
import wsd.printers.agent.springfx.gui.DocumentManagePresentation;
import wsd.printers.agent.springfx.gui.ScreensConfig;
import wsd.printers.agent.springfx.gui.SpecifyAgentPresentation;


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
    SpecifyAgentPresentation specifyAgentPresentation(ScreensConfig screensConfig) {
        return new SpecifyAgentPresentation(screensConfig);
    }

    @Bean
    @Autowired
    DocumentManagePresentation documentManagePresentation(ScreensConfig screensConfig) {
        return new DocumentManagePresentation(screensConfig);
    }

    @Bean
    SellerAgentMain getAgentBuyer(){
        SellerAgentMain instance = SellerAgentMain.getInstance();
        while (instance == null){
            logger.info("Waiting for agent instance");
            try {
                Thread.sleep(1000);
                instance = SellerAgentMain.getInstance();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        return instance;
    }
}
