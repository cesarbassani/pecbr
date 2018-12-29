package com.cesarbassani.pecbr.helper;

public class ValidacaoHelper {

    public static String validaQuantidade(Integer qtde) {
        if (qtde > 1)
            return "animais";
        else
            return "animal";
    }
}
