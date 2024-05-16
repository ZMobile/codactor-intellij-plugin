package com.translator.service.codactor.test;

import com.github.javaparser.ast.body.BodyDeclaration;

public class CodeBodyResource {
    private String signature;
    private String body;
    private BodyDeclaration<?> bodyDeclaration;

    public CodeBodyResource(String signature, String body, BodyDeclaration<?> bodyDeclaration) {
        this.signature = signature;
        this.body = body;
        this.bodyDeclaration = bodyDeclaration;
    }

    public CodeBodyResource(String signature, String body) {
        this.signature = signature;
        this.body = body;
    }

    public CodeBodyResource(BodyDeclaration<?> bodyDeclaration) {
        this.bodyDeclaration = bodyDeclaration;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public BodyDeclaration<?> getBodyDeclaration() {
        return bodyDeclaration;
    }

    public void setBodyDeclaration(BodyDeclaration<?> bodyDeclaration) {
        this.bodyDeclaration = bodyDeclaration;
    }
}
