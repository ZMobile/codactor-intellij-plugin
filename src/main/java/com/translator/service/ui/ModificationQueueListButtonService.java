package com.translator.service.ui;

import javax.swing.*;
import java.awt.*;

public interface ModificationQueueListButtonService {
    JToggleButton getModificationQueueListButton();

    void updateModificationQueueListButton();

    Color getModificationQueueListButtonColor();

    void setModificationQueueListButton(JToggleButton modificationQueueListButton);
}
