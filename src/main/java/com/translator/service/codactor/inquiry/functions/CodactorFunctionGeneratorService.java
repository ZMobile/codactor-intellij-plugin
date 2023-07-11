package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.function.ChatGptFunction;

import java.util.List;

public interface CodactorFunctionGeneratorService {
    List<ChatGptFunction> generateCodactorFunctions();
}
