package wsd.printers.agent.springfx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wsd.printers.agent.springfx.control.LanguageController;
import wsd.printers.agent.springfx.gui.ScreensConfig;
import wsd.printers.agent.springfx.model.LanguageModel;
import wsd.printers.agent.springfx.model.MessageModel;


@Configuration
@Import(ScreensConfig.class)
public class AppConfig {
    @Bean
    LanguageModel languageModel() {
        return new LanguageModel();
    }

    @Bean
    LanguageController languageController() {
        return new LanguageController(languageModel());
    }

    @Bean
    MessageModel messageModel() {
        return new MessageModel();
    }
}
