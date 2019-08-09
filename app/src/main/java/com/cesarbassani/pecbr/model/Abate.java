package com.cesarbassani.pecbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Abate implements Serializable, Parcelable {

    private String id;
    private Usuario tecnico;
    private String frigorifico;
    private Lote lote;
    private Categoria categoria;
    private Rendimento rendimento;
    private Acabamento acabamento;
    private Maturidade maturidade;
    private List<Bonificacao> bonificacoes = new ArrayList<>();
    private List<Penalizacao> penalizacoes = new ArrayList<>();
    private Acerto acerto;
    private String observacoes;
    private String dataAbate;
    private String fotoLote;
    private Cliente cliente;
    private Propriedade propriedade;
    private boolean status = true;

    public Abate() {
    }

    protected Abate(Parcel in) {
        id = in.readString();
        frigorifico = in.readString();
        observacoes = in.readString();
        dataAbate = in.readString();
        fotoLote = in.readString();
        tecnico = in.readParcelable(Usuario.class.getClassLoader());
        cliente = in.readParcelable(Cliente.class.getClassLoader());
        propriedade = in.readParcelable(Cliente.class.getClassLoader());
        lote = in.readParcelable(Lote.class.getClassLoader());
        categoria = in.readParcelable(Categoria.class.getClassLoader());
        rendimento = in.readParcelable(Rendimento.class.getClassLoader());
        acabamento = in.readParcelable(Acabamento.class.getClassLoader());
        maturidade = in.readParcelable(Maturidade.class.getClassLoader());
        acerto = in.readParcelable(Acerto.class.getClassLoader());
        bonificacoes = in.readArrayList(Bonificacao.class.getClassLoader());
        penalizacoes = in.readArrayList(Penalizacao.class.getClassLoader());
        status = in.readByte() != 0;
    }

    public static final Creator<Abate> CREATOR = new Creator<Abate>() {
        @Override
        public Abate createFromParcel(Parcel in) {
            return new Abate(in);
        }

        @Override
        public Abate[] newArray(int size) {
            return new Abate[size];
        }
    };

    public void salvar() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("abates");
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(this);
    }

    public void atualizar() {
//        String identificadorAbate = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference abatesRef = database.child("abates")
                .child(getId());

        Map<String, Object> valoresAbate = converterParaMap();

        abatesRef.updateChildren(valoresAbate);
    }

    @Exclude
    private Map<String, Object> converterParaMap() {
        HashMap<String, Object> abateMap = new HashMap<>();
        abateMap.put("tecnico", getTecnico());
        abateMap.put("cliente", getCliente());
        abateMap.put("propriedade", getCliente());
        abateMap.put("frigorifico", getFrigorifico());
        abateMap.put("dataAbate", getDataAbate());
        abateMap.put("lote", getLote());
        abateMap.put("categoria", getCategoria());
        abateMap.put("rendimento", getRendimento());
        abateMap.put("acabamento", getAcabamento());
        abateMap.put("maturidade", getMaturidade());
        abateMap.put("bonificacoes", getBonificacoes());
        abateMap.put("penalizacoes", getPenalizacoes());
        abateMap.put("acerto", getAcerto());
        abateMap.put("observacoes", getObservacoes());
        abateMap.put("fotoLote", getFotoLote());
        abateMap.put("status", getStatus());

        return abateMap;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Rendimento getRendimento() {
        return rendimento;
    }

    public void setRendimento(Rendimento rendimento) {
        this.rendimento = rendimento;
    }

    public Acabamento getAcabamento() {
        return acabamento;
    }

    public void setAcabamento(Acabamento acabamento) {
        this.acabamento = acabamento;
    }

    public Maturidade getMaturidade() {
        return maturidade;
    }

    public void setMaturidade(Maturidade maturidade) {
        this.maturidade = maturidade;
    }

    public List<Bonificacao> getBonificacoes() {
        return bonificacoes;
    }

    public void setBonificacoes(List<Bonificacao> bonificacoes) {
        this.bonificacoes = bonificacoes;
    }

    public List<Penalizacao> getPenalizacoes() {
        return penalizacoes;
    }

    public void setPenalizacoes(List<Penalizacao> penalizacoes) {
        this.penalizacoes = penalizacoes;
    }

    public Acerto getAcerto() {
        return acerto;
    }

    public void setAcerto(Acerto acerto) {
        this.acerto = acerto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public String getFrigorifico() {
        return frigorifico;
    }

    public void setFrigorifico(String frigorifico) {
        this.frigorifico = frigorifico;
    }

    public String getDataAbate() {
        return dataAbate;
    }

    public void setDataAbate(String dataAbate) {
        this.dataAbate = dataAbate;
    }

    public String getFotoLote() {
        return fotoLote;
    }

    public void setFotoLote(String fotoLote) {
        this.fotoLote = fotoLote;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(frigorifico);
        dest.writeString(observacoes);
        dest.writeString(dataAbate);
        dest.writeString(fotoLote);
        dest.writeParcelable(tecnico, flags);
        dest.writeParcelable(cliente, flags);
        dest.writeParcelable(propriedade, flags);
        dest.writeParcelable(lote, flags);
        dest.writeParcelable(categoria, flags);
        dest.writeParcelable(rendimento, flags);
        dest.writeParcelable(acabamento, flags);
        dest.writeParcelable(maturidade, flags);
        dest.writeParcelable(acerto, flags);
        Object[] objectsBonificacoes = bonificacoes.toArray();
        dest.writeArray(objectsBonificacoes);
        Object[] objectsPenalizacoes = penalizacoes.toArray();
        dest.writeArray(objectsPenalizacoes);
        dest.writeByte((byte) (status ? 1 : 0));
    }
}
