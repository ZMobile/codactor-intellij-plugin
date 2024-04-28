package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.function.GptFunction;

import java.util.List;

public interface CodactorFunctionGeneratorService {
    List<GptFunction> generateCodactorFunctions();
}
