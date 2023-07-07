package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;
import jnr.ffi.annotations.In;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryChatListFunctionCallCompressorServiceImpl implements InquiryChatListFunctionCallCompressorService {
    @Override
    public List<InquiryChatViewer> compress(List<InquiryChat> inquiryChats) {
        List<InquiryChatViewer> compressedInquiryChats = new ArrayList<>();
        List<InquiryChat> functionCallOrResponseChats = new ArrayList<>();

        for (InquiryChat currentChat : inquiryChats) {
            if (currentChat.getFunctionCall() != null
                    || currentChat.getFrom().equalsIgnoreCase("function")) {
                functionCallOrResponseChats.add(currentChat);
            } else {
                InquiryChatViewer inquiryChatViewer;
                if (!functionCallOrResponseChats.isEmpty()) {
                    inquiryChatViewer = new InquiryChatViewer.Builder()
                            .withInquiryChat(currentChat)
                            .withFunctionCalls(functionCallOrResponseChats)
                            .build();
                    functionCallOrResponseChats.clear();
                } else {
                    inquiryChatViewer = new InquiryChatViewer.Builder()
                            .withInquiryChat(currentChat)
                            .build();
                }
                compressedInquiryChats.add(inquiryChatViewer);
            }
        }

        // Deal with trailing function call/response chats
        if (!functionCallOrResponseChats.isEmpty()) {
            InquiryChat lastFunctionChat = functionCallOrResponseChats.get(functionCallOrResponseChats.size() - 1);
            InquiryChatViewer inquiryChatViewer = new InquiryChatViewer.Builder()
                    .withInquiryChat(lastFunctionChat)
                    .withFunctionCalls(functionCallOrResponseChats)
                    .build();
            compressedInquiryChats.add(inquiryChatViewer);
        }

        return compressedInquiryChats;
    }
}