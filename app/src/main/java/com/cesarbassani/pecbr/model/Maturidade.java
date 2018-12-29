package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Maturidade implements Serializable, Parcelable {

    private String qtdeZeroDentes;
    private String percentualZeroDentes;
    private String qtdeDoisDentes;
    private String percentualDoisDentes;
    private String qtdeQuatroDentes;
    private String percentualQuatroDentes;
    private String qtdeSeisDentes;
    private String percentualSeisDentes;
    private String qtdeOitoDentes;
    private String percentualOitoDentes;

    public Maturidade() {
    }

    protected Maturidade(Parcel in) {
        qtdeZeroDentes = in.readString();
        percentualZeroDentes = in.readString();
        qtdeDoisDentes = in.readString();
        percentualDoisDentes = in.readString();
        qtdeQuatroDentes = in.readString();
        percentualQuatroDentes = in.readString();
        qtdeSeisDentes = in.readString();
        percentualSeisDentes = in.readString();
        qtdeOitoDentes = in.readString();
        percentualOitoDentes = in.readString();
    }

    public static final Creator<Maturidade> CREATOR = new Creator<Maturidade>() {
        @Override
        public Maturidade createFromParcel(Parcel in) {
            return new Maturidade(in);
        }

        @Override
        public Maturidade[] newArray(int size) {
            return new Maturidade[size];
        }
    };

    public String getQtdeZeroDentes() {
        return qtdeZeroDentes;
    }

    public void setQtdeZeroDentes(String qtdeZeroDentes) {
        this.qtdeZeroDentes = qtdeZeroDentes;
    }

    public String getPercentualZeroDentes() {
        return percentualZeroDentes;
    }

    public void setPercentualZeroDentes(String percentualZeroDentes) {
        this.percentualZeroDentes = percentualZeroDentes;
    }

    public String getQtdeDoisDentes() {
        return qtdeDoisDentes;
    }

    public void setQtdeDoisDentes(String qtdeDoisDentes) {
        this.qtdeDoisDentes = qtdeDoisDentes;
    }

    public String getPercentualDoisDentes() {
        return percentualDoisDentes;
    }

    public void setPercentualDoisDentes(String percentualDoisDentes) {
        this.percentualDoisDentes = percentualDoisDentes;
    }

    public String getQtdeQuatroDentes() {
        return qtdeQuatroDentes;
    }

    public void setQtdeQuatroDentes(String qtdeQuatroDentes) {
        this.qtdeQuatroDentes = qtdeQuatroDentes;
    }

    public String getPercentualQuatroDentes() {
        return percentualQuatroDentes;
    }

    public void setPercentualQuatroDentes(String percentualQuatroDentes) {
        this.percentualQuatroDentes = percentualQuatroDentes;
    }

    public String getQtdeSeisDentes() {
        return qtdeSeisDentes;
    }

    public void setQtdeSeisDentes(String qtdeSeisDentes) {
        this.qtdeSeisDentes = qtdeSeisDentes;
    }

    public String getPercentualSeisDentes() {
        return percentualSeisDentes;
    }

    public void setPercentualSeisDentes(String percentualSeisDentes) {
        this.percentualSeisDentes = percentualSeisDentes;
    }

    public String getQtdeOitoDentes() {
        return qtdeOitoDentes;
    }

    public void setQtdeOitoDentes(String qtdeOitoDentes) {
        this.qtdeOitoDentes = qtdeOitoDentes;
    }

    public String getPercentualOitoDentes() {
        return percentualOitoDentes;
    }

    public void setPercentualOitoDentes(String percentualOitoDentes) {
        this.percentualOitoDentes = percentualOitoDentes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qtdeZeroDentes);
        dest.writeString(percentualZeroDentes);
        dest.writeString(qtdeDoisDentes);
        dest.writeString(percentualDoisDentes);
        dest.writeString(qtdeQuatroDentes);
        dest.writeString(percentualQuatroDentes);
        dest.writeString(qtdeSeisDentes);
        dest.writeString(percentualSeisDentes);
        dest.writeString(qtdeOitoDentes);
        dest.writeString(percentualOitoDentes);
    }
}
