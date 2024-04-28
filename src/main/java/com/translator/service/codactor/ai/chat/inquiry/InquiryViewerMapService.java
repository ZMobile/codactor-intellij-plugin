package com.translator.service.codactor.ai.chat.inquiry;

import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import java.util.Map;

public interface InquiryViewerMapService {
    Map<String, InquiryViewer> getInquiryViewerMap();
}
