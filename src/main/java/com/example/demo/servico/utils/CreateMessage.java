package com.example.demo.servico.utils;

import lombok.Builder;

@Builder
public class CreateMessage {

    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String text5;
    private String text6;
    private String text7;
    private String text8;

    public String personNotFound() {
        return "CPF: " + text1 + " nao encontrado!";
    }

    public String cpfDuplicity() {
        return "CPF: " + text1 + " duplicado!";
    }

    public String telNotFound() {
        return "Telefone: " + text1 + " - " + text2 + " nao encontrado!";
    }

    public String telDuplicity() {
        return "Telefone: " + text1 + " - " + text2 + " duplicado!";
    }
}
