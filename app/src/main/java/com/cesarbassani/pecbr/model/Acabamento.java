package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Acabamento implements Serializable, Parcelable {

    private String qtdeAusente;
    private String percentualAusente;
    private String qtdeEscassoMenos;
    private String percentualEscassoMenos;
    private String qtdeEscasso;
    private String percentualEscasso;
    private String qtdeMediano;
    private String percentualMediano;
    private String qtdeUniforme;
    private String percentualUniforme;
    private String qtdeExcessivo;
    private String percentualExcessivo;

    public Acabamento() {
    }

    protected Acabamento(Parcel in) {
        qtdeAusente = in.readString();
        percentualAusente = in.readString();
        qtdeEscassoMenos = in.readString();
        percentualEscassoMenos = in.readString();
        qtdeEscasso = in.readString();
        percentualEscasso = in.readString();
        qtdeMediano = in.readString();
        percentualMediano = in.readString();
        qtdeUniforme = in.readString();
        percentualUniforme = in.readString();
        qtdeExcessivo = in.readString();
        percentualExcessivo = in.readString();
    }

    public static final Creator<Acabamento> CREATOR = new Creator<Acabamento>() {
        @Override
        public Acabamento createFromParcel(Parcel in) {
            return new Acabamento(in);
        }

        @Override
        public Acabamento[] newArray(int size) {
            return new Acabamento[size];
        }
    };

    public String getQtdeAusente() {
        return qtdeAusente;
    }

    public void setQtdeAusente(String qtdeAusente) {
        this.qtdeAusente = qtdeAusente;
    }

    public String getPercentualAusente() {
        return percentualAusente;
    }

    public void setPercentualAusente(String percentualAusente) {
        this.percentualAusente = percentualAusente;
    }

    public String getQtdeEscassoMenos() {
        return qtdeEscassoMenos;
    }

    public void setQtdeEscassoMenos(String qtdeEscassoMenos) {
        this.qtdeEscassoMenos = qtdeEscassoMenos;
    }

    public String getPercentualEscassoMenos() {
        return percentualEscassoMenos;
    }

    public void setPercentualEscassoMenos(String percentualEscassoMenos) {
        this.percentualEscassoMenos = percentualEscassoMenos;
    }

    public String getQtdeEscasso() {
        return qtdeEscasso;
    }

    public void setQtdeEscasso(String qtdeEscasso) {
        this.qtdeEscasso = qtdeEscasso;
    }

    public String getPercentualEscasso() {
        return percentualEscasso;
    }

    public void setPercentualEscasso(String percentualEscasso) {
        this.percentualEscasso = percentualEscasso;
    }

    public String getQtdeMediano() {
        return qtdeMediano;
    }

    public void setQtdeMediano(String qtdeMediano) {
        this.qtdeMediano = qtdeMediano;
    }

    public String getPercentualMediano() {
        return percentualMediano;
    }

    public void setPercentualMediano(String percentualMediano) {
        this.percentualMediano = percentualMediano;
    }

    public String getQtdeUniforme() {
        return qtdeUniforme;
    }

    public void setQtdeUniforme(String qtdeUniforme) {
        this.qtdeUniforme = qtdeUniforme;
    }

    public String getPercentualUniforme() {
        return percentualUniforme;
    }

    public void setPercentualUniforme(String percentualUniforme) {
        this.percentualUniforme = percentualUniforme;
    }

    public String getQtdeExcessivo() {
        return qtdeExcessivo;
    }

    public void setQtdeExcessivo(String qtdeExcessivo) {
        this.qtdeExcessivo = qtdeExcessivo;
    }

    public String getPercentualExcessivo() {
        return percentualExcessivo;
    }

    public void setPercentualExcessivo(String percentualExcessivo) {
        this.percentualExcessivo = percentualExcessivo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qtdeAusente);
        dest.writeString(percentualAusente);
        dest.writeString(qtdeEscassoMenos);
        dest.writeString(percentualEscassoMenos);
        dest.writeString(qtdeEscasso);
        dest.writeString(percentualEscasso);
        dest.writeString(qtdeMediano);
        dest.writeString(percentualMediano);
        dest.writeString(qtdeUniforme);
        dest.writeString(percentualUniforme);
        dest.writeString(qtdeExcessivo);
        dest.writeString(percentualExcessivo);
    }
}
