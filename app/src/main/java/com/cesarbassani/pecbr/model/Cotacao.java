package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cotacao implements Serializable, Parcelable {

    private String id;
    private String bezerroAPrazo;
    private String bezerroAVista;
    private String boiGordoAPrazo;
    private String boiGordoAVista;
    private String boiMagroAPrazo;
    private String boiMagroAVista;
    private String vacaGordaAPrazo;
    private String vacaGordaAVista;
    private String dataCotacao;

    public Cotacao() {
    }

    protected Cotacao(Parcel in) {
        id = in.readString();
        bezerroAPrazo = in.readString();
        bezerroAVista = in.readString();
        boiGordoAPrazo = in.readString();
        boiGordoAVista = in.readString();
        boiMagroAPrazo = in.readString();
        boiMagroAVista = in.readString();
        vacaGordaAPrazo = in.readString();
        vacaGordaAVista = in.readString();
        dataCotacao = in.readString();
    }

    public static final Creator<Cotacao> CREATOR = new Creator<Cotacao>() {
        @Override
        public Cotacao createFromParcel(Parcel in) {
            return new Cotacao(in);
        }

        @Override
        public Cotacao[] newArray(int size) {
            return new Cotacao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cotacoes");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference cotacoesRef = database.child("cotacoes")
                .child(getId());

        Map<String, Object> valoresCotacao = converterParaMap();

        cotacoesRef.updateChildren(valoresCotacao);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(bezerroAPrazo);
        dest.writeString(bezerroAVista);
        dest.writeString(boiGordoAPrazo);
        dest.writeString(boiGordoAVista);
        dest.writeString(boiMagroAPrazo);
        dest.writeString(boiMagroAVista);
        dest.writeString(vacaGordaAPrazo);
        dest.writeString(vacaGordaAVista);
        dest.writeString(dataCotacao);
    }

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> cotacaoMap = new HashMap<>();
        cotacaoMap.put("bezerroAPrazo", getBezerroAPrazo());
        cotacaoMap.put("bezerroAVista", getBezerroAVista());
        cotacaoMap.put("boiGordoAPrazo", getBoiGordoAPrazo());
        cotacaoMap.put("boiGordoAVista", getBoiGordoAVista());
        cotacaoMap.put("boiMagroAPrazo", getBoiMagroAPrazo());
        cotacaoMap.put("boiMagroAVista", getBoiMagroAVista());
        cotacaoMap.put("vacaGordaAPrazo", getVacaGordaAPrazo());
        cotacaoMap.put("vacaGordaAVista", getVacaGordaAVista());
        cotacaoMap.put("dataCotacao", getDataCotacao());

        return cotacaoMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBezerroAPrazo() {
        return bezerroAPrazo;
    }

    public void setBezerroAPrazo(String bezerroAPrazo) {
        this.bezerroAPrazo = bezerroAPrazo;
    }

    public String getBezerroAVista() {
        return bezerroAVista;
    }

    public void setBezerroAVista(String bezerroAVista) {
        this.bezerroAVista = bezerroAVista;
    }

    public String getBoiGordoAPrazo() {
        return boiGordoAPrazo;
    }

    public void setBoiGordoAPrazo(String boiGordoAPrazo) {
        this.boiGordoAPrazo = boiGordoAPrazo;
    }

    public String getBoiGordoAVista() {
        return boiGordoAVista;
    }

    public void setBoiGordoAVista(String boiGordoAVista) {
        this.boiGordoAVista = boiGordoAVista;
    }

    public String getBoiMagroAPrazo() {
        return boiMagroAPrazo;
    }

    public void setBoiMagroAPrazo(String boiMagroAPrazo) {
        this.boiMagroAPrazo = boiMagroAPrazo;
    }

    public String getBoiMagroAVista() {
        return boiMagroAVista;
    }

    public void setBoiMagroAVista(String boiMagroAVista) {
        this.boiMagroAVista = boiMagroAVista;
    }

    public String getVacaGordaAPrazo() {
        return vacaGordaAPrazo;
    }

    public void setVacaGordaAPrazo(String vacaGordaAPrazo) {
        this.vacaGordaAPrazo = vacaGordaAPrazo;
    }

    public String getVacaGordaAVista() {
        return vacaGordaAVista;
    }

    public void setVacaGordaAVista(String vacaGordaAVista) {
        this.vacaGordaAVista = vacaGordaAVista;
    }

    public String getDataCotacao() {
        return dataCotacao;
    }

    public void setDataCotacao(String dataCotacao) {
        this.dataCotacao = dataCotacao;
    }

    @Override
    public String toString() {
        return dataCotacao;
    }
}
