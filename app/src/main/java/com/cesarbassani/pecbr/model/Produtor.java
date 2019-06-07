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

public class Produtor implements Serializable, Parcelable {

    private String id;
    private String nomeProdutor;

    public Produtor() {
    }

    protected Produtor(Parcel in) {
        id = in.readString();
        nomeProdutor = in.readString();
    }

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("produtores");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference produtoresRef = database.child("produtores")
                .child(getId());

        Map<String, Object> valoresProdutor = converterParaMap();

        produtoresRef.updateChildren(valoresProdutor);
    }

    public static final Creator<Produtor> CREATOR = new Creator<Produtor>() {
        @Override
        public Produtor createFromParcel(Parcel in) {
            return new Produtor(in);
        }

        @Override
        public Produtor[] newArray(int size) {
            return new Produtor[size];
        }
    };

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> produtorMap = new HashMap<>();
        produtorMap.put("nomeProdutor", getNomeProdutor());

        return produtorMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeProdutor() {
        return nomeProdutor;
    }

    public void setNomeProdutor(String nomeProdutor) {
        this.nomeProdutor = nomeProdutor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nomeProdutor);
    }

    @Override
    public String toString() {
        return nomeProdutor;
    }
}
