package wsd.printers.agent.springfx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import wsd.printers.agent.springfx.gui.AgentStatePresentation;
import wsd.printers.agent.springfx.gui.DocumentManagePresentation;
import wsd.printers.agent.springfx.gui.ScreensConfig;
import wsd.printers.agent.springfx.gui.SpecifyAgentPresentation;


@Configuration
@Import(ScreensConfig.class)
@ComponentScan("wsd.printers.agent.springfx")
public class AppConfig {

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
    @Autowired
    AgentStatePresentation agentStatePresentation(ScreensConfig screensConfig){
        return new AgentStatePresentation(screensConfig);
    }
}
