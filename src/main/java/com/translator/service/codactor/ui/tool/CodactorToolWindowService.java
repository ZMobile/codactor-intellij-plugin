package com.translator.service.codactor.ui.tool;

import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import com.translator.view.codactor.viewer.modification.ProvisionalModificationViewer;

public interface CodactorToolWindowService {
    void openCodactorConsoleToolWindow();

    void openModificationQueueViewerToolWindow();

    void openProvisionalModificationViewerToolWindow();

    void closeModificationQueueViewerToolWindow();

    void createInquiryViewerToolWindow(InquiryViewer inquiryViewer);

    void openInquiryListViewerToolWindow();

    void openHistoricalModificationListViewerToolWindow();

    void closeInquiryViewerToolWindow();

    InquiryListViewer getInquiryListViewer();

    HistoricalModificationListViewer getHistoricalModificationListViewer();

    ModificationQueueViewer getModificationQueueViewer();

    ProvisionalModificationViewer getProvisionalModificationViewer();

    CodactorConsole getConsole();

    void setConsole(CodactorConsole console);
}
