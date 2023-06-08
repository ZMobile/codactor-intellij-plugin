package com.translator.service.codactor.ui.tool;

import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.viewer.*;

public interface CodactorToolWindowService {
    void openCodactorConsoleToolWindow();

    void openModificationQueueViewerToolWindow();

    void openProvisionalModificationViewerToolWindow();

    void closeModificationQueueViewerToolWindow();

    void openInquiryViewerToolWindow();

    void openInquiryListViewerToolWindow();

    void openHistoricalModificationListViewerToolWindow();

    void closeInquiryViewerToolWindow();

    InquiryViewer getInquiryViewer();

    InquiryListViewer getInquiryListViewer();

    HistoricalModificationListViewer getHistoricalModificationListViewer();

    ModificationQueueViewer getModificationQueueViewer();

    ProvisionalModificationViewer getProvisionalModificationViewer();

    CodactorConsole getConsole();

    void setConsole(CodactorConsole console);
}
