package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Usuario implements Parcelable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {
    }

    protected Usuario(Parcel in) {
        id = in.readString();
        nome = in.readString();
        email = in.readString();
        senha = in.readString();
        foto = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getId());

        usuario.setValue(this);
    }

    public void atualizar() {
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child(identificadorUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(email);
        dest.writeString(senha);
        dest.writeString(foto);
    }
}
