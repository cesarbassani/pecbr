package com.cesarbassani.pecbr.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.adapter.AdapterBonificacoesPersonalizado;
import com.cesarbassani.pecbr.adapter.BonificacaoAdapter;
import com.cesarbassani.pecbr.adapter.ListaAbatesAdapter;
import com.cesarbassani.pecbr.adapter.PenalizacaoAdapter;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.listener.OnAbateListenerInteractionListener;
import com.cesarbassani.pecbr.listener.RecyclerItemClickListener;
import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.model.Bonificacao;
import com.cesarbassani.pecbr.model.Penalizacao;
import com.cesarbassani.pecbr.model.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.cesarbassani.pecbr.utils.Tools.*;

public class ListaAbatesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerViewListaAbates;
    private ListaAbatesAdapter adapter;
    private Context context;
    private ArrayList<Abate> abates = new ArrayList<>();
    private DatabaseReference abateRef;
    private ValueEventListener valueEventListenerAbates;
    private Abate abate;
    private Abate abateListado;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private LinearLayout lyt_no_result;
    private RecyclerView recyclerViewBonificacao;
    private RecyclerView recyclerViewPenalizacao;
    private AdapterBonificacoesPersonalizado adapterBonificacao;
    private BonificacaoAdapter bonificacaoAdapter;
    private PenalizacaoAdapter penalizacaoAdapter;
    private StorageReference storageReference;
    private StorageReference imagens;
    private StorageReference imageRef;
    private ContentResolver contentResolver;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseAuth auth;
    private boolean supervisor = false;

    private Bitmap imagemLote;
    private Uri path;

    private OnAbateListenerInteractionListener mOnAbateListenerInteractionListener;

    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuarios;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ImageView image_lote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        verifyStoragePermissions(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_abates, container, false);

        context = view.getContext();

        initComponent(view);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        swipe();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarAbates();
        recuperaUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        abateRef.removeEventListener(valueEventListenerAbates);
        usuarioRef.removeEventListener(valueEventListenerUsuarios);
    }

    private void recuperaUsuarios() {
        valueEventListenerUsuarios = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarios.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Usuario usuario = dados.getValue(Usuario.class);
                    usuarios.add(usuario);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initComponent(View view) {

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.show();
        }

        contentResolver = getActivity().getContentResolver();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        imagens = storageReference.child("imagens").child("lote");

        abateRef = ConfiguracaoFirebase.getFirebaseDatabase().child("abates");

        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        lyt_no_result = view.findViewById(R.id.lyt_no_result);

        recyclerViewListaAbates = view.findViewById(R.id.recycler_lista_abates);
        LinearLayoutManager layout_manager = new LinearLayoutManager(context);
        layout_manager.setReverseLayout(true);
        layout_manager.setStackFromEnd(true);
        recyclerViewListaAbates.setLayoutManager(layout_manager);
        recyclerViewListaAbates.setHasFixedSize(true);

        adapter = new ListaAbatesAdapter(abates, context, usuarios);
        recyclerViewListaAbates.setAdapter(adapter);

        if (user.getEmail().equals("joaoalvaro@pecbr.com") || user.getEmail().equals("c.fantini@pecbr.com")) {
            supervisor = true;
        }

        //Evento de click
        recyclerViewListaAbates.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerViewListaAbates, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Abate abateListado = abates.get(position);
                carregarAbate(abateListado);
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                //Configurar o Alert
                alertDialog.setTitle("Editar Abate");
                alertDialog.setMessage("Escolha uma ação para editar o abate selecionado.");
                alertDialog.setCancelable(false);


//                alertDialog.setPositiveButton("PDF", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        progressDialog = new ProgressDialog(context);
//                        progressDialog.setMessage("Aguarde..."); // Setting Message
//                        progressDialog.setTitle("Gerando PDF"); // Setting Title
//                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
//                        progressDialog.show(); // Display Progress Dialog
//                        progressDialog.setCancelable(false);
//                        new Thread(new Runnable() {
//                            public void run() {
//                                try {
//                                    Thread.sleep(10000);
//                                    Abate abateListado = abates.get(position);
//                                    PDFHelper.pdfView(abateListado, context);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                progressDialog.cancel();
//                            }
//                        }).start();
//                    }
//                });

                if (abates.get(position).getStatus() == false && supervisor) {
                    alertDialog.setNeutralButton("Finalizado", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            abate = abates.get(position);
                            abate.setStatus(true);
                            abate.atualizar();
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else if (supervisor){
                    alertDialog.setNeutralButton("Aberto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            abate = abates.get(position);
                            abate.setStatus(false);
                            abate.atualizar();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (abates.size() > position) {
                            if (abates.get(position) != null) {
                                Abate abate = new Abate();
                                abate = abates.get(position);
                                Intent intent = new Intent(getActivity(), AbateFormActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("abate", abate);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

                AlertDialog alert = alertDialog.create();
                alert.show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    private void carregarAbate(Abate abateListado) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_visualizar_abate);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

//        recyclerViewBonificacao = new RecyclerView(getContext());
        recyclerViewBonificacao = dialog.findViewById(R.id.list_view_bonificacao);

//        recyclerViewPenalizacao = new RecyclerView(getContext());
        recyclerViewPenalizacao = dialog.findViewById(R.id.list_view_penalizacao);

        image_lote = dialog.findViewById(R.id.imagem_lote_visualizacao);

//        LinearLayoutManager layout_manager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBonificacao.setLayoutManager(newLLM());
        recyclerViewPenalizacao.setLayoutManager(newLLM());

        recyclerViewBonificacao.setHasFixedSize(true);
        recyclerViewPenalizacao.setHasFixedSize(true);

        dialog.findViewById(R.id.text_bonificacao_total);
        dialog.findViewById(R.id.text_penalizacao_total);

        ((TextView) dialog.findViewById(R.id.text_tecnico)).setText("Técnico Responsável: " + abateListado.getTecnico().getNome());
        ((TextView) dialog.findViewById(R.id.text_data)).setText("Data: " + abateListado.getDataAbate());
        ((TextView) dialog.findViewById(R.id.txt_proprietario)).setText(abateListado.getLote().getNomeCliente());
        ((TextView) dialog.findViewById(R.id.txt_propriedade)).setText(abateListado.getLote().getPropriedade());
        ((TextView) dialog.findViewById(R.id.txt_frigorifico)).setText("Frigorífico: " + abateListado.getFrigorifico());
        ((TextView) dialog.findViewById(R.id.txt_qtd_animais)).setText("Quantidade: " + abateListado.getLote().getQtdeAnimaisLote());
        ((TextView) dialog.findViewById(R.id.txt_categoria)).setText("Categoria: " + abateListado.getCategoria().getCategoria() + " - " + abateListado.getCategoria().getRacial());

        int somaBezerros = (Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosGrandes()) + Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosMedios()) + Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosPequenos()));
        if (somaBezerros > 0) {
            ((TextView) dialog.findViewById(R.id.titulo_bezerros)).setVisibility(View.VISIBLE);
            if (Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosPequenos()) > 0) {
                ((TextView) dialog.findViewById(R.id.txt_bz_pequeno)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.txt_bz_pequeno)).setText("Pequeno: " + abateListado.getCategoria().getQtdeBezerrosPequenos());
            } else {
                ((TextView) dialog.findViewById(R.id.txt_bz_pequeno)).setVisibility(View.GONE);
                ((View) dialog.findViewById(R.id.espaco_bezerros)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosMedios()) > 0) {
                ((TextView) dialog.findViewById(R.id.txt_bz_medio)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.txt_bz_medio)).setText("Médio: " + abateListado.getCategoria().getQtdeBezerrosMedios());
            } else {
                ((TextView) dialog.findViewById(R.id.txt_bz_medio)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getCategoria().getQtdeBezerrosGrandes()) > 0) {
                ((TextView) dialog.findViewById(R.id.txt_bz_grande)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.txt_bz_grande)).setText("Grande: " + abateListado.getCategoria().getQtdeBezerrosGrandes());
            } else {
                ((TextView) dialog.findViewById(R.id.txt_bz_grande)).setVisibility(View.GONE);
            }
        } else if (abateListado.getCategoria().getCategoria().equals("Vacas") || abateListado.getCategoria().getCategoria().equals("Novilhas") || abateListado.getCategoria().getCategoria().equals("Vacas e Novilhas")) {
            ((TextView) dialog.findViewById(R.id.titulo_bezerros)).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.txt_nenhum_bezerro)).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.txt_bz_pequeno)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_bz_medio)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_bz_grande)).setVisibility(View.GONE);
        } else {
            ((TextView) dialog.findViewById(R.id.titulo_bezerros)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_bz_pequeno)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_bz_medio)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_bz_grande)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_nenhum_bezerro)).setVisibility(View.GONE);
        }

        if (!abateListado.getRendimento().getPesoFazendaKilo().equals("0.0")) {
            ((TextView) dialog.findViewById(R.id.txt_peso_fazenda)).setText("Peso fazenda: " + (formatDecimal(Double.valueOf(abateListado.getRendimento().getPesoFazendaKilo())) + "kg (" + (formatDecimal(Double.valueOf(abateListado.getRendimento().getPesoFazendaArroba()))) + "@)"));
        } else {
            ((TextView) dialog.findViewById(R.id.txt_peso_fazenda)).setVisibility(View.GONE);
        }

        if (!abateListado.getRendimento().getPesoCarcacaKilo().equals("0.0")) {
            ((TextView) dialog.findViewById(R.id.txt_peso_carcaca)).setText("Peso carcaça: " + (formatDecimal(Double.valueOf(abateListado.getRendimento().getPesoCarcacaKilo())) + "kg (" + (formatDecimal(Double.valueOf(abateListado.getRendimento().getPesoCarcacaArroba()))) + "@)"));
        } else {
            ((TextView) dialog.findViewById(R.id.txt_peso_carcaca)).setVisibility(View.GONE);
        }

        if (!abateListado.getRendimento().getRendimentoCarcaça().equals("0.0")) {
            ((TextView) dialog.findViewById(R.id.txt_rendimento)).setText("Rendimento: " + (formatDecimal(Double.valueOf(abateListado.getRendimento().getRendimentoCarcaça()))) + "%");
        } else {
            ((TextView) dialog.findViewById(R.id.txt_rendimento)).setVisibility(View.GONE);
        }

        if (!abateListado.getRendimento().getRendimentoEstimado().equals("-")) {
            ((TextView) dialog.findViewById(R.id.txt_rend_estimado)).setText("Rendimento: " + (formatDecimal(Double.valueOf(abateListado.getRendimento().getRendimentoEstimado().replace("%", "")))) + "%");
        } else {
            ((TextView) dialog.findViewById(R.id.txt_rend_estimado)).setVisibility(View.GONE);
        }

        int somaAcabamento = (Integer.parseInt(abateListado.getAcabamento().getQtdeAusente()) + Integer.parseInt(abateListado.getAcabamento().getQtdeEscasso()) + Integer.parseInt(abateListado.getAcabamento().getQtdeEscassoMenos()) + Integer.parseInt(abateListado.getAcabamento().getQtdeMediano()) + Integer.parseInt(abateListado.getAcabamento().getQtdeUniforme()) + Integer.parseInt(abateListado.getAcabamento().getQtdeExcessivo()));
        if (somaAcabamento > 0) {
            ((TableLayout) dialog.findViewById(R.id.tab_acabamento)).setVisibility(View.VISIBLE);

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeAusente()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_ausente)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_ausente)).setText(abateListado.getAcabamento().getQtdeAusente());
                ((TextView) dialog.findViewById(R.id.perc_ausente)).setText(arredondaValor(abateListado.getAcabamento().getPercentualAusente().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_ausente)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeEscassoMenos()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_escasso_menos)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_escasso_menos)).setText(abateListado.getAcabamento().getQtdeEscassoMenos());
                ((TextView) dialog.findViewById(R.id.perc_escasso_menos)).setText(arredondaValor(abateListado.getAcabamento().getPercentualEscassoMenos().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_escasso_menos)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeEscasso()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_escasso)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_escasso)).setText(abateListado.getAcabamento().getQtdeEscasso());
                ((TextView) dialog.findViewById(R.id.perc_escasso)).setText(arredondaValor(abateListado.getAcabamento().getPercentualEscasso().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_escasso)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeMediano()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_mediano)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_mediano)).setText(abateListado.getAcabamento().getQtdeMediano());
                ((TextView) dialog.findViewById(R.id.perc_mediano)).setText(arredondaValor(abateListado.getAcabamento().getPercentualMediano().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_mediano)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeUniforme()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_Uniforme)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_uniforme)).setText(abateListado.getAcabamento().getQtdeUniforme());
                ((TextView) dialog.findViewById(R.id.perc_uniforme)).setText(arredondaValor(abateListado.getAcabamento().getPercentualUniforme().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_Uniforme)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getAcabamento().getQtdeExcessivo()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_excessivo)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_excessivo)).setText(abateListado.getAcabamento().getQtdeExcessivo());
                ((TextView) dialog.findViewById(R.id.perc_excessivo)).setText(arredondaValor(abateListado.getAcabamento().getPercentualExcessivo().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_excessivo)).setVisibility(View.GONE);
            }

        } else {
            ((TableLayout) dialog.findViewById(R.id.tab_acabamento)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_acabamento)).setVisibility(View.GONE);
        }

        int somaMaturidade = (Integer.parseInt(abateListado.getMaturidade().getQtdeZeroDentes()) + Integer.parseInt(abateListado.getMaturidade().getQtdeDoisDentes()) + Integer.parseInt(abateListado.getMaturidade().getQtdeQuatroDentes()) + Integer.parseInt(abateListado.getMaturidade().getQtdeSeisDentes()) + Integer.parseInt(abateListado.getMaturidade().getQtdeOitoDentes()));
        if (somaMaturidade > 0) {
            ((TableLayout) dialog.findViewById(R.id.tab_maturidade)).setVisibility(View.VISIBLE);

            if (Integer.parseInt(abateListado.getMaturidade().getQtdeZeroDentes()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_0_dentes)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_0_dentes)).setText(abateListado.getMaturidade().getQtdeZeroDentes());
                ((TextView) dialog.findViewById(R.id.perc_0_dentes)).setText(arredondaValor(abateListado.getMaturidade().getPercentualZeroDentes().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_0_dentes)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getMaturidade().getQtdeDoisDentes()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_2_dentes)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_2_dentes)).setText(abateListado.getMaturidade().getQtdeDoisDentes());
                ((TextView) dialog.findViewById(R.id.perc_2_dentes)).setText(arredondaValor(abateListado.getMaturidade().getPercentualDoisDentes().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_2_dentes)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getMaturidade().getQtdeQuatroDentes()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_4_dentes)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_4_dentes)).setText(abateListado.getMaturidade().getQtdeQuatroDentes());
                ((TextView) dialog.findViewById(R.id.perc_4_dentes)).setText(arredondaValor(abateListado.getMaturidade().getPercentualQuatroDentes().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_4_dentes)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getMaturidade().getQtdeSeisDentes()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_6_dentes)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_6_dentes)).setText(abateListado.getMaturidade().getQtdeSeisDentes());
                ((TextView) dialog.findViewById(R.id.perc_6_dentes)).setText(arredondaValor(abateListado.getMaturidade().getPercentualSeisDentes().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_6_dentes)).setVisibility(View.GONE);
            }

            if (Integer.parseInt(abateListado.getMaturidade().getQtdeOitoDentes()) > 0) {
                ((TableRow) dialog.findViewById(R.id.linha_8_dentes)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.qtd_8_dentes)).setText(abateListado.getMaturidade().getQtdeOitoDentes());
                ((TextView) dialog.findViewById(R.id.perc_8_dentes)).setText(arredondaValor(abateListado.getMaturidade().getPercentualOitoDentes().replaceAll("%", "")) + "%");
            } else {
                ((TableRow) dialog.findViewById(R.id.linha_8_dentes)).setVisibility(View.GONE);
            }

        } else {
            ((TableLayout) dialog.findViewById(R.id.tab_maturidade)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_maturidade)).setVisibility(View.GONE);
        }

        if (!abateListado.getAcerto().getTotalLiquido().equals("") || !abateListado.getAcerto().getArrobaNegociada().equals("")) {
            ((TextView) dialog.findViewById(R.id.titulo_acerto)).setVisibility(View.VISIBLE);
            ((View) dialog.findViewById(R.id.espaco_acerto)).setVisibility(View.VISIBLE);

            if (!abateListado.getAcerto().getTotalLiquido().equals("")) {
                if (!abateListado.getAcerto().getArrobaNegociada().equals("")) {
                    ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setText("@ Negociada: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getArrobaNegociada())));
                }

                if (!abateListado.getAcerto().getTotalBruto().equals("")) {
                    ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setText("Total Bruto: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getTotalBruto())));
                }

                if (!abateListado.getAcerto().getTotalLiquido().equals("")) {
                    ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setText("Total Líquido: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getTotalLiquido())));
                }

                if (!abateListado.getAcerto().getArrobaRecebidaComFunrural().equals("0.0")) {
                    ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setText("Valor da @ com Funrural: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getArrobaRecebidaComFunrural())));
                }

                if (!abateListado.getAcerto().getArrobaRecebida().equals("")) {
                    ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setText("@ Recebida: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getArrobaRecebida())));
                }

                ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.VISIBLE);

                if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("A prazo")) {
                    ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo() + " (" + abateListado.getAcerto().getDias() + " dias)");
                } else if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("À vista")) {
                    ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo());
                }

            } else {
                if (!abateListado.getAcerto().getArrobaNegociada().equals("")) {
                    ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setText("@ Negociada: R$ " + formatDecimal(Double.valueOf(abateListado.getAcerto().getArrobaNegociada())));

                    if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("A prazo")) {
                        ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.VISIBLE);
                        ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo() + " (" + abateListado.getAcerto().getDias() + " dias)");

                        ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.GONE);

                    } else if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("À vista")) {
                        ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.VISIBLE);
                        ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo());

                        ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.GONE);
                        ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.GONE);
                    }
                }
            }
        } else if (!(abateListado.getAcerto().getPrazo() == null) && !(abateListado.getAcerto().getPrazo().equals(""))) {
            ((TextView) dialog.findViewById(R.id.titulo_acerto)).setVisibility(View.VISIBLE);
            ((View) dialog.findViewById(R.id.espaco_acerto)).setVisibility(View.VISIBLE);

            if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("A prazo")) {
                ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo() + " (" + abateListado.getAcerto().getDias() + " dias)");

                ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setVisibility(View.GONE);

            } else if (!(abateListado.getAcerto().getPrazo() == null) && abateListado.getAcerto().getPrazo().equals("À vista")) {
                ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.VISIBLE);
                ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setText("Forma de pagamento: " + abateListado.getAcerto().getPrazo());

                ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setVisibility(View.GONE);
            }
        } else {
            ((TextView) dialog.findViewById(R.id.titulo_acerto)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_arroba_negociada)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_total_bruto)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_total_liquido)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_arroba_com_funrural)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_arroba_recebida)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_forma_pagamento)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_acerto)).setVisibility(View.GONE);
        }

        if (!abateListado.getObservacoes().equals("")) {
            ((TextView) dialog.findViewById(R.id.titulo_observacoes)).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.txt_observacoes)).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.txt_observacoes)).setText(abateListado.getObservacoes());
            ((View) dialog.findViewById(R.id.espaco_observacoes)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) dialog.findViewById(R.id.titulo_observacoes)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.txt_observacoes)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_observacoes)).setVisibility(View.GONE);
        }

        if (abateListado.getBonificacoes().size() > 0) {
            ((TextView) dialog.findViewById(R.id.titulo_bonificacao)).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.text_bonificacao_total)).setVisibility(View.VISIBLE);

            String bonificacaoTotal = atualizaTotalBonificacao(abateListado);

            if (!bonificacaoTotal.equals(" - ")) {
                ((TextView) dialog.findViewById(R.id.text_bonificacao_total)).setText(bonificacaoTotal);
            } else {
                ((TextView) dialog.findViewById(R.id.text_bonificacao_total)).setVisibility(View.GONE);
            }
            ((RecyclerView) dialog.findViewById(R.id.list_view_bonificacao)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) dialog.findViewById(R.id.titulo_bonificacao)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.text_bonificacao_total)).setVisibility(View.GONE);
            ((RecyclerView) dialog.findViewById(R.id.list_view_bonificacao)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_bonificacao)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_bonificacao1)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_bonificacao2)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_bonificacao_total)).setVisibility(View.GONE);
        }

