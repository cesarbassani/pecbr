package com.cesarbassani.pecbr.model;

import java.io.Serializable;

public class ParcialPenalizacao implements Serializable {

    private String descricao;
    private String qtde;
    private String peso;
    private String desconto;
    private String valorPenalizacao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getValorPenalizacao() {
        return valorPenalizacao;
    }

    public void setValorPenalizacao(String valorPenalizacao) {
        this.valorPenalizacao = valorPenalizacao;
    }

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }
}
