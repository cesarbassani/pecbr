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
                if (!bonificacao.getObservacoes().equals("")) {
                    observacoes.setText(bonificacao.getObservacoes());
                } else {
                    observacoes.setVisibility(View.GONE);
                }

            } else {
                convertView = activity.getLayoutInflater().inflate(R.layout.lista_bonificacao_personalizada, parent, false);

                TextView tipo = convertView.findViewById(R.id.lista_bonificacao_tipo);
                TextView qtde = convertView.findViewById(R.id.lista_bonificacao_qtde);
                TextView observacoes = convertView.findViewById(R.id.lista_bonificacao_observacao);
                TextView total = convertView.findViewById(R.id.lista_bonificacao_total);
                TextView media = convertView.findViewById(R.id.lista_bonificacao_media);

                LinearLayout layout_bonificacao_total_linha = convertView.findViewById(R.id.layout_bonificacao_total_linha);
                LinearLayout layout_bonificacao_media_linha = convertView.findViewById(R.id.layout_bonificacao_media_linha);

                total.setVisibility(View.GONE);
                media.setVisibility(View.GONE);

                layout_bonificacao_total_linha.setVisibility(View.GONE);
                layout_bonificacao_media_linha.setVisibility(View.GONE);

                qtde.setText(String.valueOf(bonificacao.getQtde()));
                if (!bonificacao.getObservacoes().equals("")) {
                    observacoes.setText(bonificacao.getObservacoes());
                } else {
                    observacoes.setVisibility(View.GONE);
                }
                tipo.setText(bonificacao.getTipo());
            }
        }

        return convertView;
    }
}
