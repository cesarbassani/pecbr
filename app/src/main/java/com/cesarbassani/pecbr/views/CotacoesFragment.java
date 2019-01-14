package com.cesarbassani.pecbr.views;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.Cotacao;
import com.cesarbassani.pecbr.utils.Tools;
import com.cesarbassani.pecbr.utils.ViewAnimation;

public class CotacoesFragment extends Fragment {

    private ImageButton bt_toggle_items, bt_toggle_address, bt_toggle_description;
    private View lyt_expand_items, lyt_expand_address, lyt_expand_description;
    private NestedScrollView nested_scroll_view;
    private Cotacao cotacao = new Cotacao();
    private String dataCotacao;
    private TextView bezerroAPrazo, bezerroAVista, boiGordoAPrazo, boiGordoAVista, boiMagroAPrazo, boiMagroAVista, vacaGordaAPrazo, vacaGordaAVista, txtDataCotacao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cotacoes, container, false);

        final Context context = view.getContext();

//        initToolbar(view);
        initComponent(view);

        return view;
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarMain);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(getActivity(), R.color.colorPrimary);
    }



    private void initComponent(View view) {

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }

        // nested scrollview
        nested_scroll_view = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        bezerroAPrazo = view.findViewById(R.id.bezerro_a_prazo);
        bezerroAVista = view.findViewById(R.id.bezerro_a_vista);
        boiGordoAPrazo = view.findViewById(R.id.boi_gordo_a_prazo);
        boiGordoAVista = view.findViewById(R.id.boi_gordo_a_vista);
        boiMagroAPrazo = view.findViewById(R.id.boi_magro_a_prazo);
        boiMagroAVista = view.findViewById(R.id.boi_magro_a_vista);
        vacaGordaAPrazo = view.findViewById(R.id.vaca_gorda_a_prazo);
        vacaGordaAVista = view.findViewById(R.id.vaca_gorda_a_vista);
        txtDataCotacao = view.findViewById(R.id.dataCotacao);

        cotacao = inicializaCotacao();

        // section items
        bt_toggle_items = (ImageButton) view.findViewById(R.id.bt_toggle_items);
        lyt_expand_items = (View) view.findViewById(R.id.lyt_expand_items);
        bt_toggle_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_items);
            }
        });

        // section address
//        bt_toggle_address = (ImageButton) view.findViewById(R.id.bt_toggle_address);
//        lyt_expand_address = (View) view.findViewById(R.id.lyt_expand_address);
//        bt_toggle_address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleSection(view, lyt_expand_address);
//            }
//        });

        // section description
//        bt_toggle_description = (ImageButton) view.findViewById(R.id.bt_toggle_description);
//        lyt_expand_description = (View) view.findViewById(R.id.lyt_expand_description);
//        bt_toggle_description.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleSection(view, lyt_expand_description);
//            }
//        });

        // copy to clipboard
        final TextView tv_invoice_code = (TextView) view.findViewById(R.id.tv_invoice_code);
        ImageButton bt_copy_code = (ImageButton) view.findViewById(R.id.bt_copy_code);
        bt_copy_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.copyToClipboard(getActivity().getApplicationContext(), tv_invoice_code.getText().toString());
            }
        });

    }

    private Cotacao inicializaCotacao() {

        cotacao.setBezerroAPrazo(bezerroAPrazo.getText().toString());
        cotacao.setBezerroAVista(bezerroAVista.getText().toString());
        cotacao.setBoiGordoAPrazo(boiGordoAPrazo.getText().toString());
        cotacao.setBoiGordoAVista(boiGordoAVista.getText().toString());
        cotacao.setBoiMagroAPrazo(boiMagroAPrazo.getText().toString());
        cotacao.setBoiMagroAVista(boiMagroAVista.getText().toString());
        cotacao.setVacaGordaAPrazo(vacaGordaAPrazo.getText().toString());
        cotacao.setVacaGordaAVista(vacaGordaAVista.getText().toString());
        cotacao.setDataCotacao(vacaGordaAVista.getText().toString());
        cotacao.setDataCotacao(txtDataCotacao.getText().toString());
        return cotacao;
    }

    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
