package com.cesarbassani.pecbr.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.model.GuestEntity;
import com.cesarbassani.pecbr.listener.OnAbateListenerInteractionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AbateViewHolder extends RecyclerView.ViewHolder{

    public CircleImageView foto;
    public TextView nomeCliente, fazenda, frigorifico, categoria_racial, lote, dataAbate;
    public View statusBar;

    public AbateViewHolder(View itemView, Context context) {
        super(itemView);

        foto = itemView.findViewById(R.id.imageUser);
        nomeCliente = itemView.findViewById(R.id.nomeCliente);
        fazenda = itemView.findViewById(R.id.fazenda);
        frigorifico = itemView.findViewById(R.id.frigorifico);
        categoria_racial = itemView.findViewById(R.id.categoria_racial);
        lote = itemView.findViewById(R.id.lote);
        dataAbate = itemView.findViewById(R.id.text_data_abate);
        statusBar = itemView.findViewById(R.id.statusAbate);

    }

}
