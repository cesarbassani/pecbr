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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.adapter.ListaAbatesAdapter;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.constants.DataBaseConstants;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.helper.PDFHelper;
import com.cesarbassani.pecbr.listener.OnAbateListenerInteractionListener;
import com.cesarbassani.pecbr.listener.RecyclerItemClickListener;
import com.cesarbassani.pecbr.model.Abate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ListaAbatesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerViewListaAbates;
    private ListaAbatesAdapter adapter;
    private Context context;
    private ArrayList<Abate> abates = new ArrayList<>();
    private DatabaseReference abateRef;
    private ValueEventListener valueEventListenerAbates;
    private Abate abate;
    private  Abate abateListado;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private LinearLayout lyt_no_result;

    private StorageReference storageReference;
    private StorageReference imagens;
    private StorageReference imageRef;
    private ContentResolver contentResolver;

    private Bitmap imagemLote;
    private Uri path;

    private OnAbateListenerInteractionListener mOnAbateListenerInteractionListener;

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

        swipe();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarAbates();
    }

    @Override
    public void onStop() {
        super.onStop();
        abateRef.removeEventListener(valueEventListenerAbates);
    }

    private void initComponent(View view) {

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.show();
        }

        contentResolver = getActivity().getContentResolver();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        imagens = storageReference.child("imagens").child("lote");

        abateRef = ConfiguracaoFirebase.getFirebaseDatabase().child("abates");

        lyt_no_result = view.findViewById(R.id.lyt_no_result);

        recyclerViewListaAbates = view.findViewById(R.id.recycler_lista_abates);
        LinearLayoutManager layout_manager = new LinearLayoutManager(context);
        layout_manager.setReverseLayout(true);
        layout_manager.setStackFromEnd(true);
        recyclerViewListaAbates.setLayoutManager(layout_manager);
        recyclerViewListaAbates.setHasFixedSize(true);

        adapter = new ListaAbatesAdapter(abates, context);
        recyclerViewListaAbates.setAdapter(adapter);

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
//
//        ((TextView) dialog.findViewById(R.id.title)).setText(p.name);
//        ((CircleImageView) dialog.findViewById(R.id.image)).setImageResource(p.image);

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
