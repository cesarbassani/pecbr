package com.cesarbassani.pecbr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cesarbassani.pecbr.R;

import com.cesarbassani.pecbr.model.Bonificacao;

import java.util.List;

public class AdapterBonificacoesPersonalizado extends BaseAdapter {

    private final List<Bonificacao> bonificacoes;
    private final Activity activity;

    public AdapterBonificacoesPersonalizado(List<Bonificacao> bonificacoes, Activity activity) {
        this.bonificacoes = bonificacoes;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return bonificacoes.size();
    }

    @Override
    public Object getItem(int position) {
        return bonificacoes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bonificacao bonificacao = bonificacoes.get(position);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (bonificacao.getTotal() != null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_bonificacao_personalizada, parent, false);

                TextView tipo = convertView.findViewById(R.id.lista_bonificacao_tipo);
                TextView qtde = convertView.findViewById(R.id.lista_bonificacao_qtde);
                TextView total = convertView.findViewById(R.id.lista_bonificacao_total);
                TextView media = convertView.findViewById(R.id.lista_bonificacao_media);
                TextView observacoes = convertView.findViewById(R.id.lista_bonificacao_observacao);

                tipo.setText(bonificacao.getTipo());
                qtde.setText(String.valueOf(bonificacao.getQtde()));
                total.setText(String.valueOf(bonificacao.getTotal()));
                media.setText(String.valueOf(bonificacao.getMediaLote()) + "/@");
                observacoes.setText(bonificacao.getObservacoes());

            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_bonificacao_personalizada_simplificada, parent, false);

                TextView tipo = convertView.findViewById(R.id.lista_bonificacao_tipo);
                TextView qtde = convertView.findViewById(R.id.lista_bonificacao_qtde);
                TextView observacoes = convertView.findViewById(R.id.lista_bonificacao_observacao);


                qtde.setText(String.valueOf(bonificacao.getQtde()));
                observacoes.setText(bonificacao.getObservacoes());
                tipo.setText(bonificacao.getTipo());
            }
        }

        return convertView;
    }
}
