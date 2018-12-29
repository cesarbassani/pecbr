package com.cesarbassani.pecbr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.ParcialBonificacao;

import java.util.List;
import java.util.Locale;

public class AdapterParciaisBonificacoesPersonalizado extends BaseAdapter {

    private final List<ParcialBonificacao> parciais;
    private final Activity activity;

    public AdapterParciaisBonificacoesPersonalizado(List<ParcialBonificacao> parciais, Activity activity) {
        this.parciais = parciais;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return parciais.size();
    }

    @Override
    public Object getItem(int position) {
        return parciais.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ParcialBonificacao parcialBonificacao = parciais.get(position);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (!parcialBonificacao.getValor_bonificacao().equals("")) {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_parcial_bonificacao_personalizada, parent, false);

                TextView qtde = convertView.findViewById(R.id.lista_parcial_bonificacao_qtde);
                TextView peso = convertView.findViewById(R.id.lista_parcial_bonificacao_peso);
                TextView tipoBonificacao = convertView.findViewById(R.id.lista_parcial_bonificacao_tipo);
                TextView valorBonificacao = convertView.findViewById(R.id.lista_parcial_bonificacao_valor);

                tipoBonificacao.setText(parcialBonificacao.getTipoBonificacao());
                qtde.setText(String.valueOf(parcialBonificacao.getQtde()));
                valorBonificacao.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", Double.valueOf(parcialBonificacao.getValor_bonificacao())))));

                if (!tipoBonificacao.getText().toString().trim().isEmpty())
                    peso.setText(String.valueOf(parcialBonificacao.getPeso()));

            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_parcial_bonificacao_personalizada_simplificada, parent, false);

                TextView qtde = convertView.findViewById(R.id.lista_parcial_bonificacao_qtde);
                TextView tipoBonificacao = convertView.findViewById(R.id.lista_parcial_bonificacao_tipo);

                tipoBonificacao.setText(parcialBonificacao.getTipoBonificacao());
                qtde.setText(String.valueOf(parcialBonificacao.getQtde()));
            }
        }

        return convertView;
    }
}
