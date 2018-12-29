package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Penalizacao implements Serializable, Parcelable {

    private String descricao;
    private String quantidade;
    private String pesoAnimaisPenalizados;
    private String mediaLote;
    private String Total;
    private String observacoes;

    public Penalizacao() {
    }

    protected Penalizacao(Parcel in) {
        descricao = in.readString();
        quantidade = in.readString();
        pesoAnimaisPenalizados = in.readString();
        mediaLote = in.readString();
        Total = in.readString();
        observacoes = in.readString();
    }

    public static final Creator<Penalizacao> CREATOR = new Creator<Penalizacao>() {
        @Override
        public Penalizacao createFromParcel(Parcel in) {
            return new Penalizacao(in);
        }

        @Override
        public Penalizacao[] newArray(int size) {
            return new Penalizacao[size];
        }
    };

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getPesoAnimaisPenalizados() {
        return pesoAnimaisPenalizados;
    }

    public void setPesoAnimaisPenalizados(String pesoAnimaisPenalizados) {
        this.pesoAnimaisPenalizados = pesoAnimaisPenalizados;
    }

    public String getMedia() {
        return mediaLote;
    }

    public void setMedia(String media) {
        this.mediaLote = media;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(descricao);
        dest.writeString(quantidade);
        dest.writeString(pesoAnimaisPenalizados);
        dest.writeString(mediaLote);
        dest.writeString(Total);
        dest.writeString(observacoes);
    }
}
