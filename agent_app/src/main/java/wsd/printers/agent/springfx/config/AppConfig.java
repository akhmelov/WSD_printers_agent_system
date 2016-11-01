package wsd.printers.agent.springfx.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wsd.printers.agent.springfx.gui.ScreensConfig;


@Configuration
@Import(ScreensConfig.class)
@ComponentScan("wsd.printers.agent.springfx")
public class AppConfig {
    //beans
}