//        adapterBonificacao = new AdapterBonificacoesPersonalizado(abateListado.getBonificacoes(), getActivity());

        bonificacaoAdapter = new BonificacaoAdapter(abateListado.getBonificacoes());
        recyclerViewBonificacao.setAdapter(bonificacaoAdapter);
//        setListViewHeightBasedOnChildren(recyclerViewBonificacao);

        if (abateListado.getPenalizacoes().size() > 0) {
            ((TextView) dialog.findViewById(R.id.titulo_penalizacao)).setVisibility(View.VISIBLE);

            String penalizacaoTotal = atualizaTotalPenalizacao(abateListado);

            if (!penalizacaoTotal.equals(" - ")) {
                ((TextView) dialog.findViewById(R.id.text_penalizacao_total)).setText(penalizacaoTotal);
            } else {
                ((TextView) dialog.findViewById(R.id.text_penalizacao_total)).setVisibility(View.GONE);
            }
            ((RecyclerView) dialog.findViewById(R.id.list_view_penalizacao)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) dialog.findViewById(R.id.titulo_penalizacao)).setVisibility(View.GONE);
            ((TextView) dialog.findViewById(R.id.text_penalizacao_total)).setVisibility(View.GONE);
            ((RecyclerView) dialog.findViewById(R.id.list_view_penalizacao)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_penalizacao)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_penalizacao2)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_penalizacao1)).setVisibility(View.GONE);
            ((View) dialog.findViewById(R.id.espaco_penalizacao_total)).setVisibility(View.GONE);
        }

        imagens.child(abateListado.getFotoLote() + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    image_lote.setVisibility(View.VISIBLE);
//            Uri url = abate.getFotoLote();

//            if (url != null) {

                    GlideApp.with(getActivity().getApplicationContext())
                            .load(uri.toString())
                            .into(image_lote);
                } else {
                    image_lote.setImageResource(R.drawable.padrao_boi);
                    image_lote.setVisibility(View.GONE);
                }
            }
        });

