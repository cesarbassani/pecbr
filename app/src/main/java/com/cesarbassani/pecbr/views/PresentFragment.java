package com.cesarbassani.pecbr.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.adapter.ListaAbatesAdapter;
import com.cesarbassani.pecbr.business.GuestBusiness;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.model.GuestEntity;
import com.cesarbassani.pecbr.listener.OnAbateListenerInteractionListener;

import java.util.List;

public class PresentFragment extends Fragment {

    private ViewHolder mViewHolder = new ViewHolder();
    private GuestBusiness mGuestBusiness;
    private OnAbateListenerInteractionListener mOnGuestListenerInteractionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_resumo_abates, container, false);

        final Context context = view.getContext();

        //Obter a RecyclerView
        this.mViewHolder.mRecyclerViewAllPresent = view.findViewById(R.id.recycler_all_present);

        this.mGuestBusiness = new GuestBusiness(context);

        this.mOnGuestListenerInteractionListener = new OnAbateListenerInteractionListener() {
            @Override
            public void onListClick(int id) {
                //Abrir activity de formulario
                Bundle bundle = new Bundle();
                bundle.putInt(GuestConstants.BundleConstants.GUEST_ID, id);

                Intent intent = new Intent(context, AbateFormActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int id) {
                mGuestBusiness.remove(id);

                Toast.makeText(context, R.string.convidado_removido_sucesso, Toast.LENGTH_LONG).show();

//                loadGuests();

            }
        };

        //Definir um layout
        this.mViewHolder.mRecyclerViewAllPresent.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        this.loadGuests();
    }

//    private void loadGuests() {
//        List<GuestEntity> guestEntityList = this.mGuestBusiness.getInvited();
//
//        //Definir um adapter
//        ListaAbatesAdapter listaAbatesAdapter = new ListaAbatesAdapter(abateList, this.mOnGuestListenerInteractionListener);
//        this.mViewHolder.mRecyclerViewAllPresent.setAdapter(listaAbatesAdapter);
//
//        listaAbatesAdapter.notifyDataSetChanged();
//    }

    private static class ViewHolder {
        RecyclerView mRecyclerViewAllPresent;
    }
}
