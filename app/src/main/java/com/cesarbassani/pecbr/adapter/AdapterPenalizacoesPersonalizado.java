package com.cesarbassani.pecbr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.Penalizacao;

import java.util.List;

public class AdapterPenalizacoesPersonalizado extends BaseAdapter {

    private final List<Penalizacao> penalizacoes;
    private final Activity activity;

    public AdapterPenalizacoesPersonalizado(List<Penalizacao> penalizacoes, Activity activity) {
        this.penalizacoes = penalizacoes;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return penalizacoes.size();
    }

    @Override
    public Object getItem(int position) {
        return penalizacoes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Penalizacao penalizacao = penalizacoes.get(position);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (penalizacao.getTotal() != null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_penalizacao_personalizada, parent, false);

                TextView descricao = convertView.findViewById(R.id.lista_penalizacao_descricao);
                TextView qtde = convertView.findViewById(R.id.lista_penalizacao_quantidade);
                TextView total = convertView.findViewById(R.id.lista_penalizacao_Total);
                TextView mediaLote = convertView.findViewById(R.id.lista_penalizacao_media);
                TextView observacoes = convertView.findViewById(R.id.lista_penalizacao_observacao);

                descricao.setText(penalizacao.getDescricao());
                qtde.setText(String.valueOf(penalizacao.getQuantidade()));
                total.setText(String.valueOf(penalizacao.getTotal()));
                mediaLote.setText(String.valueOf(penalizacao.getMedia()) + "/@");
                observacoes.setText(penalizacao.getObservacoes());
            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_penalizacao_personalizada_simplificada, parent, false);

                TextView descricao = convertView.findViewById(R.id.lista_penalizacao_descricao);
                TextView qtde = convertView.findViewById(R.id.lista_penalizacao_quantidade);
                TextView observacoes = convertView.findViewById(R.id.lista_penalizacao_observacao);

                descricao.setText(penalizacao.getDescricao());
                qtde.setText(String.valueOf(penalizacao.getQuantidade()));
                observacoes.setText(penalizacao.getObservacoes());
            }
        }

        return convertView;
    }
}
