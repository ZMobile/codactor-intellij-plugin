package com.translator.service.codactor.inquiry;

import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryChatListFunctionCallCompressorServiceImpl implements InquiryChatListFunctionCallCompressorService {
    @Override
    public List<InquiryChatViewer> compress(List<InquiryChatViewer> inquiryChatViewers) {
        List<InquiryChatViewer> compressedInquiryChats = new ArrayList<>();
        List<InquiryChatViewer> functionCallOrResponseChats = new ArrayList<>();

        for (InquiryChatViewer currentChat : inquiryChatViewers) {
            if (currentChat.getInquiryChat().getMessage() == null
                    && (currentChat.getInquiryChat().getFunctionCall() != null
                    || currentChat.getInquiryChat().getFunctionName() != null)) {
                functionCallOrResponseChats.add(currentChat);
            } else {
                if (!functionCallOrResponseChats.isEmpty()) {
                    currentChat.getFunctionCalls().addAll(functionCallOrResponseChats.stream()
                            .map(InquiryChatViewer::getInquiryChat)
                            .collect(Collectors.toList()));
                    functionCallOrResponseChats.clear();
                }
                compressedInquiryChats.add(currentChat);
            }
        }

        // Deal with trailing function call/response chats
        if (!functionCallOrResponseChats.isEmpty()) {
            InquiryChatViewer lastFunctionChat = functionCallOrResponseChats.get(functionCallOrResponseChats.size() - 1);
            if (functionCallOrResponseChats.size() > 1) {
                lastFunctionChat.getFunctionCalls().addAll(functionCallOrResponseChats.subList(0, functionCallOrResponseChats.size() - 1).stream()
                        .map(InquiryChatViewer::getInquiryChat)
                        .collect(Collectors.toList()));
            }
            compressedInquiryChats.add(lastFunctionChat);
        }

        return compressedInquiryChats;
    }
}