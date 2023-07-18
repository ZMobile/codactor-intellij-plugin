package com.translator.service.codactor.inquiry;

import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import java.util.HashMap;
import java.util.Map;

public class InquiryViewerMapServiceImpl implements InquiryViewerMapService {
    private final Map<String, InquiryViewer> inquiryViewerMap;

    public InquiryViewerMapServiceImpl() {
        this.inquiryViewerMap = new HashMap<>();
    }

    public Map<String, InquiryViewer> getInquiryViewerMap() {
        return inquiryViewerMap;
    }
}
