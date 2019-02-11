package com.cesarbassani.pecbr.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.Bonificacao;

import java.util.List;

public class BonificacaoAdapter extends RecyclerView.Adapter<BonificacaoAdapter.BonificacaoViewHolder> {

    private List<Bonificacao> bonificacoes;

    public BonificacaoAdapter(List<Bonificacao> bonificacoes) {
        this.bonificacoes = bonificacoes;
    }

    @NonNull
    @Override
    public BonificacaoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_bonificacao_personalizada, viewGroup, false);

        return new BonificacaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BonificacaoViewHolder bonificacaoViewHolder, int i) {
        Bonificacao bonificacao = bonificacoes.get(i);
        if (bonificacao.getTotal() != null) {

            bonificacaoViewHolder.tipo.setText(bonificacao.getTipo());
            bonificacaoViewHolder.qtde.setText(String.valueOf(bonificacao.getQtde()));
            bonificacaoViewHolder.total.setText(String.valueOf(bonificacao.getTotal()));
            bonificacaoViewHolder.media.setText(String.valueOf(bonificacao.getMediaLote()) + "/@");
            if (!bonificacao.getObservacoes().equals("")) {
                bonificacaoViewHolder.observacoes.setText(bonificacao.getObservacoes());
            } else {
                bonificacaoViewHolder.observacoes.setVisibility(View.GONE);
            }

        } else {
            bonificacaoViewHolder.total.setVisibility(View.GONE);
            bonificacaoViewHolder.media.setVisibility(View.GONE);

            bonificacaoViewHolder.layout_bonificacao_total_linha.setVisibility(View.GONE);
            bonificacaoViewHolder.layout_bonificacao_media_linha.setVisibility(View.GONE);

            bonificacaoViewHolder.qtde.setText(String.valueOf(bonificacao.getQtde()));
            if (!bonificacao.getObservacoes().equals("")) {
                bonificacaoViewHolder.observacoes.setText(bonificacao.getObservacoes());
            } else {
                bonificacaoViewHolder.observacoes.setVisibility(View.GONE);
            }
            bonificacaoViewHolder.tipo.setText(bonificacao.getTipo());
        }
    }

    @Override
    public int getItemCount() {
        return bonificacoes.size();
    }

    public static class BonificacaoViewHolder extends RecyclerView.ViewHolder {

        protected TextView tipo;
        protected TextView qtde;
        protected TextView total;
        protected TextView media;
        protected TextView observacoes;
        protected LinearLayout layout_bonificacao_total_linha;
        protected LinearLayout layout_bonificacao_media_linha;

        public BonificacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.lista_bonificacao_tipo);
            qtde = itemView.findViewById(R.id.lista_bonificacao_qtde);
            total = itemView.findViewById(R.id.lista_bonificacao_total);
            media = itemView.findViewById(R.id.lista_bonificacao_media);
            observacoes = itemView.findViewById(R.id.lista_bonificacao_observacao);

            layout_bonificacao_total_linha = itemView.findViewById(R.id.layout_bonificacao_total_linha);
            layout_bonificacao_media_linha = itemView.findViewById(R.id.layout_bonificacao_media_linha);
        }
    }
}