//        adapterPenalizacao = new AdapterPenalizacoesPersonalizado(abateListado.getPenalizacoes(), getActivity());
        penalizacaoAdapter = new PenalizacaoAdapter(abateListado.getPenalizacoes());
        recyclerViewPenalizacao.setAdapter(penalizacaoAdapter);
//        setListViewHeightBasedOnChildren(recyclerViewPenalizacao);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        ((AppCompatButton) dialog.findViewById(R.id.bt_follow)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Follow Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

//        public void carregaImagemDoLote(Bitmap imagemLote) {
//
//            try {
//                if (imagemLote != null) {
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    imagemLote.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    imageLote = Image.getInstance(stream.toByteArray());
//                    imageLote.scaleToFit(525, 525);
//                }
//            } catch (BadElementException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    private LinearLayoutManager newLLM() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    private String atualizaTotalPenalizacao(Abate abate) {
        Double totalPenalizacao = 0.0;
        Double mediaTotalLote = 0.0;
        Double pesoTotalDoLote = 0.0;

        for (Penalizacao penalizacaoRetornada : abate.getPenalizacoes()) {
            if (penalizacaoRetornada.getTotal() != null)
                totalPenalizacao += Double.valueOf(penalizacaoRetornada.getTotal());
        }

        if (!totalPenalizacao.equals(0.0)) {
            totalPenalizacao = Double.valueOf(String.format(Locale.US, "%.2f", totalPenalizacao));
            pesoTotalDoLote = Double.parseDouble(abate.getRendimento().getPesoCarcacaArroba()) * Integer.parseInt(abate.getLote().getQtdeAnimaisLote());

            mediaTotalLote = totalPenalizacao / pesoTotalDoLote;
            mediaTotalLote = Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalLote));

            return ("Penalização total: R$" + formatDecimal(totalPenalizacao) + " (- R$ " + mediaTotalLote + "/@)");
        } else
            return " - ";
    }

    private String atualizaTotalBonificacao(Abate abate) {
        Double totalBonificacao = 0.0;
        Double mediaTotalLote = 0.0;
        Double pesoTotalDoLote = 0.0;

        for (Bonificacao bonificacaoRetornada : abate.getBonificacoes()) {
            if (bonificacaoRetornada.getTotal() != null)
                totalBonificacao += Double.valueOf(bonificacaoRetornada.getTotal());
        }

        if (!totalBonificacao.equals(0.0)) {
            totalBonificacao = Double.valueOf(String.format(Locale.US, "%.2f", totalBonificacao));
            pesoTotalDoLote = Double.parseDouble(abate.getRendimento().getPesoCarcacaArroba()) * Integer.parseInt(abate.getLote().getQtdeAnimaisLote());

            mediaTotalLote = totalBonificacao / pesoTotalDoLote;
            mediaTotalLote = Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalLote));

            return ("Bonificação total: R$" + formatDecimal(totalBonificacao) + " (+ R$ " + mediaTotalLote + "/@)");
        } else
            return " - ";
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    GuestConstants.PERMISSIONS.PERMISSIONS_STORAGE,
                    GuestConstants.PERMISSIONS.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void swipe() {

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//                Log.i("swipe", "Item foi arrastado");
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewListaAbates);
    }

    private void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        //Configurar o Alert
        alertDialog.setTitle("Excluir Abate");
        alertDialog.setIcon(R.drawable.absent);
        alertDialog.setMessage("Você tem certeza que deseja excluir este abate da lista de abates?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                abate = abates.get(position);

                abateRef.child(abate.getId()).removeValue();
                adapter.notifyItemRemoved(position);

                imageRef = imagens
                        .child(abate.getFotoLote() + ".jpeg");

                imageRef.delete().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("storage", "Imagem excluída com sucesso do Firebase Storage!");
                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show();

                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void recuperarAbates() {
        valueEventListenerAbates = abateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                abates.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Abate abate = dados.getValue(Abate.class);
                    abate.setId(dados.getKey());
                    abates.add(abate);
                }

//                Collections.sort(abates, Collections.reverseOrder());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lista_abates, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_pesquisa);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar Abates");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_pesquisa) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            String textoDigitado = newText.toUpperCase();
            pesquisarAbates(newText);
        } else {
            recuperarAbates();
            lyt_no_result.setVisibility(View.GONE);
        }
        return true;
    }

    private void pesquisarAbates(String textoDigitado) {

        lyt_no_result.setVisibility(View.GONE);

        //Limpar lista de abates
        abates.clear();

        //Pesquisa abates caso tenha texto na pesquisa
        if (textoDigitado.length() > 0) {

            Query queryFrigorifico = abateRef.orderByChild("frigorifico")
                    .startAt(textoDigitado.toUpperCase())
                    .endAt(textoDigitado.toLowerCase() + "\uf8ff");

            Query queryNomeCliente = abateRef.orderByChild("lote/nomeCliente")
                    .startAt(textoDigitado)
                    .endAt(textoDigitado + "\uf8ff");

            queryNomeCliente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Limpar a lista
                    abates.clear();

                    if (!dataSnapshot.exists()) {
                        lyt_no_result.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            abates.add(ds.getValue(Abate.class));
                        }

//                    int total = abates.size();
//                    Log.i("totalAbates", "Total de abates: " + total);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
