package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;

import java.util.List;

public interface InquiryChatListFunctionCallCompressorService {
    List<InquiryChatViewer> compress(List<InquiryChat> inquiryChats);
}
