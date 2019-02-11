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
import com.cesarbassani.pecbr.model.Penalizacao;

import java.util.List;

public class PenalizacaoAdapter extends RecyclerView.Adapter<PenalizacaoAdapter.PenalizacaoViewHolder> {

    private List<Penalizacao> penalizacoes;

    public PenalizacaoAdapter(List<Penalizacao> penalizacoes) {
        this.penalizacoes = penalizacoes;
    }

    @NonNull
    @Override
    public PenalizacaoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_penalizacao_personalizada, viewGroup, false);

        return new PenalizacaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PenalizacaoViewHolder penalizacaoViewHolder, int i) {
        Penalizacao penalizacao = penalizacoes.get(i);
        if (penalizacao.getTotal() != null) {

            penalizacaoViewHolder.descricao.setText(penalizacao.getDescricao());
            penalizacaoViewHolder.qtde.setText(String.valueOf(penalizacao.getQuantidade()));
            penalizacaoViewHolder.total.setText(String.valueOf(penalizacao.getTotal()));
            penalizacaoViewHolder.mediaLote.setText(String.valueOf(penalizacao.getMedia()) + "/@");
            if (!penalizacao.getObservacoes().equals("")) {
                penalizacaoViewHolder.observacoes.setText(penalizacao.getObservacoes());
            } else {
                penalizacaoViewHolder.observacoes.setVisibility(View.GONE);
            }
        } else {

            penalizacaoViewHolder.total.setVisibility(View.GONE);
            penalizacaoViewHolder.mediaLote.setVisibility(View.GONE);

            penalizacaoViewHolder.layout_penalizacao_total_linha.setVisibility(View.GONE);
            penalizacaoViewHolder.layout_penalizacao_media_linha.setVisibility(View.GONE);

            penalizacaoViewHolder.descricao.setText(penalizacao.getDescricao());
            penalizacaoViewHolder.qtde.setText(String.valueOf(penalizacao.getQuantidade()));
            if (!penalizacao.getObservacoes().equals("")) {
                penalizacaoViewHolder.observacoes.setText(penalizacao.getObservacoes());
            } else {
                penalizacaoViewHolder.observacoes.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return penalizacoes.size();
    }

    public static class PenalizacaoViewHolder extends RecyclerView.ViewHolder {

        protected TextView descricao;
        protected TextView qtde;
        protected TextView total;
        protected TextView mediaLote;
        protected TextView observacoes;

        protected LinearLayout layout_penalizacao_total_linha;
        protected LinearLayout layout_penalizacao_media_linha;

        public PenalizacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.lista_penalizacao_descricao);
            qtde = itemView.findViewById(R.id.lista_penalizacao_quantidade);
            total = itemView.findViewById(R.id.lista_penalizacao_Total);
            mediaLote = itemView.findViewById(R.id.lista_penalizacao_media);
            observacoes = itemView.findViewById(R.id.lista_penalizacao_observacao);

            layout_penalizacao_total_linha = itemView.findViewById(R.id.layout_penalizacao_total_linha);
            layout_penalizacao_media_linha = itemView.findViewById(R.id.layout_penalizacao_media_linha);
        }
    }
}
