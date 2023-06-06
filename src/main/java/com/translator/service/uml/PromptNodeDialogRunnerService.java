package com.translator.service.uml;

import com.translator.model.uml.node.PromptNode;
import com.translator.view.uml.dialog.PromptNodeDialog;

public interface PromptNodeDialogRunnerService {
    void run(PromptNode promptNode, String model);
}
