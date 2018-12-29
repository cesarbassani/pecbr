package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Bonificacao implements Serializable, Parcelable {

    private String tipo;
    private String qtde;
    private String total;
    private String mediaLote;
    private String observacoes;

    public Bonificacao() {
    }

    protected Bonificacao(Parcel in) {
        tipo = in.readString();
        qtde = in.readString();
        total = in.readString();
        mediaLote = in.readString();
        observacoes = in.readString();
    }

    public static final Creator<Bonificacao> CREATOR = new Creator<Bonificacao>() {
        @Override
        public Bonificacao createFromParcel(Parcel in) {
            return new Bonificacao(in);
        }

        @Override
        public Bonificacao[] newArray(int size) {
            return new Bonificacao[size];
        }
    };

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getQtde() {
        return qtde;
    }

    public void setQtde(String qtde) {
        this.qtde = qtde;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMediaLote() {
        return mediaLote;
    }

    public void setMediaLote(String mediaLote) {
        this.mediaLote = mediaLote;
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
        dest.writeString(tipo);
        dest.writeString(qtde);
        dest.writeString(total);
        dest.writeString(mediaLote);
        dest.writeString(observacoes);
    }
}
