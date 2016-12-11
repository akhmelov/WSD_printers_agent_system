package wsd.printers.agent.springfx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import wsd.printers.agent.springfx.gui.DocumentManagePresentation;
import wsd.printers.agent.springfx.gui.ScreensConfig;


@Configuration
//@Import(ScreensConfig.class)
@ComponentScan("wsd.printers.agent.springfx")
public class AppConfig {

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
}
