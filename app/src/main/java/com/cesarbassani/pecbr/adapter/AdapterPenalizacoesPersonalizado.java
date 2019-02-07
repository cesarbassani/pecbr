package com.cesarbassani.pecbr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
                if (!penalizacao.getObservacoes().equals("")) {
                    observacoes.setText(penalizacao.getObservacoes());
                } else {
                    observacoes.setVisibility(View.GONE);
                }
            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_penalizacao_personalizada, parent, false);

                TextView descricao = convertView.findViewById(R.id.lista_penalizacao_descricao);
                TextView qtde = convertView.findViewById(R.id.lista_penalizacao_quantidade);
                TextView observacoes = convertView.findViewById(R.id.lista_penalizacao_observacao);
                TextView total = convertView.findViewById(R.id.lista_penalizacao_Total);
                TextView mediaLote = convertView.findViewById(R.id.lista_penalizacao_media);

                LinearLayout layout_penalizacao_total_linha = convertView.findViewById(R.id.layout_penalizacao_total_linha);
                LinearLayout layout_penalizacao_media_linha = convertView.findViewById(R.id.layout_penalizacao_media_linha);

                total.setVisibility(View.GONE);
                mediaLote.setVisibility(View.GONE);

                layout_penalizacao_total_linha.setVisibility(View.GONE);
                layout_penalizacao_media_linha.setVisibility(View.GONE);

                descricao.setText(penalizacao.getDescricao());
                qtde.setText(String.valueOf(penalizacao.getQuantidade()));
                if (!penalizacao.getObservacoes().equals("")) {
                    observacoes.setText(penalizacao.getObservacoes());
                } else {
                    observacoes.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }
}
