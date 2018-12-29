package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Acerto implements Serializable, Parcelable {

    private String totalBruto;
    private String descontoFunrural;
    private String totalAntecipacao;
    private String totalLiquido;
    private String arrobaNegociada;
    private String arrobaRecebida;
    private String arrobaRecebidaComFunrural;
    private String prazo;
    private int dias;

    public Acerto() {
    }

    protected Acerto(Parcel in) {
        totalBruto = in.readString();
        descontoFunrural = in.readString();
        totalAntecipacao = in.readString();
        totalLiquido = in.readString();
        arrobaNegociada = in.readString();
        arrobaRecebida = in.readString();
        arrobaRecebidaComFunrural = in.readString();
        prazo = in.readString();
        dias = in.readInt();
    }

    public static final Creator<Acerto> CREATOR = new Creator<Acerto>() {
        @Override
        public Acerto createFromParcel(Parcel in) {
            return new Acerto(in);
        }

        @Override
        public Acerto[] newArray(int size) {
            return new Acerto[size];
        }
    };

    public String getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(String totalBruto) {
        this.totalBruto = totalBruto;
    }

    public String getDescontoFunrural() {
        return descontoFunrural;
    }

    public void setDescontoFunrural(String descontoFunrural) {
        this.descontoFunrural = descontoFunrural;
    }

    public String getTotalAntecipacao() {
        return totalAntecipacao;
    }

    public void setTotalAntecipacao(String totalAntecipacao) {
        this.totalAntecipacao = totalAntecipacao;
    }

    public String getTotalLiquido() {
        return totalLiquido;
    }

    public void setTotalLiquido(String totalLiquido) {
        this.totalLiquido = totalLiquido;
    }

    public String getArrobaNegociada() {
        return arrobaNegociada;
    }

    public void setArrobaNegociada(String arrobaNegociada) {
        this.arrobaNegociada = arrobaNegociada;
    }

    public String getArrobaRecebida() {
        return arrobaRecebida;
    }

    public void setArrobaRecebida(String arrobaRecebida) {
        this.arrobaRecebida = arrobaRecebida;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getArrobaRecebidaComFunrural() {
        return arrobaRecebidaComFunrural;
    }

    public void setArrobaRecebidaComFunrural(String arrobaRecebidaComFunrural) {
        this.arrobaRecebidaComFunrural = arrobaRecebidaComFunrural;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalBruto);
        dest.writeString(descontoFunrural);
        dest.writeString(totalAntecipacao);
        dest.writeString(totalLiquido);
        dest.writeString(arrobaNegociada);
        dest.writeString(arrobaRecebida);
        dest.writeString(arrobaRecebidaComFunrural);
        dest.writeString(prazo);
        dest.writeInt(dias);
    }
}
