package com.cesarbassani.pecbr.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.model.Cotacao;
import com.cesarbassani.pecbr.utils.Tools;
import com.cesarbassani.pecbr.utils.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.support.constraint.Constraints.TAG;
import static com.cesarbassani.pecbr.utils.Tools.formatDecimal;

public class CotacoesFragment extends Fragment {

    private ImageButton bt_toggle_items, bt_toggle_address, bt_toggle_description, bt_nova_cotacao, datePickerNovaCotacao, datePickerCotacao;
    private View lyt_expand_items, lyt_expand_address, lyt_expand_description;
    private NestedScrollView nested_scroll_view;
    private Cotacao cotacao = new Cotacao();
    private String dataCotacao;
    private TextView bezerroAPrazo, bezerroAVista, boiGordoAPrazo, boiGordoAVista, boiMagroAPrazo, boiMagroAVista, vacaGordaAPrazo, vacaGordaAVista, txtDataCotacao, dataCotacaoResult;
    private EditText edit_bezerro_a_prazo, edit_bezerro_a_vista, edit_boi_gordo_a_prazo, edit_boi_gordo_a_vista, edit_boi_magro_a_prazo, edit_boi_magro_a_vista, edit_vaca_gorda_a_prazo, edit_vaca_gorda_a_vista;
    private DatabaseReference cotacaoRef;
    private ValueEventListener valueEventListenerCotacoes;
    private boolean cotacaoEncontrada = false;
    private AppCompatSpinner spnCotacoes;
    private ArrayList<String> cotacoes = new ArrayList<>();
    private ArrayAdapter<String> cotacoesAdapter;
    private Context context;
    private Button btn_consultar_cotacao;

    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cotacoes, container, false);

        context = view.getContext();

//        initToolbar(view);
        initComponent(view);

        return view;
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarMain);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(getActivity(), R.color.colorPrimary);
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

    private void initComponent(View view) {

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        cotacaoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cotacoes");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }

        // nested scrollview
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        bezerroAPrazo = view.findViewById(R.id.bezerro_a_prazo);
        bezerroAVista = view.findViewById(R.id.bezerro_a_vista);
        boiGordoAPrazo = view.findViewById(R.id.boi_gordo_a_prazo);
        boiGordoAVista = view.findViewById(R.id.boi_gordo_a_vista);
        boiMagroAPrazo = view.findViewById(R.id.boi_magro_a_prazo);
        boiMagroAVista = view.findViewById(R.id.boi_magro_a_vista);
        vacaGordaAPrazo = view.findViewById(R.id.vaca_gorda_a_prazo);
        vacaGordaAVista = view.findViewById(R.id.vaca_gorda_a_vista);
        txtDataCotacao = view.findViewById(R.id.dataCotacao);

        recuperarCotacoes();

        bt_nova_cotacao = view.findViewById(R.id.bt_nova_cotacao);
        RelativeLayout layout_bt_nova_cotacao = view.findViewById(R.id.layout_bt_nova_cotacao);

        //check already session, if ok-> Dashboard
        if (user.getEmail().equals("joaoalvaro@pecbr.com") || user.getEmail().equals("cesarbassani.dev@gmail.com")) {
            layout_bt_nova_cotacao.setVisibility(View.VISIBLE);
        }

        datePickerCotacao = view.findViewById(R.id.datePickerCotacao);

        datePickerCotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialogDatePickerLight((ImageButton) view);
                showConsultaCotacaoDialog();
            }
        });

        // section items
        bt_toggle_items = (ImageButton) view.findViewById(R.id.bt_toggle_items);
        lyt_expand_items = (View) view.findViewById(R.id.lyt_expand_items);
        bt_toggle_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_items);
            }
        });

        // copy to clipboard
        final TextView tv_invoice_code = (TextView) view.findViewById(R.id.tv_invoice_code);
        bt_nova_cotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCotacaoDialog();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void showConsultaCotacaoDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_consulta_cotacao);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        spnCotacoes = dialog.findViewById(R.id.spn_cotacoes);
        btn_consultar_cotacao = dialog.findViewById(R.id.btn_consultar_cotacao);

        cotacoesAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.simple_spinner_item, cotacoes);
        cotacoesAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spnCotacoes.setAdapter(cotacoesAdapter);
        spnCotacoes.setSelection(0);

        atualizaSpinnerCotacoes(cotacoes);

        spnCotacoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        dialog.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//            }
