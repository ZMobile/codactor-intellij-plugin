package com.translator.service.codactor.io;

import com.intellij.ui.components.JBTextArea;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.listener.keyboard.TabKeyListener;

import javax.inject.Inject;
import java.awt.event.KeyListener;

public class TabKeyListenerServiceImpl implements TabKeyListenerService {
    private FileModificationTrackerService fileModificationTrackerService;
    private CodactorToolWindowService codactorToolWindowService;

    @Inject
    public TabKeyListenerServiceImpl(FileModificationTrackerService fileModificationTrackerService, CodactorToolWindowService codactorToolWindowService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.codactorToolWindowService = codactorToolWindowService;
    }

    @Override
    public JBTextArea setFilePath(JBTextArea jBTextArea, String filePath) {
        if (jBTextArea.getKeyListeners().length == 0) {
            jBTextArea.addKeyListener(new TabKeyListener(filePath, fileModificationTrackerService, codactorToolWindowService, jBTextArea));
        } else {
            boolean containsTabKeyListener = false;
            for (int i = 0; i < jBTextArea.getKeyListeners().length; i++) {
                KeyListener keyListener = jBTextArea.getKeyListeners()[i];
                if (keyListener instanceof TabKeyListener) {
                    TabKeyListener tabKeyListener = (TabKeyListener) jBTextArea.getKeyListeners()[i];
                    tabKeyListener.setFilePath(filePath);
                    containsTabKeyListener = true;
                    break;
                }
            }
            if (!containsTabKeyListener) {
                jBTextArea.addKeyListener(new TabKeyListener(filePath, fileModificationTrackerService, codactorToolWindowService, jBTextArea));
            }
        }
        return jBTextArea;
    }
}
