package wsd.printers.agent.springfx.control;


import wsd.printers.agent.springfx.model.LanguageModel;

public class LanguageController {

    private LanguageModel model;

    public LanguageController(LanguageModel model) {
        this.model = model;
        toEnglish();
    }

    public void toEnglish() {
        model.setBundle(LanguageModel.Language.EN);
    }

    public void toRomanian() {
        model.setBundle(LanguageModel.Language.RO);
    }

    public LanguageModel.Language getLanguage() {
        return model.getLanguage();
    }
}
