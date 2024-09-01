package com.translator.model.codactor.ai.chat.function;

public interface CodactorFunction {
    String execute(GptFunctionCall gptFunctionCall);
}
