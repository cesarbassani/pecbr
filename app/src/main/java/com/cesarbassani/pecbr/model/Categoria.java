package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Categoria implements Serializable, Parcelable {

    private String categoria;
    private String racial;
    private String qtdeBezerrosPequenos;
    private String qtdeBezerrosMedios;
    private String qtdeBezerrosGrandes;

    public Categoria() {
    }

    protected Categoria(Parcel in) {
        categoria = in.readString();
        racial = in.readString();
        qtdeBezerrosPequenos = in.readString();
        qtdeBezerrosMedios = in.readString();
        qtdeBezerrosGrandes = in.readString();
    }

    public static final Creator<Categoria> CREATOR = new Creator<Categoria>() {
        @Override
        public Categoria createFromParcel(Parcel in) {
            return new Categoria(in);
        }

        @Override
        public Categoria[] newArray(int size) {
            return new Categoria[size];
        }
    };

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRacial() {
        return racial;
    }

    public void setRacial(String racial) {
        this.racial = racial;
    }

    public String getQtdeBezerrosPequenos() {
        return qtdeBezerrosPequenos;
    }

    public void setQtdeBezerrosPequenos(String qtdeBezerrosPequenos) {
        this.qtdeBezerrosPequenos = qtdeBezerrosPequenos;
    }

    public String getQtdeBezerrosMedios() {
        return qtdeBezerrosMedios;
    }

    public void setQtdeBezerrosMedios(String qtdeBezerrosMedios) {
        this.qtdeBezerrosMedios = qtdeBezerrosMedios;
    }

    public String getQtdeBezerrosGrandes() {
        return qtdeBezerrosGrandes;
    }

    public void setQtdeBezerrosGrandes(String qtdeBezerrosGrandes) {
        this.qtdeBezerrosGrandes = qtdeBezerrosGrandes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoria);
        dest.writeString(racial);
        dest.writeString(qtdeBezerrosPequenos);
        dest.writeString(qtdeBezerrosMedios);
        dest.writeString(qtdeBezerrosGrandes);
    }
}