//        });

        btn_consultar_cotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDataCotacao.setText(spnCotacoes.getSelectedItem().toString());

                recuperarCotacoes();

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarCotacoes();
    }

    @Override
    public void onStop() {
        super.onStop();
        cotacaoRef.removeEventListener(valueEventListenerCotacoes);
    }

    public void recuperarCotacoes() {

        cotacaoEncontrada = false;

        valueEventListenerCotacoes = cotacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                cotacoes.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Cotacao cotacaoBuscada = dados.getValue(Cotacao.class);
                    cotacaoBuscada.setId(dados.getKey());
                    cotacoes.add(cotacaoBuscada.getDataCotacao());

                    if (cotacaoBuscada.getDataCotacao().equals(txtDataCotacao.getText().toString())) {
                        cotacao = cotacaoBuscada;
                        cotacaoEncontrada = true;
                    }
                }

                if (cotacoesAdapter != null) {
                    cotacoesAdapter.notifyDataSetChanged();
//                    atualizaSpinnerCotacoes(cotacoes);
                }

                if (cotacaoEncontrada) {
                    bezerroAPrazo.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBezerroAPrazo())))));
                    bezerroAVista.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBezerroAVista())))));
                    boiGordoAPrazo.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBoiGordoAPrazo())))));
                    boiGordoAVista.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBoiGordoAVista())))));
                    boiMagroAPrazo.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBoiMagroAPrazo())))));
                    boiMagroAVista.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getBoiMagroAVista())))));
                    vacaGordaAPrazo.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getVacaGordaAPrazo())))));
                    vacaGordaAVista.setText(validaCampoZerado(formatDecimal(Double.valueOf(validaCampoNulo(cotacao.getVacaGordaAVista())))));
                    txtDataCotacao.setText(cotacao.getDataCotacao());
                } else {
                    limparCampos();
                    txtDataCotacao.setText(cotacoes.get(cotacoes.size()-1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void atualizaSpinnerCotacoes(ArrayList<String> cotacoes) {
        if (cotacao.getDataCotacao() != null) {
            int posicaoArray = 0;

            for (int i = 0; (i <= cotacoes.size() - 1); i++) {

                if (cotacoes.get(i).equals(cotacao.getDataCotacao())) {
                    posicaoArray = i;
                    break;
                } else {
                    posicaoArray = cotacoes.size()-1;
                }
            }
            spnCotacoes.setSelection(posicaoArray);
        } else {
            spnCotacoes.setSelection(cotacoes.size()-1);
        }
    }

    private void limparCampos() {
        bezerroAPrazo.setText(" - ");
        bezerroAVista.setText(" - ");
        boiGordoAPrazo.setText(" - ");
        boiGordoAVista.setText(" - ");
        boiMagroAPrazo.setText(" - ");
        boiMagroAVista.setText(" - ");
        vacaGordaAPrazo.setText(" - ");
        vacaGordaAVista.setText(" - ");
    }

    private void pesquisarCotacao(String dataCotacao) {

        Query queryCotacao = cotacaoRef.child("cotacoes").equalTo(dataCotacao);

        queryCotacao.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    cotacao = ds.getValue(Cotacao.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String validaCampoZerado(String valor) {

        if (valor.equals("0,00")) {
            valor = " - ";
        } else {
            valor = "R$ " + valor;
        }

        return valor;
    }

    private String validaCampoNulo(String valor) {

        if (valor.equals("")) {
            valor = "0.0";
        }

        return valor;
    }

    private void showCotacaoDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_cotacao);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edit_bezerro_a_prazo = dialog.findViewById(R.id.edit_bezerro_a_prazo);
        edit_bezerro_a_vista = dialog.findViewById(R.id.edit_bezerro_a_vista);
        edit_boi_gordo_a_prazo = dialog.findViewById(R.id.edit_boi_gordo_a_prazo);
        edit_boi_gordo_a_vista = dialog.findViewById(R.id.edit_boi_gordo_a_vista);
        edit_boi_magro_a_prazo = dialog.findViewById(R.id.edit_boi_magro_a_prazo);
        edit_boi_magro_a_vista = dialog.findViewById(R.id.edit_boi_magro_a_vista);
        edit_vaca_gorda_a_prazo = dialog.findViewById(R.id.edit_vaca_gorda_a_prazo);
        edit_vaca_gorda_a_vista = dialog.findViewById(R.id.edit_vaca_gorda_a_vista);
        dataCotacaoResult = dialog.findViewById(R.id.dataCotacaoResult);

        dataCotacaoResult.setText(Tools.getFormattedDateSimple(new Date().getTime()));

        datePickerNovaCotacao = dialog.findViewById(R.id.datePicker);

        datePickerNovaCotacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerLight((ImageButton) view);
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        ((Button) dialog.findViewById(R.id.bt_save)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        EventCotacao eventCotacao = new EventCotacao();
//                        event.categoria = spn_categoria.getSelectedItem().toString();
//                        event.racial = spn_racial.getSelectedItem().toString();
//
//                        if (check_bezerros.isChecked()) {
//                            event.qtdGrande = edt_grande.getText().toString();
//                            event.qtdMedio = edt_medio.getText().toString();
//                            event.qtdPequeno = edt_pequeno.getText().toString();
//
//                        } else {
//                            event.qtdGrande = "0";
//                            event.qtdMedio = "0";
//                            event.qtdPequeno = "0";
//                        }

//                        displayDataResult(event);

                        Cotacao cotacaoGerada = inicializaCotacao();

                        if (cotacaoGerada.getId() == null) {
                            cotacaoGerada.salvar();
                            txtDataCotacao.setText(cotacaoGerada.getDataCotacao());
                        } else {
                            cotacaoGerada.atualizar();
                        }

                        dialog.dismiss();
                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

//    private void displayDataResult(Event event) {
//        this.mViewHolder.txtCategoria.setText(event.categoria);
//        this.mViewHolder.txtRacial.setText(event.racial);
//
//        if ((!event.qtdGrande.equals(""))) {
//            ((TextView) findViewById(R.id.tv_bezerros_grande)).setText(event.qtdGrande);
//        }
//
//        if ((!event.qtdMedio.equals(""))) {
//            ((TextView) findViewById(R.id.tv_bezerros_medio)).setText(event.qtdMedio);
//        }
//
//        if ((!event.qtdPequeno.equals(""))) {
//            ((TextView) findViewById(R.id.tv_bezerros_pequeno)).setText(event.qtdPequeno);
//        }
//    }

    private void dialogDatePickerLight(final ImageButton bt) {
        Calendar cur_calender = Calendar.getInstance();
        final DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        long date_ship_millis = calendar.getTimeInMillis();

                        if (bt.getId() == R.id.datePicker) {
                            dataCotacaoResult.setText(Tools.getFormattedDateSimple(date_ship_millis));
                        } else {
                            txtDataCotacao.setText(Tools.getFormattedDateSimple(date_ship_millis));
                            recuperarCotacoes();

                        }
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        Calendar calendarMin = new GregorianCalendar(2019, Calendar.JANUARY, 10);
        datePicker.setMinDate(calendarMin);
        datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private Cotacao inicializaCotacao() {

        Cotacao cotacaoSalva = new Cotacao();

        cotacaoSalva.setBezerroAPrazo(edit_bezerro_a_prazo.getText().toString().trim());
        cotacaoSalva.setBezerroAVista(edit_bezerro_a_vista.getText().toString().trim());
        cotacaoSalva.setBoiGordoAPrazo(edit_boi_gordo_a_prazo.getText().toString().trim());
        cotacaoSalva.setBoiGordoAVista(edit_boi_gordo_a_vista.getText().toString().trim());
        cotacaoSalva.setBoiMagroAPrazo(edit_boi_magro_a_prazo.getText().toString().trim());
        cotacaoSalva.setBoiMagroAVista(edit_boi_magro_a_vista.getText().toString().trim());
        cotacaoSalva.setVacaGordaAPrazo(edit_vaca_gorda_a_prazo.getText().toString().trim());
        cotacaoSalva.setVacaGordaAVista(edit_vaca_gorda_a_vista.getText().toString().trim());

//        if (spnCotacoes.getSelectedItem() != null)
//            dataCotacao = (String) spnCotacoes.getSelectedItem();

        cotacaoSalva.setDataCotacao(dataCotacaoResult.getText().toString());
//        cotacaoSalva.setDataCotacao(dataCotacaoResult.getText().toString());

        return cotacaoSalva;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
