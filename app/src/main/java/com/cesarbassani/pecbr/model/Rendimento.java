package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Rendimento implements Serializable, Parcelable {

    private String pesoFazendaKilo;
    private String pesoFazendaArroba;
    private String pesoCarcacaKilo;
    private String pesoCarcacaArroba;
    private String rendimentoCarcaça;
    private String rendimentoEstimado;

    public Rendimento() {
    }

    protected Rendimento(Parcel in) {
        pesoFazendaKilo = in.readString();
        pesoFazendaArroba = in.readString();
        pesoCarcacaKilo = in.readString();
        pesoCarcacaArroba = in.readString();
        rendimentoCarcaça = in.readString();
        rendimentoEstimado = in.readString();
    }

    public static final Creator<Rendimento> CREATOR = new Creator<Rendimento>() {
        @Override
        public Rendimento createFromParcel(Parcel in) {
            return new Rendimento(in);
        }

        @Override
        public Rendimento[] newArray(int size) {
            return new Rendimento[size];
        }
    };

    public String getPesoFazendaKilo() {
        return pesoFazendaKilo;
    }

    public void setPesoFazendaKilo(String pesoFazendaKilo) {
        this.pesoFazendaKilo = pesoFazendaKilo;
    }

    public String getPesoFazendaArroba() {
        return pesoFazendaArroba;
    }

    public void setPesoFazendaArroba(String pesoFazendaArroba) {
        this.pesoFazendaArroba = pesoFazendaArroba;
    }

    public String getPesoCarcacaKilo() {
        return pesoCarcacaKilo;
    }

    public void setPesoCarcacaKilo(String pesoCarcacaKilo) {
        this.pesoCarcacaKilo = pesoCarcacaKilo;
    }

    public String getPesoCarcacaArroba() {
        return pesoCarcacaArroba;
    }

    public void setPesoCarcacaArroba(String pesoCarcacaArroba) {
        this.pesoCarcacaArroba = pesoCarcacaArroba;
    }

    public String getRendimentoCarcaça() {
        return rendimentoCarcaça;
    }

    public void setRendimentoCarcaça(String rendimentoCarcaça) {
        this.rendimentoCarcaça = rendimentoCarcaça;
    }

    public String getRendimentoEstimado() {
        return rendimentoEstimado;
    }

    public void setRendimentoEstimado(String rendimentoEstimado) {
        this.rendimentoEstimado = rendimentoEstimado;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pesoFazendaKilo);
        dest.writeString(pesoFazendaArroba);
        dest.writeString(pesoCarcacaKilo);
        dest.writeString(pesoCarcacaArroba);
        dest.writeString(rendimentoCarcaça);
        dest.writeString(rendimentoEstimado);
    }
}
