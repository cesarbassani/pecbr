package com.cesarbassani.pecbr.model;

import java.io.Serializable;

public class ParcialBonificacao implements Serializable {

    private String tipoBonificacao;
    private String qtde;
    private String peso;
    private String valor_bonificacao;

    public String getTipoBonificacao() {
        return tipoBonificacao;
    }

    public void setTipoBonificacao(String tipoBonificacao) {
        this.tipoBonificacao = tipoBonificacao;
    }

    public String getQtde() {
        return qtde;
    }

    public void setQtde(String qtde) {
        this.qtde = qtde;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getValor_bonificacao() {
        return valor_bonificacao;
    }

    public void setValor_bonificacao(String valor_bonificacao) {
        this.valor_bonificacao = valor_bonificacao;
    }
}
