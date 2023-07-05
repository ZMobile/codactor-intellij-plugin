package com.translator.service.codactor.functions;

import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunction;

import java.util.List;

public interface CodactorFunctionGeneratorService {
    List<ChatGptFunction> generateCodactorFunctions();
}
