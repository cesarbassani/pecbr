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

public class Frigorifico implements Serializable, Parcelable {

    private String id;
    private String nomeFrigorifico;

    public Frigorifico() {
    }

    protected Frigorifico(Parcel in) {
        id = in.readString();
        nomeFrigorifico = in.readString();
    }

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("frigorificos");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference cotacoesRef = database.child("frigorificos")
                .child(getId());

        Map<String, Object> valoresFrigorifico = converterParaMap();

        cotacoesRef.updateChildren(valoresFrigorifico);
    }

    public static final Creator<Frigorifico> CREATOR = new Creator<Frigorifico>() {
        @Override
        public Frigorifico createFromParcel(Parcel in) {
            return new Frigorifico(in);
        }

        @Override
        public Frigorifico[] newArray(int size) {
            return new Frigorifico[size];
        }
    };

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> frigorificoMap = new HashMap<>();
        frigorificoMap.put("nomeFrigorifico", getNomeFrigorifico());

        return frigorificoMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeFrigorifico() {
        return nomeFrigorifico;
    }

    public void setNomeFrigorifico(String nomeFrigorifico) {
        this.nomeFrigorifico = nomeFrigorifico;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nomeFrigorifico);
    }

    @Override
    public String toString() {
        return nomeFrigorifico;
    }
}
