package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Lote implements Serializable, Parcelable {

    private String nomeCliente;
    private String propriedade;
    private String qtdeAnimaisLote;

    public Lote() {
    }

    protected Lote(Parcel in) {
        nomeCliente = in.readString();
        propriedade = in.readString();
        qtdeAnimaisLote = in.readString();
    }

    public static final Creator<Lote> CREATOR = new Creator<Lote>() {
        @Override
        public Lote createFromParcel(Parcel in) {
            return new Lote(in);
        }

        @Override
        public Lote[] newArray(int size) {
            return new Lote[size];
        }
    };

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(String propriedade) {
        this.propriedade = propriedade;
    }

    public String getQtdeAnimaisLote() {
        return qtdeAnimaisLote;
    }

    public void setQtdeAnimaisLote(String qtdeAnimaisLote) {
        this.qtdeAnimaisLote = qtdeAnimaisLote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nomeCliente);
        dest.writeString(propriedade);
        dest.writeString(qtdeAnimaisLote);
    }
}
