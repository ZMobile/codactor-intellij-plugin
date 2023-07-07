package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;

import java.util.List;

public interface InquiryChatListFunctionCallCompressorService {
    List<InquiryChatViewer> compress(List<InquiryChat> inquiryChats);
}
