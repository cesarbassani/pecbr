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
    private Produtor produtor;

    public Propriedade() {
    }

    protected Propriedade(Parcel in) {
        id = in.readString();
        nomePropriedade = in.readString();
        produtor = in.readParcelable(Produtor.class.getClassLoader());
    }

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("propriedades");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference propriedadesRef = database.child("propriedades")
                .child(getId());

        Map<String, Object> valoresPropriedade = converterParaMap();

        propriedadesRef.updateChildren(valoresPropriedade);
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
        HashMap<String, Object> propriedadeMap = new HashMap<>();
        propriedadeMap.put("nomePropriedade", getNomePropriedade());
        propriedadeMap.put("produtor", getProdutor());

        return propriedadeMap;
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

    public Produtor getProdutor() {
        return produtor;
    }

    public void setProdutor(Produtor produtor) {
        this.produtor = produtor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nomePropriedade);
        dest.writeParcelable(produtor, flags);
    }

    @Override
    public String toString() {
        return nomePropriedade;
    }
}
