package com.cesarbassani.pecbr.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.helper.ValidacaoHelper;
import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.model.Usuario;
import com.cesarbassani.pecbr.viewholder.AbateViewHolder;
import com.google.firebase.auth.FirebaseUser;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListaAbatesAdapter extends RecyclerView.Adapter<AbateViewHolder> {

    private List<Abate> abates;
    private List<Usuario> usuarios;
    private Context context;
    private Uri uri;

    public ListaAbatesAdapter(List<Abate> abates, Context context, ArrayList<Usuario> usuarios) {

        this.abates = abates;
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public AbateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View abateView = layoutInflater.inflate(R.layout.row_abate_list, parent, false);

        return new AbateViewHolder(abateView, context);

    }

    @Override
    public void onBindViewHolder(@NonNull AbateViewHolder holder, int position) {
        //Entidade abate
        Abate abate = this.abates.get(position);
        holder.nomeCliente.setText(abate.getLote().getNomeCliente());
        holder.fazenda.setText(abate.getLote().getPropriedade());
        holder.lote.setText("Lote: " + abate.getLote().getQtdeAnimaisLote() + " " + ValidacaoHelper.validaQuantidade(Integer.parseInt(abate.getLote().getQtdeAnimaisLote())));
        holder.frigorifico.setText("Frigorífico " + abate.getFrigorifico());
        holder.categoria_racial.setText(abate.getCategoria().getCategoria() + " - " +  abate.getCategoria().getRacial());
        if (abate.getStatus() == true) {
            holder.statusBar.setBackgroundResource(R.color.colorTextPresent);
        } else {
            holder.statusBar.setBackgroundResource(R.color.red_400);
        }

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date dataFormatada = new Date();
        try {
            if (abate.getDataAbate() != null)
                dataFormatada = formato.parse(abate.getDataAbate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(dataFormatada);
        Format formatMes = new SimpleDateFormat("MMM");
        Format formatDia = new SimpleDateFormat("dd");

        holder.dataAbate.setText(formatDia.format(c.getTime()) + " de " + formatMes.format(c.getTime()).toUpperCase());

        FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
        if (abate.getTecnico() != null) {
//            Uri uri = Uri.parse(abate.getTecnico().getFoto());
            for (Usuario usuario : usuarios) {
                if (usuario.getEmail().equals(abate.getTecnico().getEmail())) {
                    uri = Uri.parse(usuario.getFoto());
                }
            }
            GlideApp.with(context)
                    .load(uri)
                    .into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return this.abates.size();
    }
}
