package com.translator.service.ui.tool;

import com.translator.view.console.CodactorConsole;
import com.translator.view.viewer.*;

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
