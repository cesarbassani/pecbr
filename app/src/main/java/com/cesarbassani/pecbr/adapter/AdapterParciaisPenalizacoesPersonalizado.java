package com.cesarbassani.pecbr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.ParcialPenalizacao;

import java.util.List;
import java.util.Locale;

public class AdapterParciaisPenalizacoesPersonalizado extends BaseAdapter {

    private final List<ParcialPenalizacao> parciaisPenalizacao;
    private final Activity activity;

    public AdapterParciaisPenalizacoesPersonalizado(List<ParcialPenalizacao> parciaisPenalizacao, Activity activity) {
        this.parciaisPenalizacao = parciaisPenalizacao;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return parciaisPenalizacao.size();
    }

    @Override
    public Object getItem(int position) {
        return parciaisPenalizacao.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ParcialPenalizacao parcialPenalizacao = parciaisPenalizacao.get(position);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (!parcialPenalizacao.getDesconto().equals("")) {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_parcial_penalizacao_personalizada, parent, false);

                TextView descricao = convertView.findViewById(R.id.lista_parcial_penalizacao_descricao);
                TextView qtde = convertView.findViewById(R.id.lista_parcial_penalizacao_qtde);
                TextView peso = convertView.findViewById(R.id.lista_parcial_penalizacao_peso);
                TextView valorPenalizacao = convertView.findViewById(R.id.lista_parcial_penalizacao_valor);

                descricao.setText(String.valueOf(parcialPenalizacao.getDescricao()));
                qtde.setText(String.valueOf(parcialPenalizacao.getQtde()));
                peso.setText(String.valueOf(parcialPenalizacao.getPeso()));
                valorPenalizacao.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", Double.valueOf(parcialPenalizacao.getValorPenalizacao())))));
            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_parcial_penalizacao_personalizada_simplificada, parent, false);

                TextView descricao = convertView.findViewById(R.id.lista_parcial_penalizacao_descricao);
                TextView qtde = convertView.findViewById(R.id.lista_parcial_penalizacao_qtde);

                descricao.setText(String.valueOf(parcialPenalizacao.getDescricao()));
                qtde.setText(String.valueOf(parcialPenalizacao.getQtde()));
            }

        }


        return convertView;
    }
}
