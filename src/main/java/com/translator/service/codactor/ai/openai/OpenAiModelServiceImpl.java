package com.translator.service.codactor.ai.openai;

import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;

import javax.inject.Inject;

public class OpenAiModelServiceImpl implements OpenAiModelService {
    private String selectedOpenAiModel;
    private CodactorToolWindowService codactorToolWindowService;

    @Inject
    public OpenAiModelServiceImpl(CodactorToolWindowService codactorToolWindowService) {
        this.codactorToolWindowService = codactorToolWindowService;
        this.selectedOpenAiModel = "gpt-3.5-turbo";
    }

    public String getSelectedOpenAiModel() {
        return selectedOpenAiModel;
    }

    public void setSelectedOpenAiModel(String selectedOpenAiModel) {
        this.selectedOpenAiModel = selectedOpenAiModel;
        CodactorConsole console = codactorToolWindowService.getConsole();
        if (console != null) {
            console.updateModelComboBox();
        }
    }
}
