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

public class Propriedade implements Serializable, Parcelable {

    private String id;
    private String nomePropriedade;

    public Propriedade() {
    }

    public Propriedade(Parcel in) {
        this.id = in.readString();
        this.nomePropriedade = in.readString();
    }

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("propriedades");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference clientesRef = database.child("propriedades")
                .child(getId());

        Map<String, Object> valoresCliente = converterParaMap();

        clientesRef.updateChildren(valoresCliente);
    }

    public static final Creator<Propriedade> CREATOR = new Creator<Propriedade>() {
        @Override
        public Propriedade createFromParcel(Parcel in) {
            return new Propriedade(in);
        }

        @Override
        public Propriedade[] newArray(int size) {
            return new Propriedade[size];
        }
    };

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> clienteMap = new HashMap<>();
        clienteMap.put("nomePropriedade", getNomePropriedade());

        return clienteMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomePropriedade() {
        return nomePropriedade;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nomePropriedade);
    }

    @Override
    public String toString() {
        return nomePropriedade;
    }
}
