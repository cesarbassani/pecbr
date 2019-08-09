package com.cesarbassani.pecbr.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.adapter.AdapterBonificacoesPersonalizado;
import com.cesarbassani.pecbr.adapter.AdapterParciaisBonificacoesPersonalizado;
import com.cesarbassani.pecbr.adapter.AdapterParciaisPenalizacoesPersonalizado;
import com.cesarbassani.pecbr.adapter.AdapterPenalizacoesPersonalizado;
import com.cesarbassani.pecbr.business.GuestBusiness;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.constants.DataBaseConstants;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.helper.Permissao;
import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.model.Acabamento;
import com.cesarbassani.pecbr.model.Acerto;
import com.cesarbassani.pecbr.model.Bonificacao;
import com.cesarbassani.pecbr.model.Categoria;
import com.cesarbassani.pecbr.model.Cliente;
import com.cesarbassani.pecbr.model.Event;
import com.cesarbassani.pecbr.model.Frigorifico;
import com.cesarbassani.pecbr.model.GuestEntity;
import com.cesarbassani.pecbr.model.Lote;
import com.cesarbassani.pecbr.model.Maturidade;
import com.cesarbassani.pecbr.model.ParcialBonificacao;
import com.cesarbassani.pecbr.model.ParcialPenalizacao;
import com.cesarbassani.pecbr.model.Penalizacao;
import com.cesarbassani.pecbr.model.Propriedade;
import com.cesarbassani.pecbr.model.Rendimento;
import com.cesarbassani.pecbr.model.Usuario;
import com.cesarbassani.pecbr.repository.TemplatePDF;
import com.cesarbassani.pecbr.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.DocumentException;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import pl.utkala.searchablespinner.SearchableSpinner;

import static android.support.constraint.Constraints.TAG;
import static android.widget.Toast.LENGTH_SHORT;

public class AbateFormActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemClickListener {

    private static final int CALCULOPESOFAZENDA = 30;
    private static final int CALCULOPESOCARCACA = 15;
    private static final Double FUNRURAL = 0.015;

    private final ViewHolder mViewHolder = new ViewHolder();

    private TemplatePDF templatePDF;

    private GuestBusiness mGuestBusiness;
    private String dateString;

    private Typeface typeFace_date;
    private Typeface typeFace_title;

    private int mGuestID = 0;

    private String[] header = {"Id", "Nombre", "Apelido"};
    private String nomeFazendeiro;
    private String nomeFazenda;
    private NestedScrollView nested_scroll_view;
    private View lyt_expand_pasto;
    private View lyt_expand_confinamento;
    private AppCompatSeekBar seekbar_primary_light;
    private AppCompatRadioButton check_grao_inteiro;
    private AppCompatRadioButton check_convencional;
    private AppCompatSpinner spn_grao_inteiro;
    private AppCompatSpinner spn_convencional;
    private AppCompatSpinner spn_semi_confinamento;
    private AppCompatSpinner spn_pasto;
    private CheckBox check_semi_confinamento;
    private CheckBox check_ausente;
    private CheckBox check_escasso_menos;
    private CheckBox check_escasso;
    private CheckBox check_mediano;
    private CheckBox check_uniforme;
    private CheckBox check_excessivo;
    private CheckBox check_0_dentes;
    private CheckBox check_2_dentes;
    private CheckBox check_4_dentes;
    private CheckBox check_6_dentes;
    private CheckBox check_8_dentes;
    private CheckBox check_funrural;
    private CheckBox check_a_vista;
    private CheckBox check_a_prazo;

    private EditText edit_arroba_negociada_a_prazo;

    private Double pesoFazendaKilo = 0.0;
    private Double pesoCarcacaKilo = 0.0;
    private Button btn_rendimento_carcaca;
    private Button btn_dados_rendimento;
    private Button btn_maturidade;
    private Button btn_calcular_acerto;

    private List<Bonificacao> bonificacoes = new ArrayList<>();
    private List<Penalizacao> penalizacoes = new ArrayList<>();
    private AdapterBonificacoesPersonalizado adapterBonificacao;
    private AdapterPenalizacoesPersonalizado adapterPenalizacao;
    private AdapterParciaisBonificacoesPersonalizado adapterParciaisBonificacao;
    private AdapterParciaisPenalizacoesPersonalizado adapterParciaisPenalizacao;
    private ListView listViewBonificacao;
    private ListView listViewParcialBonificacao;
    private ListView listViewPenalizacao;
    private ListView listViewParcialPenalizacao;

    private Double pesoTotalDoLote = 0.0;
    private Double rendimentoEstimado = 0.0;
    private Double resultadoPesoCarcacaArroba = 0.0;
    private Double resultadoPesoFazendaArroba = 0.0;
    private Double resultadoRendimentoCarcaca = 0.0;
    private Double mediaTotalLote = 0.0;
    private Double valorTotalBonificacao = 0.0;
    private Double mediaTotalBonificacao = 0.0;
    private Double arrobaRecebidaComFunrural = 0.0;
    int qtdeAnimais = 0;

    private View parent_view;
    private List<ParcialBonificacao> parciaisBonificacao;
    private List<ParcialPenalizacao> parciaisPenalizacao;
    private EditText edit_quantidade_animais_bonificacao;
    private EditText edit_quantidade_animais_penalizacao;
    private EditText edit_peso_animais_bonificacao;
    private EditText edit_peso_animais_penalizacao;
    private EditText edit_penalizacao_descricao;
    private EditText edit_penalizacao_desconto;
    private EditText edit_valor_bonificacao;
    private EditText edit_penalizacao_observacoes;
    private EditText edt_grande;
    private EditText edt_medio;
    private EditText edt_pequeno;
    private EditText edit_acerto_total_bruto;
    private EditText edit_acerto_desconto_funrural;
    private EditText edit_acerto_percentual_antecipacao;
    private EditText edit_acerto_valor_antecipacao;
    private EditText edit_acerto_total_liquido;
    private EditText edit_arroba_recebida;
    private EditText edit_arroba_negociada;
    private EditText edit_observacoes;
    private EditText edit_bonificacao_observacoes;
    private EditText edit_bonificacao_total;
    private EditText edit_bonificacao_media_do_lote;
    private EditText edit_bonificacao_outras;

    private TextView bonificacaoTotal;
    private TextView penalizacaoTotal;
    private TextView txt_rendimento_estimado;

    private boolean desativaFunrural;
    private String prazoAcerto = "";
    private String bonificacaoObservacoes;
    private String penalizacaoObservacoes;
    private int diasAcerto = 0;
    private int somaQtdeBezerros = 0;
    private int[] qtdeAnimaisBonificados = {0};
    private boolean bonificacaoValidada = false;

    private ProgressDialog progressDialog;

    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuarios;
    private AppCompatSpinner spinnerTecnico;
    private AppCompatSpinner spinnerFrigorifico;
    //    private AppCompatSpinner spinnerCliente;
//    private AppCompatSpinner spinnerPropriedade;
    private SearchableSpinner spinnerPropriedade;
    private SearchableSpinner spinnerCliente;
    private ArrayAdapter<Usuario> usuariosAdapter;
    private ArrayAdapter<String> frigorificoAdapter;
    private ArrayAdapter<String> clienteAdapter;
    private ArrayAdapter<String> propriedadeAdapter;

    private String nomeClienteSelecionado;
    private String nomePropriedadeSelecionada;

    private Abate abate = new Abate();
    private ArrayList<String> frigorificos = new ArrayList<>();
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<String> clientesNomes = new ArrayList<>();
    private ArrayList<String> propriedadesNomes = new ArrayList<>();

    private ImageView image_lote;
    private StorageReference storageReference;
    private DatabaseReference abateRef;
    private DatabaseReference frigorificoRef;
    private DatabaseReference clienteRef;
    private DatabaseReference propriedadeRef;
    private ValueEventListener valueEventListenerFrigorificos;
    private ValueEventListener valueEventListenerClientes;
    private ValueEventListener valueEventListenerPropriedades;

    private Bitmap imagemLote;
    private boolean fotoDoAbate = false;

    double totalAnimaisAcabamento = 0.0;

    double totalAnimaisMaturidade = 0.0;

    private TextView txt_data_abate;
    private ImageButton datePickerAbate;
    private boolean frigorificoEncontrado = false;
    private boolean clienteEncontrado = false;
    private boolean propriedadeEncontrada = false;
    private ImageView adicionar_frigorifico;
    private Button adicionar_cliente;
    private Button adicionar_propriedade;
    private EditText edit_novo_frigorifico;
    private EditText edit_novo_cliente;
    private EditText edit_nova_propriedade;
    private Float pageSizeAbatePDF = 0f;

    private String nomeCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_abate_form);

        long data = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateString = sdf.format(data);

        initToolBar();

        initComponent();

        verifyStoragePermissions(this);

        parent_view = findViewById(android.R.id.content);

//        this.mViewHolder.mEditNomeCliente = findViewById(R.id.edit_nome_cliente);
//        this.mViewHolder.mEditDocument = findViewById(R.id.edit_document);
//        this.mViewHolder.mEditFazenda = findViewById(R.id.edit_fazenda);
//        this.mViewHolder.mButtonSave = findViewById(R.id.button_save);
        this.mViewHolder.mQtdeAnimais = findViewById(R.id.edit_quantidade_animais);

        this.mGuestBusiness = new GuestBusiness(this);

//        this.setListeners();

        this.mViewHolder.txtCategoria = findViewById(R.id.tv_categoria);
        this.mViewHolder.txtRacial = findViewById(R.id.tv_racial);
//        spn_pasto = findViewById(R.id.spn_pasto_racao);
//        spn_grao_inteiro = findViewById(R.id.spn_grao_inteiro);
//        spn_convencional = findViewById(R.id.spn_convencional);
//
//        spn_grao_inteiro.setEnabled(false);
//        spn_convencional.setEnabled(false);
//
//        check_grao_inteiro = findViewById(R.id.check_grao_inteiro);
//        check_convencional = findViewById(R.id.check_convencional);
//        spn_semi_confinamento = findViewById(R.id.spn_semi_confinamento);
//        check_semi_confinamento = findViewById(R.id.check_semi_confinamento);
        check_ausente = findViewById(R.id.check_ausente);
        check_escasso_menos = findViewById(R.id.check_escasso_menos);
        check_escasso = findViewById(R.id.check_escasso);
        check_mediano = findViewById(R.id.check_mediano);
        check_uniforme = findViewById(R.id.check_uniforme);
        check_excessivo = findViewById(R.id.check_excessivo);
        check_0_dentes = findViewById(R.id.check_0_dentes);
        check_2_dentes = findViewById(R.id.check_2_dentes);
        check_4_dentes = findViewById(R.id.check_4_dentes);
        check_6_dentes = findViewById(R.id.check_6_dentes);
        check_8_dentes = findViewById(R.id.check_8_dentes);
        check_funrural = findViewById(R.id.check_funrural);

        check_a_vista = findViewById(R.id.check_a_vista);
        check_a_prazo = findViewById(R.id.check_a_prazo);

        this.mViewHolder.pesoFazenda = findViewById(R.id.edit_peso_fazenda);
        this.mViewHolder.pesoCarcaca = findViewById(R.id.edit_peso_carcaca);
        this.mViewHolder.rendimentoCarcaca = findViewById(R.id.edit_rendimento_carcaca);
        this.mViewHolder.text_arroba_fazenda = findViewById(R.id.text_arroba_fazenda);
        this.mViewHolder.text_arroba_carcaca = findViewById(R.id.text_arroba_carcaca);
        this.mViewHolder.edit_quantidade_ausente = findViewById(R.id.edit_quantidade_ausente);
        this.mViewHolder.edit_percentual_ausente = findViewById(R.id.edit_percentual_ausente);
        this.mViewHolder.edit_quantidade_escasso_menos = findViewById(R.id.edit_quantidade_escasso_menos);
        this.mViewHolder.edit_percentual_escasso_menos = findViewById(R.id.edit_percentual_escasso_menos);
        this.mViewHolder.edit_quantidade_escasso = findViewById(R.id.edit_quantidade_escasso);
        this.mViewHolder.edit_percentual_escasso = findViewById(R.id.edit_percentual_escasso);
        this.mViewHolder.edit_quantidade_mediano = findViewById(R.id.edit_quantidade_mediano);
        this.mViewHolder.edit_percentual_mediano = findViewById(R.id.edit_percentual_mediano);
        this.mViewHolder.edit_quantidade_uniforme = findViewById(R.id.edit_quantidade_uniforme);
        this.mViewHolder.edit_percentual_uniforme = findViewById(R.id.edit_percentual_uniforme);
        this.mViewHolder.edit_quantidade_excessivo = findViewById(R.id.edit_quantidade_excessivo);
        this.mViewHolder.edit_percentual_excessivo = findViewById(R.id.edit_percentual_excessivo);
        this.mViewHolder.edit_quantidade_0_dentes = findViewById(R.id.edit_quantidade_0_dentes);
        this.mViewHolder.edit_quantidade_2_dentes = findViewById(R.id.edit_quantidade_2_dentes);
        this.mViewHolder.edit_quantidade_4_dentes = findViewById(R.id.edit_quantidade_4_dentes);
        this.mViewHolder.edit_quantidade_6_dentes = findViewById(R.id.edit_quantidade_6_dentes);
        this.mViewHolder.edit_quantidade_8_dentes = findViewById(R.id.edit_quantidade_8_dentes);
        this.mViewHolder.edit_percentual_0_dentes = findViewById(R.id.edit_percentual_0_dentes);
        this.mViewHolder.edit_percentual_2_dentes = findViewById(R.id.edit_percentual_2_dentes);
        this.mViewHolder.edit_percentual_4_dentes = findViewById(R.id.edit_percentual_4_dentes);
        this.mViewHolder.edit_percentual_6_dentes = findViewById(R.id.edit_percentual_6_dentes);
        this.mViewHolder.edit_percentual_8_dentes = findViewById(R.id.edit_percentual_8_dentes);

        edit_acerto_total_bruto = findViewById(R.id.edit_acerto_total_bruto);
        edit_acerto_desconto_funrural = findViewById(R.id.edit_acerto_desconto_funrural);
        edit_acerto_total_liquido = findViewById(R.id.edit_acerto_total_liquido);
        edit_acerto_percentual_antecipacao = findViewById(R.id.edit_acerto_percentual_antecipacao);
        edit_acerto_valor_antecipacao = findViewById(R.id.edit_acerto_valor_antecipacao);
        edit_arroba_negociada = findViewById(R.id.edit_arroba_negociada);
        edit_arroba_recebida = findViewById(R.id.edit_arroba_recebida);

        edit_observacoes = findViewById(R.id.edit_observacoes);
        edit_observacoes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.edit_observacoes) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        edit_arroba_negociada_a_prazo = findViewById(R.id.edit_arroba_negociada_a_prazo);

        btn_rendimento_carcaca = findViewById(R.id.btn_rendimento_carcaca);
        btn_dados_rendimento = findViewById(R.id.btn_dados_acabamento);
        btn_maturidade = findViewById(R.id.btn_maturidade);
        btn_calcular_acerto = findViewById(R.id.btn_calcular_acerto);

        image_lote = findViewById(R.id.image_lote);

        image_lote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intentGaleria.resolveActivity(AbateFormActivity.this.getPackageManager()) != null) {
                    startActivityForResult(intentGaleria, DataBaseConstants.SELECAO_GALERIA);
                }
            }
        });

//        String[] tiposDePastos = getResources().getStringArray(R.array.pasto);
//        final ArrayAdapter<String> arrayPasto = new ArrayAdapter<>(this, R.layout.simple_spinner_item, tiposDePastos);
//        arrayPasto.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spn_pasto.setAdapter(arrayPasto);
//
//        String[] confinadoGraos = getResources().getStringArray(R.array.grao_inteiro);
//        final ArrayAdapter<String> arrayGraos = new ArrayAdapter<>(this, R.layout.simple_spinner_item, confinadoGraos);
//        arrayGraos.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spn_grao_inteiro.setAdapter(arrayGraos);
//
//        String[] confinadoConvencional = getResources().getStringArray(R.array.convencional);
//        final ArrayAdapter<String> arrayConvencional = new ArrayAdapter<>(this, R.layout.simple_spinner_item, confinadoConvencional);
//        arrayConvencional.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spn_convencional.setAdapter(arrayConvencional);
//
//        String[] semi_confinamento = getResources().getStringArray(R.array.semi_confinamento);
//        final ArrayAdapter<String> arraySemiConfinamento = new ArrayAdapter<>(this, R.layout.simple_spinner_item, semi_confinamento);
//        arraySemiConfinamento.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spn_semi_confinamento.setAdapter(arraySemiConfinamento);

        findViewById(R.id.btn_dialog_categorias).setOnClickListener(this);
        findViewById(R.id.btn_dialog_bonificacao).setOnClickListener(this);
        findViewById(R.id.btn_dialog_penalizacao).setOnClickListener(this);

//        this.mViewHolder.bt_toggle_input_pasto = (ImageButton) findViewById(R.id.bt_toggle_input_pasto);
//        this.mViewHolder.bt_toggle_input_confinamento = (ImageButton) findViewById(R.id.bt_toggle_input_confinamento);
//        lyt_expand_pasto = findViewById(R.id.lyt_expand_pasto);
//        lyt_expand_pasto.setVisibility(View.GONE);
//        lyt_expand_confinamento = findViewById(R.id.lyt_expand_confinamento);
//        lyt_expand_confinamento.setVisibility(View.GONE);
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);

//        this.mViewHolder.bt_toggle_input_pasto.setOnClickListener(this);
//        this.mViewHolder.bt_toggle_input_confinamento.setOnClickListener(this);

        btn_rendimento_carcaca.setOnClickListener(this);
        btn_dados_rendimento.setOnClickListener(this);
        btn_maturidade.setOnClickListener(this);
        btn_calcular_acerto.setOnClickListener(this);

//        check_semi_confinamento.setOnCheckedChangeListener(this);
//        check_grao_inteiro.setOnCheckedChangeListener(this);
//        check_convencional.setOnCheckedChangeListener(this);
        check_ausente.setOnCheckedChangeListener(this);
        check_escasso_menos.setOnCheckedChangeListener(this);
        check_escasso.setOnCheckedChangeListener(this);
        check_mediano.setOnCheckedChangeListener(this);
        check_uniforme.setOnCheckedChangeListener(this);
        check_excessivo.setOnCheckedChangeListener(this);
        check_0_dentes.setOnCheckedChangeListener(this);
        check_2_dentes.setOnCheckedChangeListener(this);
        check_4_dentes.setOnCheckedChangeListener(this);
        check_6_dentes.setOnCheckedChangeListener(this);
        check_8_dentes.setOnCheckedChangeListener(this);
        check_funrural.setOnCheckedChangeListener(this);
        check_a_vista.setOnCheckedChangeListener(this);
        check_a_prazo.setOnCheckedChangeListener(this);

        listViewBonificacao = findViewById(R.id.listViewBonificacao);
        adapterBonificacao = new AdapterBonificacoesPersonalizado(this.bonificacoes, this);
        listViewBonificacao.setAdapter(adapterBonificacao);
        setListViewHeightBasedOnChildren(listViewBonificacao);

        listViewBonificacao.setOnItemClickListener(this);

        listViewPenalizacao = findViewById(R.id.listViewPenalizacao);
        adapterPenalizacao = new AdapterPenalizacoesPersonalizado(this.penalizacoes, this);
        listViewPenalizacao.setAdapter(adapterPenalizacao);
        setListViewHeightBasedOnChildren(listViewPenalizacao);

        listViewPenalizacao.setOnItemClickListener(this);

        bonificacaoTotal = findViewById(R.id.bonificacao_total);
        penalizacaoTotal = findViewById(R.id.penalizacao_total);
        txt_rendimento_estimado = findViewById(R.id.txt_rendimento_estimado);

        Bundle dados = getIntent().getExtras();
//        Abate abateEditado;
        if (dados != null) {
            abate = dados.getParcelable("abate");
            this.loadDataFromActivity(abate);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                Permissao.alertaValidacaoPermissao(this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imagemLote = null;

            try {
                switch (requestCode) {
                    case DataBaseConstants.SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagemLote = MediaStore.Images.Media.getBitmap(AbateFormActivity.this.getContentResolver(), localImagemSelecionada);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imagemLote != null) {
                image_lote.setImageBitmap(imagemLote);

                //Recuperar os dados da imagem para o Firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagemLote.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosDaImagem = baos.toByteArray();

                //Salvar imagem no Firebase
                final StorageReference imagemRef;
                final String nomeArquivo;
                if (abate.getId() == null || abate.getFotoLote() == null) {
                    nomeArquivo = UUID.randomUUID().toString();
                } else {
                    nomeArquivo = abate.getFotoLote();
                }

                imagemRef = storageReference
                        .child("imagens")
                        .child("lote")
                        .child(nomeArquivo + ".jpeg");

                UploadTask uploadTask = imagemRef.putBytes(dadosDaImagem);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(parent_view, "Erro ao fazer upload da imagem!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar snackbar = Snackbar.make(parent_view, "Sucesso ao fazer upload da imagem!", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                        url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                atualizaFotoLote(nomeArquivo);
                                fotoDoAbate = true;
                            }
                        });
                    }
                });
            } else {
                fotoDoAbate = false;
            }
        }
    }

    private void atualizaFotoLote(String nomeArquivo) {
        abate.setFotoLote(nomeArquivo);
//        abate.atualizar();

        Snackbar snackbar = Snackbar.make(parent_view, "Sua foto foi alterada com sucesso!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void initComponent() {

        frigorificoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("frigorificos");
        clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");
        propriedadeRef = ConfiguracaoFirebase.getFirebaseDatabase().child("propriedades");

        txt_data_abate = findViewById(R.id.txt_data_abate);

        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        abateRef = ConfiguracaoFirebase.getFirebaseDatabase().child("abates");

        spinnerTecnico = findViewById(R.id.spn_nome_tecnico);
        usuariosAdapter = new ArrayAdapter<>(AbateFormActivity.this, R.layout.simple_spinner_item, usuarios);
        usuariosAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerTecnico.setAdapter(usuariosAdapter);
        spinnerTecnico.setSelection(0);

        String[] listaFrigorificos = getResources().getStringArray(R.array.frigorifico);
        spinnerFrigorifico = findViewById(R.id.spn_nome_frigorifico);
        spinnerCliente = findViewById(R.id.spn_nome_cliente);
        spinnerPropriedade = findViewById(R.id.spn_fazenda);

        frigorificoAdapter = new ArrayAdapter<>(AbateFormActivity.this, R.layout.simple_spinner_item, frigorificos);
        frigorificoAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerFrigorifico.setAdapter(frigorificoAdapter);
        spinnerFrigorifico.setSelection(0);

        clienteAdapter = new ArrayAdapter<>(AbateFormActivity.this, R.layout.simple_spinner_item, clientesNomes);
        clienteAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(clienteAdapter);
        spinnerCliente.setSelection(0);

        propriedadeAdapter = new ArrayAdapter<>(AbateFormActivity.this, R.layout.simple_spinner_item, propriedadesNomes);
        propriedadeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerPropriedade.setAdapter(propriedadeAdapter);
        spinnerPropriedade.setSelection(0);

        spinnerTecnico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerFrigorifico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nomeClienteSelecionado = spinnerCliente.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPropriedade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nomePropriedadeSelecionada = spinnerPropriedade.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adicionar_frigorifico = findViewById(R.id.adicionar_frigorifico);
        adicionar_cliente = findViewById(R.id.btn_dialog_clientes);
        adicionar_propriedade = findViewById(R.id.btn_dialog_propriedades);

        adicionar_frigorifico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFrigorificoDialog();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                            }
                        });
            }
        });

        adicionar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showClienteDialog();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                            }
                        });
            }
        });

        adicionar_propriedade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPropriedadeDialog();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                            }
                        });
            }
        });

        txt_data_abate.setText(dateString);

        datePickerAbate = findViewById(R.id.datePickerAbate);

        datePickerAbate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerLight((ImageButton) view);
            }
        });
    }

    private void showFrigorificoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_frigorifico);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edit_novo_frigorifico = dialog.findViewById(R.id.edit_novo_frigorifico);

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

                        Frigorifico frigorificoGerado = inicializaFrigorifico();

                        if (frigorificoGerado.getId() == null) {
                            frigorificoGerado.salvar();
                        } else {
                            frigorificoGerado.atualizar();
                        }

                        dialog.dismiss();
                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private Frigorifico inicializaFrigorifico() {

        Frigorifico frigorificoSalvo = new Frigorifico();

        frigorificoSalvo.setNomeFrigorifico(edit_novo_frigorifico.getText().toString().trim().toUpperCase());

        return frigorificoSalvo;
    }

    private void showClienteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_cliente);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edit_novo_cliente = dialog.findViewById(R.id.edit_novo_cliente);
//        edit_novo_cliente.setText(nomeCliente);

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

                        Cliente clienteGerado = inicializaCliente();

                        if (clienteGerado.getId() == null) {
                            if (!edit_novo_cliente.getText().toString().trim().equals("")) {
                                clienteGerado.salvar();
                                recuperarClientes(edit_novo_cliente.getText().toString());

                                dialog.dismiss();
                            } else {
                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText(R.string.err_msg_clienteValidado);

                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                                edit_novo_cliente.requestFocus();
                            }
                        } else {
                            clienteGerado.atualizar();
                            recuperarClientes(edit_novo_cliente.getText().toString());

                            dialog.dismiss();
                        }
                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private Cliente inicializaCliente() {

        Cliente clienteSalvo = new Cliente();

        clienteSalvo.setnomeCliente(edit_novo_cliente.getText().toString().trim());

        return clienteSalvo;
    }

    private void showPropriedadeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_propriedade);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edit_nova_propriedade = dialog.findViewById(R.id.edit_nova_propriedade);
//        edit_nova_propriedade.setText(nomePropriedade);

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

                        Propriedade propriedadeGerada = inicializaPropriedade();

                        if (propriedadeGerada.getId() == null) {
                            if (!edit_nova_propriedade.getText().toString().trim().equals("")) {
                                propriedadeGerada.salvar();
                                recuperarPropriedades(edit_nova_propriedade.getText().toString());

                                dialog.dismiss();
                            } else {
                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText(R.string.err_msg_propriedadeValidada);

                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                                edit_nova_propriedade.requestFocus();
                            }
                        } else {
                            propriedadeGerada.atualizar();
                            recuperarPropriedades(edit_nova_propriedade.getText().toString());

                            dialog.dismiss();
                        }
                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private Propriedade inicializaPropriedade() {

        Propriedade propriedadeSalva = new Propriedade();

        propriedadeSalva.setNomePropriedade(edit_nova_propriedade.getText().toString().trim());

        return propriedadeSalva;
    }

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

                        if (bt.getId() == R.id.datePickerAbate) {
                            txt_data_abate.setText(Tools.getFormattedDateAbate(date_ship_millis));
                        } else {
                            txt_data_abate.setText(dateString);

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
        datePicker.show(this.getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        recuperaUsuarios();
        recuperarFrigorificos();
        recuperarClientes("");
        recuperarPropriedades("");
    }

    private void recuperarFrigorificos() {

        frigorificoEncontrado = false;

        valueEventListenerFrigorificos = frigorificoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                frigorificos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Frigorifico frigorificoBuscado = dados.getValue(Frigorifico.class);
                    frigorificoBuscado.setId(dados.getKey());
                    frigorificos.add(frigorificoBuscado.getNomeFrigorifico());

//                    if (frigorificoBuscado.getNomeFrigorifico().equals(txtDataCotacao.getText().toString())) {
//                        cotacao = cotacaoBuscada;
//                        cotacaoEncontrada = true;
//                    }
                }

                if (frigorificoAdapter != null) {
                    frigorificoAdapter.notifyDataSetChanged();
                }

                for (int i = 0; i < frigorificos.size(); i++) {
                    if (frigorificos.get(i).equalsIgnoreCase(abate.getFrigorifico())) {
                        spinnerFrigorifico.setSelection(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recuperarClientes(String nome) {

        clienteEncontrado = false;
        this.nomeCliente = nome;

        valueEventListenerClientes = clienteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                clientesNomes.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Cliente clienteBuscado = dados.getValue(Cliente.class);
                    clienteBuscado.setId(dados.getKey());
                    clientesNomes.add(clienteBuscado.getnomeCliente());
                }

                if (clienteAdapter != null) {
                    clienteAdapter.notifyDataSetChanged();
                }

                for (int i = 0; i < clientesNomes.size(); i++) {
                    if (abate.getCliente() != null) {
                        nomeCliente = abate.getCliente().getnomeCliente();
                    } else if (abate.getLote() != null) {
                        nomeCliente = abate.getLote().getNomeCliente();
                    }

                    if (clientesNomes.get(i).equalsIgnoreCase(nomeCliente)) {
                        spinnerCliente.setSelection(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recuperarPropriedades(String nomePropriedade) {

        propriedadeEncontrada = false;
        final String[] propriedadeNome = {nomePropriedade};

        valueEventListenerPropriedades = propriedadeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                propriedadesNomes.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Propriedade propriedadeBuscada = dados.getValue(Propriedade.class);
                    propriedadeBuscada.setId(dados.getKey());
                    propriedadesNomes.add(propriedadeBuscada.getNomePropriedade());
                }

                if (propriedadeAdapter != null) {
                    propriedadeAdapter.notifyDataSetChanged();
                }

                for (int i = 0; i < propriedadesNomes.size(); i++) {

                    if (abate.getPropriedade() != null) {
                        propriedadeNome[0] = abate.getPropriedade().getNomePropriedade();
                    } else if (abate.getLote() != null) {
                        propriedadeNome[0] = abate.getLote().getPropriedade();
                    }
                    if (propriedadesNomes.get(i).equalsIgnoreCase(propriedadeNome[0])) {
                        spinnerPropriedade.setSelection(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void atualizaSpinnerTecnico(ArrayList<Usuario> usuarios) {
        if (abate.getTecnico() != null) {
            int posicaoArray = 0;

            for (int i = 0; (i <= usuarios.size() - 1); i++) {

                if (usuarios.get(i).getNome().equals(abate.getTecnico().getNome())) {
                    posicaoArray = i;
                    break;
                } else {
                    posicaoArray = 0;
                }
            }
            spinnerTecnico.setSelection(posicaoArray);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuarios);
        frigorificoRef.removeEventListener(valueEventListenerFrigorificos);
        clienteRef.removeEventListener(valueEventListenerClientes);
        propriedadeRef.removeEventListener(valueEventListenerPropriedades);
    }

    private void recuperaUsuarios() {
        valueEventListenerUsuarios = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarios.clear();

                for (DataSnapshot dados1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Usuario usuario = dados.getValue(Usuario.class);
                        usuarios.add(usuario);
                    }

                    usuariosAdapter.notifyDataSetChanged();
                    atualizaSpinnerTecnico(usuarios);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//
//        if (listAdapter == null) return;
//
//        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0) view.setLayoutParams(new
//                    ViewGroup.LayoutParams(desiredWidth,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//
//        params.height = totalHeight + (listView.getDividerHeight() *
//                (listAdapter.getCount() - 1));
//
//        listView.setLayoutParams(params);
//        listView.requestLayout();

//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            // pre-condition
//            return;
//        }
//
//        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
//
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            if (listItem instanceof ViewGroup) {
//                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            }
//
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//
//        listView.setLayoutParams(params);

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 0;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.gerar_pdf:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Aguarde..."); // Setting Message
                progressDialog.setTitle("Gerando PDF"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            Abate abateGerado = inicializaAbate();
                            if (validaQtdeAnimaisDoLote() && validaRendimento() && validaRendimentoCalculado() && validaAcerto(abateGerado)) {
                                pdfView(abateGerado);
                                if (abateGerado.getId() == null) {
                                    abateGerado.salvar();
                                } else {
                                    abateGerado.atualizar();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean validaAcerto(Abate abate) {
        if (abate.getAcerto().getPrazo().equals("A prazo")) {
            if (abate.getAcerto().getDias() != 0 && !edit_arroba_negociada_a_prazo.getText().toString().equals("")) {
                return true;
            } else {
                Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento_dias_a_prazo, Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public boolean onTouch(View view, MotionEvent event) {

        if (view.getId() == R.id.edit_observacoes || view.getId() == R.id.edit_bonificacao_observacoes || view.getId() == R.id.edit_penalizacao_observacoes || view.getId() == R.id.edit_penalizacao_descricao) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    public void pdfView(Abate abate) throws IOException, DocumentException {

        templatePDF = new TemplatePDF(getApplicationContext());

        Float tamanhoPDF = medirPDF(abate);

        if (abate.getFotoLote() != null)
            tamanhoPDF += 1000f;

        if (tamanhoPDF >= 1200f) {
            templatePDF.openDocument(tamanhoPDF);
        } else {
            tamanhoPDF = 1200f;
            templatePDF.openDocument(tamanhoPDF);
        }

//        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", dateString);
        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", txt_data_abate.getText().toString());
//        templatePDF.onStartPage();
        if (imagemLote == null && fotoDoAbate) {
//            image_lote.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) image_lote.getDrawable();
            imagemLote = drawable.getBitmap();
        }
        templatePDF.carregaImagemDoLote(imagemLote);
        templatePDF.addFormulario(abate);
        templatePDF.closeDocument();
        templatePDF.viewPDF();
    }

    private Float medirPDF(Abate abate) throws DocumentException {
        pageSizeAbatePDF = 2000f;

        templatePDF.openDocument(pageSizeAbatePDF);
//        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", dateString);
        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", txt_data_abate.getText().toString());
//        templatePDF.onStartPage();
        if (imagemLote == null && fotoDoAbate) {
//            image_lote.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) image_lote.getDrawable();
            imagemLote = drawable.getBitmap();
        }
        templatePDF.carregaImagemDoLote(imagemLote);
        templatePDF.addFormulario(abate);
        Float tamanhoFinalPDF = templatePDF.calcularMedidaPDF();
        templatePDF.closeDocument();

        return (tamanhoFinalPDF - pageSizeAbatePDF);
    }

    private Abate inicializaAbate() {

        Usuario tecnico = new Usuario();

        abate.setStatus(false);

        if (spinnerTecnico.getSelectedItem() != null)
            tecnico = (Usuario) spinnerTecnico.getSelectedItem();

        abate.setTecnico(tecnico);
        abate.setFrigorifico(spinnerFrigorifico.getSelectedItem().toString());

        Cliente cliente = new Cliente();
        cliente.setnomeCliente(spinnerCliente.getSelectedItem().toString());
//        cliente.setPropriedade(spinnerPropriedade.getSelectedItem().toString());

        abate.setCliente(cliente);
//        abate.setDataAbate(dateString);
        abate.setDataAbate(txt_data_abate.getText().toString());

        Propriedade propriedade = new Propriedade();
        propriedade.setNomePropriedade(spinnerPropriedade.getSelectedItem().toString());

        Lote lote = new Lote();
        lote.setNomeCliente(cliente.getnomeCliente());
        lote.setPropriedade(propriedade.getNomePropriedade());
        lote.setQtdeAnimaisLote(this.mViewHolder.mQtdeAnimais.getText().toString());

        Categoria categoria = new Categoria();
        categoria.setCategoria(this.mViewHolder.txtCategoria.getText().toString());
        categoria.setRacial(this.mViewHolder.txtRacial.getText().toString());
        TextView qtdeBezerrosGrandes = findViewById(R.id.tv_bezerros_grande);
        TextView qtdeBezerrosMedios = findViewById(R.id.tv_bezerros_medio);
        TextView qtdeBezerrosPequenos = findViewById(R.id.tv_bezerros_pequeno);
        categoria.setQtdeBezerrosGrandes(qtdeBezerrosGrandes.getText().toString());
        categoria.setQtdeBezerrosMedios(qtdeBezerrosMedios.getText().toString());
        categoria.setQtdeBezerrosPequenos(qtdeBezerrosPequenos.getText().toString());

        Rendimento rendimento = new Rendimento();
        rendimento.setPesoFazendaKilo(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", pesoFazendaKilo))));
        rendimento.setPesoCarcacaKilo(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", pesoCarcacaKilo))));
        rendimento.setPesoFazendaArroba(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoPesoFazendaArroba))));
        rendimento.setPesoCarcacaArroba(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoPesoCarcacaArroba))));
        rendimento.setRendimentoCarcaça(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoRendimentoCarcaca))));
        rendimento.setRendimentoEstimado(txt_rendimento_estimado.getText().toString());

        Acabamento acabamento = new Acabamento();
        acabamento.setQtdeAusente(this.mViewHolder.edit_quantidade_ausente.getText().toString());
        acabamento.setQtdeEscassoMenos(this.mViewHolder.edit_quantidade_escasso_menos.getText().toString());
        acabamento.setQtdeEscasso(this.mViewHolder.edit_quantidade_escasso.getText().toString());
        acabamento.setQtdeMediano(this.mViewHolder.edit_quantidade_mediano.getText().toString());
        acabamento.setQtdeUniforme(this.mViewHolder.edit_quantidade_uniforme.getText().toString());
        acabamento.setQtdeExcessivo(this.mViewHolder.edit_quantidade_excessivo.getText().toString());
        acabamento.setPercentualAusente(this.mViewHolder.edit_percentual_ausente.getText().toString());
        acabamento.setPercentualEscassoMenos(this.mViewHolder.edit_percentual_escasso_menos.getText().toString());
        acabamento.setPercentualEscasso(this.mViewHolder.edit_percentual_escasso.getText().toString());
        acabamento.setPercentualMediano(this.mViewHolder.edit_percentual_mediano.getText().toString());
        acabamento.setPercentualUniforme(this.mViewHolder.edit_percentual_uniforme.getText().toString());
        acabamento.setPercentualExcessivo(this.mViewHolder.edit_percentual_excessivo.getText().toString());

        Maturidade maturidade = new Maturidade();
        maturidade.setQtdeZeroDentes(this.mViewHolder.edit_quantidade_0_dentes.getText().toString());
        maturidade.setQtdeDoisDentes(this.mViewHolder.edit_quantidade_2_dentes.getText().toString());
        maturidade.setQtdeQuatroDentes(this.mViewHolder.edit_quantidade_4_dentes.getText().toString());
        maturidade.setQtdeSeisDentes(this.mViewHolder.edit_quantidade_6_dentes.getText().toString());
        maturidade.setQtdeOitoDentes(this.mViewHolder.edit_quantidade_8_dentes.getText().toString());
        maturidade.setPercentualZeroDentes(this.mViewHolder.edit_percentual_0_dentes.getText().toString());
        maturidade.setPercentualDoisDentes(this.mViewHolder.edit_percentual_2_dentes.getText().toString());
        maturidade.setPercentualQuatroDentes(this.mViewHolder.edit_percentual_4_dentes.getText().toString());
        maturidade.setPercentualSeisDentes(this.mViewHolder.edit_percentual_6_dentes.getText().toString());
        maturidade.setPercentualOitoDentes(this.mViewHolder.edit_percentual_8_dentes.getText().toString());

        Acerto acerto = new Acerto();
        acerto.setArrobaNegociada(edit_arroba_negociada.getText().toString());
        acerto.setArrobaRecebida(edit_arroba_recebida.getText().toString());
        acerto.setArrobaRecebidaComFunrural(String.valueOf(arrobaRecebidaComFunrural));
        acerto.setDescontoFunrural(edit_acerto_desconto_funrural.getText().toString());
        acerto.setTotalBruto(edit_acerto_total_bruto.getText().toString());
        acerto.setTotalLiquido(edit_acerto_total_liquido.getText().toString());
        acerto.setTotalAntecipacao(edit_acerto_valor_antecipacao.getText().toString());
        acerto.setPrazo(prazoAcerto);
        acerto.setDias(diasAcerto);

        abate.setLote(lote);
        abate.setCategoria(categoria);
        abate.setRendimento(rendimento);
        abate.setAcabamento(acabamento);
        abate.setMaturidade(maturidade);
        abate.setBonificacoes(bonificacoes);
        abate.setPenalizacoes(penalizacoes);
        abate.setAcerto(acerto);
        abate.setObservacoes(edit_observacoes.getText().toString());

//        if (!abate.getCategoria().getCategoria().equals("")) {
//            pageSizeAbatePDF = 250f;
//        }
//
//        int somaBezerros = (Integer.parseInt(abate.getCategoria().getQtdeBezerrosGrandes()) + Integer.parseInt(abate.getCategoria().getQtdeBezerrosMedios()) + Integer.parseInt(abate.getCategoria().getQtdeBezerrosPequenos()));
//        if (somaBezerros > 0) {
//            pageSizeAbatePDF += 250f;
//        }
//
//        int somaAcabamento = (Integer.parseInt(abate.getAcabamento().getQtdeAusente()) + Integer.parseInt(abate.getAcabamento().getQtdeEscasso()) + Integer.parseInt(abate.getAcabamento().getQtdeEscassoMenos()) + Integer.parseInt(abate.getAcabamento().getQtdeMediano()) + Integer.parseInt(abate.getAcabamento().getQtdeUniforme()) + Integer.parseInt(abate.getAcabamento().getQtdeExcessivo()));
//        if (somaAcabamento > 0) {
//            pageSizeAbatePDF += 250f;
//        } else {
//            pageSizeAbatePDF -= 250f;
//        }
//
//        int somaMaturidade = (Integer.parseInt(abate.getMaturidade().getQtdeZeroDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeDoisDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeQuatroDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeSeisDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeOitoDentes()));
//        if (somaMaturidade > 0) {
//            pageSizeAbatePDF += 250f;
//        }
//
//        if (abate.getBonificacoes().size() > 0) {
//            pageSizeAbatePDF += 250f;
//        } else {
//            pageSizeAbatePDF -= 250f;
//        }
//
//        if (abate.getPenalizacoes().size() > 0) {
//            pageSizeAbatePDF += 250f;
//        } else {
//            pageSizeAbatePDF -= 250f;
//        }
//
//        if (!abate.getAcerto().getTotalLiquido().equals("") || !abate.getAcerto().getArrobaNegociada().equals("")) {
//            pageSizeAbatePDF += 250f;
//        }
//
//        if (!abate.getObservacoes().equals("")) {
//            pageSizeAbatePDF += 500f;
//        } else {
//            pageSizeAbatePDF -= 250f;
//        }
//
//        if (abate.getFotoLote() != null) {
//            pageSizeAbatePDF += 700f;
//        }

        return abate;
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

    public void initToolBar() {

        this.mViewHolder.mToolbar = findViewById(R.id.tb_main);
//        this.mViewHolder.mToolbar.setTitle("RESUMO DE ABATE - PECBR");
//        this.mViewHolder.mToolbar.setSubtitle("Data: " + dateString);
        this.mViewHolder.mToolbar.setTitleTextColor(Color.WHITE);
//        this.mViewHolder.mToolbar.setSubtitleTextColor(Color.WHITE);
//        this.mViewHolder.mToolbar.setLogo(R.mipmap.ic_launcher);

        typeFace_date = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_light.otf");
        typeFace_title = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_bold.otf");

//        ((TextView) this.mViewHolder.mToolbar.getChildAt(0)).setTypeface(typeFace_title);
//        ((TextView) this.mViewHolder.mToolbar.getChildAt(0)).setTextSize(16);

//        ((TextView) this.mViewHolder.mToolbar.getChildAt(1)).setTypeface(typeFace_title);
//        ((TextView) this.mViewHolder.mToolbar.getChildAt(1)).setTextSize(14);

        setSupportActionBar(this.mViewHolder.mToolbar);

        this.mViewHolder.mToolbar.setNavigationIcon(R.drawable.voltar);
        this.mViewHolder.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadDataFromActivity(Abate abate) {

        this.abate.setId(abate.getId());

        txt_data_abate.setText(abate.getDataAbate());

//        this.mViewHolder.mEditNomeCliente.setText(abate.getLote().getNomeCliente());
//        this.mViewHolder.mEditFazenda.setText(abate.getLote().getPropriedade());
        this.mViewHolder.mQtdeAnimais.setText(abate.getLote().getQtdeAnimaisLote());

        //Rendimento
        this.mViewHolder.pesoCarcaca.setText(abate.getRendimento().getPesoCarcacaKilo());

        if (abate.getRendimento().getPesoFazendaKilo() != null) {
            this.mViewHolder.pesoFazenda.setText(abate.getRendimento().getPesoFazendaKilo());
        }

        //Categoria
        this.mViewHolder.txtCategoria.setText(abate.getCategoria().getCategoria());
        this.mViewHolder.txtRacial.setText(abate.getCategoria().getRacial());

        //Bezerros
        if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosGrandes()) > 0) {
            ((TextView) findViewById(R.id.tv_bezerros_grande)).setText(abate.getCategoria().getQtdeBezerrosGrandes());
        }

        if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosMedios()) > 0) {
            ((TextView) findViewById(R.id.tv_bezerros_medio)).setText(abate.getCategoria().getQtdeBezerrosMedios());
        }

        if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosPequenos()) > 0) {
            ((TextView) findViewById(R.id.tv_bezerros_pequeno)).setText(abate.getCategoria().getQtdeBezerrosPequenos());
        }

        calculaRendimento();

        //Acabamento
        int qtdAcabamento = 0;

        if (Integer.parseInt(abate.getAcabamento().getQtdeAusente()) > 0) {
            check_ausente.setChecked(true);
            this.mViewHolder.edit_quantidade_ausente.setText(abate.getAcabamento().getQtdeAusente());
            qtdAcabamento++;
        }

        if (Integer.parseInt(abate.getAcabamento().getQtdeEscassoMenos()) > 0) {
            check_escasso_menos.setChecked(true);
            this.mViewHolder.edit_quantidade_escasso_menos.setText(abate.getAcabamento().getQtdeEscassoMenos());
            qtdAcabamento++;
        }

        if (Integer.parseInt(abate.getAcabamento().getQtdeEscasso()) > 0) {
            check_escasso.setChecked(true);
            this.mViewHolder.edit_quantidade_escasso.setText(abate.getAcabamento().getQtdeEscasso());
            qtdAcabamento++;
        }

        if (Integer.parseInt(abate.getAcabamento().getQtdeMediano()) > 0) {
            check_mediano.setChecked(true);
            this.mViewHolder.edit_quantidade_mediano.setText(abate.getAcabamento().getQtdeMediano());
            qtdAcabamento++;
        }

        if (Integer.parseInt(abate.getAcabamento().getQtdeUniforme()) > 0) {
            check_uniforme.setChecked(true);
            this.mViewHolder.edit_quantidade_uniforme.setText(abate.getAcabamento().getQtdeUniforme());
            qtdAcabamento++;
        }

        if (Integer.parseInt(abate.getAcabamento().getQtdeExcessivo()) > 0) {
            check_excessivo.setChecked(true);
            this.mViewHolder.edit_quantidade_excessivo.setText(abate.getAcabamento().getQtdeExcessivo());
            qtdAcabamento++;
        }

        if (qtdAcabamento > 0)
            calcularAcabamento();

        //Maturidade
        int qtdMaturidade = 0;
        if (Integer.parseInt(abate.getMaturidade().getQtdeZeroDentes()) > 0) {
            check_0_dentes.setChecked(true);
            this.mViewHolder.edit_quantidade_0_dentes.setText(abate.getMaturidade().getQtdeZeroDentes());
            qtdMaturidade++;
        }

        if (Integer.parseInt(abate.getMaturidade().getQtdeDoisDentes()) > 0) {
            check_2_dentes.setChecked(true);
            this.mViewHolder.edit_quantidade_2_dentes.setText(abate.getMaturidade().getQtdeDoisDentes());
            qtdMaturidade++;
        }

        if (Integer.parseInt(abate.getMaturidade().getQtdeQuatroDentes()) > 0) {
            check_4_dentes.setChecked(true);
            this.mViewHolder.edit_quantidade_4_dentes.setText(abate.getMaturidade().getQtdeQuatroDentes());
            qtdMaturidade++;
        }

        if (Integer.parseInt(abate.getMaturidade().getQtdeSeisDentes()) > 0) {
            check_6_dentes.setChecked(true);
            this.mViewHolder.edit_quantidade_6_dentes.setText(abate.getMaturidade().getQtdeSeisDentes());
            qtdMaturidade++;
        }

        if (Integer.parseInt(abate.getMaturidade().getQtdeOitoDentes()) > 0) {
            check_8_dentes.setChecked(true);
            this.mViewHolder.edit_quantidade_8_dentes.setText(abate.getMaturidade().getQtdeOitoDentes());
            qtdMaturidade++;
        }

        if (qtdMaturidade > 0)
            calcularMaturidade();

        //Bonificações
        if (abate.getBonificacoes().size() > 0) {
            for (Bonificacao bonificacao : abate.getBonificacoes()) {
                bonificacoes.add(bonificacao);
                adapterBonificacao.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listViewBonificacao);

                atualizaTotalBonificacao(abate);
            }
        }

        //Penalizações
        if (abate.getPenalizacoes().size() > 0) {
            for (Penalizacao penalizacao : abate.getPenalizacoes()) {
                penalizacoes.add(penalizacao);
                adapterPenalizacao.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listViewPenalizacao);

                atualizaTotalPenalizacao();
            }

        }

        //Acerto
        if (abate.getAcerto().getTotalBruto() != null)
            edit_acerto_total_bruto.setText(abate.getAcerto().getTotalBruto());

        if (abate.getAcerto().getDescontoFunrural() != null) {
            edit_acerto_desconto_funrural.setText(abate.getAcerto().getDescontoFunrural());
        } else {
            check_funrural.setChecked(true);
            desativaFunrural = true;
        }

        if (abate.getAcerto().getTotalAntecipacao() != null)
            edit_acerto_valor_antecipacao.setText(abate.getAcerto().getTotalAntecipacao());

        if (abate.getAcerto().getTotalLiquido() != null)
            edit_acerto_total_liquido.setText(abate.getAcerto().getTotalLiquido());

        if (abate.getAcerto().getArrobaNegociada() != null)
            edit_arroba_negociada.setText(abate.getAcerto().getArrobaNegociada());

        if (abate.getAcerto().getArrobaRecebida() != null)
            edit_arroba_recebida.setText(abate.getAcerto().getArrobaRecebida());

        if (abate.getAcerto().getPrazo() != null && !abate.getAcerto().getPrazo().equals("")) {
            if (abate.getAcerto().getPrazo().equals("À vista")) {
                check_a_vista.setChecked(true);
            } else {
                check_a_prazo.setChecked(true);
                edit_arroba_negociada_a_prazo.setText(String.valueOf(abate.getAcerto().getDias()));
                diasAcerto = abate.getAcerto().getDias();
            }
        }

        if (abate.getAcerto().getTotalBruto() != null && !abate.getAcerto().getTotalBruto().equals(""))
            calcularAcerto();

        if (!abate.getObservacoes().equals(""))
            edit_observacoes.setText(abate.getObservacoes());

        storageReference.child("imagens")
                .child("lote")
                .child(abate.getFotoLote() + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
//            Uri url = abate.getFotoLote();

//            if (url != null) {

                    GlideApp.with(AbateFormActivity.this.getApplicationContext())
                            .load(uri.toString())
                            .into(image_lote);

                    fotoDoAbate = true;
                } else {
                    image_lote.setImageResource(R.drawable.padrao_boi);
                }
            }
        });

//        if (downloadUrl != null) {
////            Uri url = abate.getFotoLote();
//
////            if (url != null) {
//
//            GlideApp.with(this.getApplicationContext())
//                    .load(downloadUrl)
//                    .into(image_lote);
//        } else {
//            image_lote.setImageResource(R.drawable.padrao_boi);
//        }
//        }

    }

    private boolean validaQtdeAnimaisDoLote() {
        if (this.mViewHolder.mQtdeAnimais.getText().toString().trim().isEmpty()) {
            Snackbar.make(parent_view, R.string.err_msg_qtde, Snackbar.LENGTH_SHORT).show();
            requestFocus(this.mViewHolder.mQtdeAnimais);
            return false;
        }

        return true;
    }

    private boolean validaRendimento() {

        if (this.mViewHolder.pesoCarcaca.getText().toString().trim().isEmpty() || this.mViewHolder.pesoCarcaca.getText().toString().trim().equals("0.0")) {
            Snackbar.make(parent_view, R.string.err_msg_rendimento_pesoFrigorifico, Snackbar.LENGTH_SHORT).show();
            requestFocus(this.mViewHolder.pesoCarcaca);
            return false;
        }

        return true;
    }

    private boolean validaRendimentoCalculado() {
        if (this.mViewHolder.text_arroba_carcaca.getText().equals(" @ ")) {
            Snackbar.make(parent_view, R.string.err_msg_rendimento_carcaca, Snackbar.LENGTH_SHORT).show();
            requestFocus(this.mViewHolder.rendimentoCarcaca);
            return false;
        }

        return true;
    }

    private boolean validaDadosDoRendimento() {
//        if (this.mViewHolder.pesoFazenda.getText().toString().trim().isEmpty()) {
//            Snackbar.make(parent_view, R.string.err_msg_pesoFazenda, Snackbar.LENGTH_SHORT).show();
//            requestFocus(this.mViewHolder.pesoFazenda);
//            return false;
//        }

        if (this.mViewHolder.mQtdeAnimais.getText().toString().isEmpty()) {
            Snackbar.make(parent_view, R.string.err_msg_qtde, Snackbar.LENGTH_SHORT).show();
            requestFocus(this.mViewHolder.mQtdeAnimais);
            return false;
        }

        if (this.mViewHolder.pesoCarcaca.getText().toString().trim().isEmpty() || this.mViewHolder.pesoCarcaca.getText().toString().trim().equals("0.0")) {
            Snackbar.make(parent_view, R.string.err_msg_pesoCarcaca, Snackbar.LENGTH_SHORT).show();
            requestFocus(this.mViewHolder.pesoCarcaca);
            return false;
        }

        return true;
    }

    private boolean validaBonificacao(int bonificacao) {
        if (bonificacao == 0) {
//            Snackbar.make(parent_view, R.string.err_msg_parcialBonificacao, Snackbar.LENGTH_SHORT).show();
            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(R.string.err_msg_parcialBonificacao);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }

        return true;
    }

    private boolean validaBonificacaoCalculada() {
//            Snackbar.make(parent_view, R.string.err_msg_parcialBonificacao, Snackbar.LENGTH_SHORT).show();
        if (valorTotalBonificacao.equals(0.0)) {

//            bonificacaoValidada = false;

            return false;
        }

        return true;
    }

    private boolean validaParcialBonificacao(ParcialBonificacao parcialBonificacao) {

        if (edit_quantidade_animais_bonificacao.getText().toString().trim().isEmpty()) {
            if (parcialBonificacao.getQtde().equals("")) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(R.string.err_msg_qtde_ParcialBonificacao);

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                return false;
            }
        }

        if ((Integer.parseInt(parcialBonificacao.getQtde()) + qtdeAnimaisBonificados[0]) > qtdeAnimais) {
            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(R.string.err_msg_qtde_ParcialBonificacao_excedida);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }

        if ((Integer.parseInt(parcialBonificacao.getQtde()) + qtdeAnimaisBonificados[0]) <= qtdeAnimais && edit_valor_bonificacao.getText().toString().trim().isEmpty()) {
            qtdeAnimaisBonificados[0] += Integer.parseInt(parcialBonificacao.getQtde());
            bonificacaoValidada = true;
        }

        if (!edit_quantidade_animais_bonificacao.getText().toString().trim().isEmpty() && !edit_peso_animais_bonificacao.getText().toString().trim().isEmpty() && edit_valor_bonificacao.getText().toString().trim().isEmpty()) {
            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(R.string.err_msg_valoresParcialBonificacao);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

            edit_valor_bonificacao.requestFocus();
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode((WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_dialog_categorias) {
            showCategoriasDialog(this.mViewHolder.txtCategoria.toString(), this.mViewHolder.txtRacial.toString());
        }

        if (id == R.id.btn_dialog_bonificacao) {
            if (validaQtdeAnimaisDoLote() && validaRendimento())
                showBonificacaoDialog();
        }

        if (id == R.id.btn_dialog_penalizacao) {
            if (validaQtdeAnimaisDoLote() && validaRendimento())
                showPenalizacaoDialog();
        }

//        if (id == R.id.bt_toggle_input_pasto) {
//            toggleSectionInputPasto(this.mViewHolder.bt_toggle_input_pasto);
//        }

//        if (id == R.id.bt_toggle_input_confinamento) {
//            toggleSectionInputConfinamento(this.mViewHolder.bt_toggle_input_confinamento);
//        }

        if (id == R.id.btn_rendimento_carcaca) {

            calculaRendimento();
        }

        if (id == R.id.btn_dados_acabamento) {

            calcularAcabamento();
        }

        if (id == R.id.btn_maturidade) {

            calcularMaturidade();
        }

        if (id == R.id.btn_calcular_acerto) {

            calcularAcerto();

        }

    }

    private void calcularAcerto() {
        Double totalBruto = 0.0;
        Double taxaAntecipacao = 0.0;
        Double arrobaRecebida = 0.0;
        Double descontoFunrural = 0.0;
        Double totalLiquido = 0.0;
        String arrobaNegociada;

        try {

            if (!edit_acerto_total_bruto.getText().toString().trim().isEmpty() || !edit_arroba_negociada.getText().toString().trim().isEmpty()) {

                if (!edit_acerto_total_bruto.getText().toString().trim().isEmpty()) {
                    totalBruto = Double.valueOf(edit_acerto_total_bruto.getText().toString());

                    if (check_a_prazo.isChecked() || check_a_vista.isChecked()) {

                        if (check_a_prazo.isChecked()) {
                            if (!edit_arroba_negociada_a_prazo.getText().toString().trim().isEmpty()) {
                                diasAcerto = Integer.parseInt(edit_arroba_negociada_a_prazo.getText().toString());
                            } else {
                                Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento_dias_a_prazo, Snackbar.LENGTH_SHORT).show();
                                diasAcerto = 0;
                            }
                        }

                        if (!desativaFunrural) {
                            if (edit_acerto_desconto_funrural.getText().toString().trim().isEmpty()) {
                                descontoFunrural = totalBruto * FUNRURAL;
                            } else {
                                descontoFunrural = Double.valueOf(edit_acerto_desconto_funrural.getText().toString());
                            }

                            totalLiquido = totalBruto - descontoFunrural;
                        } else {
                            totalLiquido = totalBruto;
                        }

                        if (!edit_acerto_percentual_antecipacao.getText().toString().trim().isEmpty()) {
                            taxaAntecipacao = (Double.valueOf(edit_acerto_percentual_antecipacao.getText().toString())) / 100;
                            totalLiquido = totalLiquido - (totalLiquido * taxaAntecipacao);
                            edit_acerto_valor_antecipacao.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", totalLiquido * taxaAntecipacao))));
                        }

                        qtdeAnimais = Integer.parseInt(this.mViewHolder.mQtdeAnimais.getText().toString());

                        arrobaRecebida = (totalLiquido / pesoTotalDoLote);
                        arrobaRecebidaComFunrural = (totalBruto / pesoTotalDoLote);

                        edit_acerto_desconto_funrural.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", descontoFunrural))));
                        edit_acerto_total_liquido.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", totalLiquido))));
                        edit_arroba_recebida.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", arrobaRecebida))));
                    } else {
                        Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento, Snackbar.LENGTH_SHORT).show();
                    }
                }

                if (!edit_arroba_negociada.getText().toString().trim().isEmpty())
                    arrobaNegociada = edit_acerto_total_bruto.getText().toString();

            } else if (check_a_prazo.isChecked() || check_a_vista.isChecked()) {

                if (check_a_prazo.isChecked() || check_a_vista.isChecked()) {

                    if (check_a_prazo.isChecked()) {
                        if (!edit_arroba_negociada_a_prazo.getText().toString().trim().isEmpty()) {
                            diasAcerto = Integer.parseInt(edit_arroba_negociada_a_prazo.getText().toString());
                        } else {
                            Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento_dias_a_prazo, Snackbar.LENGTH_SHORT).show();
                            diasAcerto = 0;
                        }
                    }
                } else {
                    Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento, Snackbar.LENGTH_SHORT).show();
                }

            } else {
                Snackbar.make(parent_view, R.string.err_msg_total_bruto, Snackbar.LENGTH_SHORT).show();
                edit_acerto_total_bruto.requestFocus();
            }

            if (check_a_prazo.isChecked() || check_a_vista.isChecked()) {

                if (check_a_prazo.isChecked()) {
                    if (!edit_arroba_negociada_a_prazo.getText().toString().trim().isEmpty()) {
                        diasAcerto = Integer.parseInt(edit_arroba_negociada_a_prazo.getText().toString());
                    } else {
                        Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento_dias_a_prazo, Snackbar.LENGTH_SHORT).show();
                        diasAcerto = 0;
                    }
                }
            } else {
                Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento, Snackbar.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            if (!check_a_vista.isChecked() && !check_a_prazo.isChecked()) {
                Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento, Snackbar.LENGTH_SHORT).show();
            }

            if (check_a_prazo.isChecked()) {
                if (diasAcerto == 0 || edit_arroba_negociada_a_prazo.getText().toString().trim().isEmpty())
                    Snackbar.make(parent_view, R.string.err_msg_forma_de_pagamento_dias_a_prazo, Snackbar.LENGTH_SHORT).show();
                edit_arroba_negociada_a_prazo.requestFocus();
            }
        }
    }

    private void calcularMaturidade() {
        try {
            Double percentualZeroDentes = Double.valueOf(this.mViewHolder.edit_quantidade_0_dentes.getText().toString());
            Double percentualDoisDentes = Double.valueOf(this.mViewHolder.edit_quantidade_2_dentes.getText().toString());
            Double percentualQuatroDentes = Double.valueOf(this.mViewHolder.edit_quantidade_4_dentes.getText().toString());
            Double percentualSeisDentes = Double.valueOf(this.mViewHolder.edit_quantidade_6_dentes.getText().toString());
            Double percentualOitoDentes = Double.valueOf(this.mViewHolder.edit_quantidade_8_dentes.getText().toString());

            qtdeAnimais = Integer.parseInt(this.mViewHolder.mQtdeAnimais.getText().toString());

            totalAnimaisMaturidade = percentualZeroDentes + percentualDoisDentes + percentualQuatroDentes + percentualSeisDentes + percentualOitoDentes;

            if (totalAnimaisMaturidade == qtdeAnimais) {

                percentualZeroDentes = (percentualZeroDentes / qtdeAnimais) * 100;
                percentualDoisDentes = (percentualDoisDentes / qtdeAnimais) * 100;
                percentualQuatroDentes = (percentualQuatroDentes / qtdeAnimais) * 100;
                percentualSeisDentes = (percentualSeisDentes / qtdeAnimais) * 100;
                percentualOitoDentes = (percentualOitoDentes / qtdeAnimais) * 100;

                String resultadoPercentualZeroDentes = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualZeroDentes)));
                String resultadoPercentualDoisDentes = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualDoisDentes)));
                String resultadoPercentualQuatroDentes = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualQuatroDentes)));
                String resultadoPercentualSeisDentes = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualSeisDentes)));
                String resultadoPercentualOitoDentes = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualOitoDentes)));

                this.mViewHolder.edit_percentual_0_dentes.setText(resultadoPercentualZeroDentes + "%");
                this.mViewHolder.edit_percentual_2_dentes.setText(resultadoPercentualDoisDentes + "%");
                this.mViewHolder.edit_percentual_4_dentes.setText(resultadoPercentualQuatroDentes + "%");
                this.mViewHolder.edit_percentual_6_dentes.setText(resultadoPercentualSeisDentes + "%");
                this.mViewHolder.edit_percentual_8_dentes.setText(resultadoPercentualOitoDentes + "%");

            } else if (totalAnimaisAcabamento < qtdeAnimais) {
                Toast.makeText(this, "Erro: A quantidade de animais informada no Acabamento é menor que a quantidades de animais do lote!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Erro: A quantidade de animais informada no Acabamento é maior que a quantidades de animais do lote!", Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {

            if (this.mViewHolder.mQtdeAnimais.getText().toString().equals("")) {
                Toast.makeText(this, "Favor informar o número de animais do lote!", Toast.LENGTH_LONG).show();
                this.mViewHolder.mQtdeAnimais.setFocusable(true);
                this.mViewHolder.mQtdeAnimais.setText("");
                this.mViewHolder.mQtdeAnimais.requestFocus();
            }
        }
    }

    private void calcularAcabamento() {
        try {
            Double percentualAusente = Double.valueOf(this.mViewHolder.edit_quantidade_ausente.getText().toString());
            Double percentualEscassoMenos = Double.valueOf(this.mViewHolder.edit_quantidade_escasso_menos.getText().toString());
            Double percentualEscasso = Double.valueOf(this.mViewHolder.edit_quantidade_escasso.getText().toString());
            Double percentualMediano = Double.valueOf(this.mViewHolder.edit_quantidade_mediano.getText().toString());
            Double percentualUniforme = Double.valueOf(this.mViewHolder.edit_quantidade_uniforme.getText().toString());
            Double percentualExcessivo = Double.valueOf(this.mViewHolder.edit_quantidade_excessivo.getText().toString());

            qtdeAnimais = Integer.parseInt(this.mViewHolder.mQtdeAnimais.getText().toString());

            totalAnimaisAcabamento = percentualAusente + percentualEscassoMenos + percentualEscasso + percentualMediano +
                    percentualUniforme + percentualExcessivo;

            if (totalAnimaisAcabamento == qtdeAnimais) {

                percentualAusente = (percentualAusente / qtdeAnimais) * 100;
                percentualEscassoMenos = (percentualEscassoMenos / qtdeAnimais) * 100;
                percentualEscasso = (percentualEscasso / qtdeAnimais) * 100;
                percentualMediano = (percentualMediano / qtdeAnimais) * 100;
                percentualUniforme = (percentualUniforme / qtdeAnimais) * 100;
                percentualExcessivo = (percentualExcessivo / qtdeAnimais) * 100;

                String resultadoPercentualAusente = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualAusente)));
                String resultadoPercentualEscassoMenos = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualEscassoMenos)));
                String resultadoPercentualEscasso = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualEscasso)));
                String resultadoPercentualMediano = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualMediano)));
                String resultadoPercentualUniforme = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualUniforme)));
                String resultadoPercentualExcessivo = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", percentualExcessivo)));

                this.mViewHolder.edit_percentual_ausente.setText(resultadoPercentualAusente + "%");
                this.mViewHolder.edit_percentual_escasso_menos.setText(resultadoPercentualEscassoMenos + "%");
                this.mViewHolder.edit_percentual_escasso.setText(resultadoPercentualEscasso + "%");
                this.mViewHolder.edit_percentual_mediano.setText(resultadoPercentualMediano + "%");
                this.mViewHolder.edit_percentual_uniforme.setText(resultadoPercentualUniforme + "%");
                this.mViewHolder.edit_percentual_excessivo.setText(resultadoPercentualExcessivo + "%");
            } else if (totalAnimaisAcabamento < qtdeAnimais) {
                Toast.makeText(this, "Erro: A quantidade de animais informada no Acabamento é menor que a quantidades de animais do lote!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Erro: A quantidade de animais informada no Acabamento é maior que a quantidades de animais do lote!", Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {

            if (this.mViewHolder.mQtdeAnimais.getText().toString().equals("")) {
                Toast.makeText(this, "Favor informar o número de animais do lote!", Toast.LENGTH_LONG).show();
                this.mViewHolder.mQtdeAnimais.setFocusable(true);
                this.mViewHolder.mQtdeAnimais.setText("");
                this.mViewHolder.mQtdeAnimais.requestFocus();
            }
        }
    }

    private void calculaRendimento() {
        validaDadosDoRendimento();

        try {
            pesoFazendaKilo = (Double.valueOf(this.mViewHolder.pesoFazenda.getText().toString()));
            pesoCarcacaKilo = (Double.valueOf(this.mViewHolder.pesoCarcaca.getText().toString()));
        } catch (NumberFormatException e) {
            pesoFazendaKilo = 0.0;
            pesoCarcacaKilo = 0.0;
        }

        if (pesoFazendaKilo != 0.0 && pesoCarcacaKilo != 0.0) {
            if (validaQtdeAnimaisDoLote()) {
                resultadoPesoFazendaArroba = pesoFazendaKilo / CALCULOPESOFAZENDA;
                String textArrobaFazenda = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoPesoFazendaArroba)));

                resultadoPesoCarcacaArroba = pesoCarcacaKilo / CALCULOPESOCARCACA;
                String textArrobaCarcaca = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoPesoCarcacaArroba)));
                qtdeAnimais = Integer.parseInt(this.mViewHolder.mQtdeAnimais.getText().toString());
                pesoTotalDoLote = resultadoPesoCarcacaArroba * qtdeAnimais;

                this.mViewHolder.text_arroba_fazenda.setText(String.valueOf(textArrobaFazenda + "@"));
                this.mViewHolder.text_arroba_carcaca.setText(String.valueOf(textArrobaCarcaca + "@"));

                resultadoRendimentoCarcaca = (pesoCarcacaKilo / pesoFazendaKilo) * 100;
                String textREndimentoCarcaca = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoRendimentoCarcaca)));

                this.mViewHolder.rendimentoCarcaca.setText(String.valueOf(textREndimentoCarcaca + "%"));

                if (!this.mViewHolder.txtCategoria.getText().toString().trim().isEmpty()) {
                    Double somaPesoBezerros;
                    if (abate == null) {
                        somaPesoBezerros = (Double.valueOf(edt_pequeno.getText().toString()) * 8) + (Double.valueOf(edt_medio.getText().toString()) * 16) + (Double.valueOf(edt_grande.getText().toString()) * 32);
                    } else {
                        somaPesoBezerros = (Double.valueOf(((TextView) findViewById(R.id.tv_bezerros_pequeno)).getText().toString()) * 8) + (Double.valueOf(((TextView) findViewById(R.id.tv_bezerros_medio)).getText().toString()) * 16) + (Double.valueOf(((TextView) findViewById(R.id.tv_bezerros_grande)).getText().toString()) * 32);
                    }

                    if (somaPesoBezerros > 0) {
                        Double pesoFazendaKiloEstimado = (pesoFazendaKilo - (somaPesoBezerros / qtdeAnimais));

                        Double resultadoRendimentoCarcacaEstimado = (pesoCarcacaKilo / pesoFazendaKiloEstimado) * 100;
                        String textREndimentoCarcacaEstimado = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoRendimentoCarcacaEstimado)));

                        txt_rendimento_estimado.setText(String.valueOf(textREndimentoCarcacaEstimado + "%"));
                    }
                }
            }
        } else if (!this.mViewHolder.pesoCarcaca.getText().toString().equals("")) {
            pesoCarcacaKilo = (Double.valueOf(this.mViewHolder.pesoCarcaca.getText().toString()));
            resultadoPesoCarcacaArroba = pesoCarcacaKilo / CALCULOPESOCARCACA;
            String textArrobaCarcaca = String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", resultadoPesoCarcacaArroba)));
            this.mViewHolder.text_arroba_carcaca.setText(String.valueOf(textArrobaCarcaca + "@"));
            this.mViewHolder.rendimentoCarcaca.setText("%");
            qtdeAnimais = Integer.parseInt(this.mViewHolder.mQtdeAnimais.getText().toString());
            pesoTotalDoLote = resultadoPesoCarcacaArroba * qtdeAnimais;

            txt_rendimento_estimado.setText("-");

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

//        if (id == R.id.check_grao_inteiro) {
//            if (isChecked) {
//                spn_grao_inteiro.setEnabled(true);
//            } else {
//                spn_grao_inteiro.setEnabled(false);
//            }
//        }
//
//        if (id == R.id.check_convencional) {
//            if (isChecked) {
//                spn_convencional.setEnabled(true);
//            } else {
//                spn_convencional.setEnabled(false);
//            }
//        }
//
//        if (check_semi_confinamento.isChecked()) {
//            spn_semi_confinamento.setEnabled(true);
//            spn_pasto.setEnabled(false);
//        } else {
//            spn_semi_confinamento.setEnabled(false);
//            spn_pasto.setEnabled(true);
//        }

        if (id == R.id.check_ausente) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_ausente.setFocusable(true);
                this.mViewHolder.edit_quantidade_ausente.setEnabled(true);
                this.mViewHolder.edit_quantidade_ausente.setText("");
                this.mViewHolder.edit_quantidade_ausente.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_ausente.setFocusable(true);
                this.mViewHolder.edit_quantidade_ausente.setEnabled(false);
                this.mViewHolder.edit_quantidade_ausente.setText("0");
                this.mViewHolder.edit_percentual_ausente.setText("%");
            }
        }

        if (id == R.id.check_escasso_menos) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_escasso_menos.setFocusable(true);
                this.mViewHolder.edit_quantidade_escasso_menos.setEnabled(true);
                this.mViewHolder.edit_quantidade_escasso_menos.setText("");
                this.mViewHolder.edit_quantidade_escasso_menos.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_escasso_menos.setFocusable(true);
                this.mViewHolder.edit_quantidade_escasso_menos.setEnabled(false);
                this.mViewHolder.edit_quantidade_escasso_menos.setText("0");
                this.mViewHolder.edit_percentual_escasso_menos.setText("%");
            }
        }

        if (id == R.id.check_escasso) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_escasso.setFocusable(true);
                this.mViewHolder.edit_quantidade_escasso.setEnabled(true);
                this.mViewHolder.edit_quantidade_escasso.setText("");
                this.mViewHolder.edit_quantidade_escasso.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_escasso.setFocusable(true);
                this.mViewHolder.edit_quantidade_escasso.setEnabled(false);
                this.mViewHolder.edit_quantidade_escasso.setText("0");
                this.mViewHolder.edit_percentual_escasso.setText("%");
            }
        }

        if (id == R.id.check_mediano) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_mediano.setFocusable(true);
                this.mViewHolder.edit_quantidade_mediano.setEnabled(true);
                this.mViewHolder.edit_quantidade_mediano.setText("");
                this.mViewHolder.edit_quantidade_mediano.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_mediano.setFocusable(true);
                this.mViewHolder.edit_quantidade_mediano.setEnabled(false);
                this.mViewHolder.edit_quantidade_mediano.setText("0");
                this.mViewHolder.edit_percentual_mediano.setText("%");
            }
        }

        if (id == R.id.check_uniforme) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_uniforme.setFocusable(true);
                this.mViewHolder.edit_quantidade_uniforme.setEnabled(true);
                this.mViewHolder.edit_quantidade_uniforme.setText("");
                this.mViewHolder.edit_quantidade_uniforme.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_uniforme.setFocusable(true);
                this.mViewHolder.edit_quantidade_uniforme.setEnabled(false);
                this.mViewHolder.edit_quantidade_uniforme.setText("0");
                this.mViewHolder.edit_percentual_uniforme.setText("%");
            }
        }

        if (id == R.id.check_excessivo) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_excessivo.setFocusable(true);
                this.mViewHolder.edit_quantidade_excessivo.setEnabled(true);
                this.mViewHolder.edit_quantidade_excessivo.setText("");
                this.mViewHolder.edit_quantidade_excessivo.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_excessivo.setFocusable(true);
                this.mViewHolder.edit_quantidade_excessivo.setEnabled(false);
                this.mViewHolder.edit_quantidade_excessivo.setText("0");
                this.mViewHolder.edit_percentual_excessivo.setText("%");
            }
        }

        if (id == R.id.check_0_dentes) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_0_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_0_dentes.setEnabled(true);
                this.mViewHolder.edit_quantidade_0_dentes.setText("");
                this.mViewHolder.edit_quantidade_0_dentes.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_0_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_0_dentes.setEnabled(false);
                this.mViewHolder.edit_quantidade_0_dentes.setText("0");
                this.mViewHolder.edit_percentual_0_dentes.setText("%");
            }
        }

        if (id == R.id.check_2_dentes) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_2_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_2_dentes.setEnabled(true);
                this.mViewHolder.edit_quantidade_2_dentes.setText("");
                this.mViewHolder.edit_quantidade_2_dentes.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_2_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_2_dentes.setEnabled(false);
                this.mViewHolder.edit_quantidade_2_dentes.setText("0");
                this.mViewHolder.edit_percentual_2_dentes.setText("%");
            }
        }

        if (id == R.id.check_4_dentes) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_4_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_4_dentes.setEnabled(true);
                this.mViewHolder.edit_quantidade_4_dentes.setText("");
                this.mViewHolder.edit_quantidade_4_dentes.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_4_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_4_dentes.setEnabled(false);
                this.mViewHolder.edit_quantidade_4_dentes.setText("0");
                this.mViewHolder.edit_percentual_4_dentes.setText("%");
            }
        }

        if (id == R.id.check_6_dentes) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_6_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_6_dentes.setEnabled(true);
                this.mViewHolder.edit_quantidade_6_dentes.setText("");
                this.mViewHolder.edit_quantidade_6_dentes.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_6_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_6_dentes.setEnabled(false);
                this.mViewHolder.edit_quantidade_6_dentes.setText("0");
                this.mViewHolder.edit_percentual_6_dentes.setText("%");
            }
        }

        if (id == R.id.check_8_dentes) {
            if (isChecked) {
                this.mViewHolder.edit_quantidade_8_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_8_dentes.setEnabled(true);
                this.mViewHolder.edit_quantidade_8_dentes.setText("");
                this.mViewHolder.edit_quantidade_8_dentes.requestFocus();
            } else {
                this.mViewHolder.edit_quantidade_8_dentes.setFocusable(true);
                this.mViewHolder.edit_quantidade_8_dentes.setEnabled(false);
                this.mViewHolder.edit_quantidade_8_dentes.setText("0");
                this.mViewHolder.edit_percentual_8_dentes.setText("%");
            }
        }

        if (id == R.id.check_funrural) {
            if (isChecked) {
                edit_acerto_desconto_funrural.setEnabled(false);
                edit_acerto_desconto_funrural.setText("");
                desativaFunrural = true;
            } else {
                desativaFunrural = false;
                edit_acerto_desconto_funrural.setEnabled(true);
                edit_acerto_desconto_funrural.setText("");
            }
        }

        if (id == R.id.check_a_vista) {
            if (isChecked) {
                prazoAcerto = "À vista";
                check_a_prazo.setChecked(false);
                edit_arroba_negociada_a_prazo.setEnabled(false);
            }
        }

        if (id == R.id.check_a_prazo) {
            if (isChecked) {
                prazoAcerto = "A prazo";
                check_a_vista.setChecked(false);
                edit_arroba_negociada_a_prazo.setEnabled(true);
                edit_arroba_negociada_a_prazo.requestFocus();
            }
        }

    }

//    private void toggleSectionInputPasto(View view) {
//        boolean show = toggleArrow(view);
//        if (show) {
//            ViewAnimation.expand(lyt_expand_pasto, new ViewAnimation.AnimListener() {
//                @Override
//                public void onFinish() {
//                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_pasto);
//                }
//            });
//        } else {
//            ViewAnimation.collapse(lyt_expand_pasto);
//        }
//    }

//    private void toggleSectionInputConfinamento(View view) {
//        boolean show = toggleArrow(view);
//        if (show) {
//            ViewAnimation.expand(lyt_expand_confinamento, new ViewAnimation.AnimListener() {
//                @Override
//                public void onFinish() {
//                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_confinamento);
//                }
//            });
//        } else {
//            ViewAnimation.collapse(lyt_expand_confinamento);
//        }
//    }

//    public boolean toggleArrow(View view) {
//        if (view.getRotation() == 0) {
//            view.animate().setDuration(200).rotation(180);
//            return true;
//        } else {
//            view.animate().setDuration(200).rotation(0);
//            return false;
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.listView_bonificacao_parcial) {
            ParcialBonificacao parcialBonificacao = parciaisBonificacao.get(position);

            showCustomDialogParcialBonificacao(parcialBonificacao, this);
        }

        if (parent.getId() == R.id.listView_penalizacao_parcial) {
            ParcialPenalizacao parcialPenalizacao = parciaisPenalizacao.get(position);

            showCustomDialogParcialPenalizacao(parcialPenalizacao, this);
        }

        if (parent.getId() == R.id.listViewBonificacao) {
            Bonificacao bonificacao = bonificacoes.get(position);

            showCustomDialogBonificacao(bonificacao, this);
        }

        if (parent.getId() == R.id.listViewPenalizacao) {
            Penalizacao penalizacao = penalizacoes.get(position);

            showCustomDialogPenalizacao(penalizacao, this);
        }

    }

    private void showPenalizacaoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_penalizacao);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edit_penalizacao_descricao = dialog.findViewById(R.id.edit_penalizacao_descricao);
        edit_peso_animais_penalizacao = dialog.findViewById(R.id.edit_peso_animais_penalizacao);
        edit_penalizacao_desconto = dialog.findViewById(R.id.edit_penalizacao_desconto);
        edit_penalizacao_observacoes = dialog.findViewById(R.id.edit_penalizacao_observacoes);

        edit_penalizacao_observacoes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.edit_penalizacao_observacoes) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        edit_quantidade_animais_penalizacao = dialog.findViewById(R.id.edit_quantidade_animais_penalizacao);
//        edit_penalizacao_total = dialog.findViewById(R.id.edit_penalizacao_total);
//        edit_penalizacao_media_do_lote = dialog.findViewById(R.id.edit_penalizacao_media_do_lote);

        listViewParcialPenalizacao = dialog.findViewById(R.id.listView_penalizacao_parcial);
        parciaisPenalizacao = new ArrayList<>();
        final Button adicionaParcialPenalizacao = dialog.findViewById(R.id.btn_add_parcial_penalizacao);
//        final Button calcularPenalizacao = dialog.findViewById(R.id.btn_calcular_penalizacao);

        adapterParciaisPenalizacao = new AdapterParciaisPenalizacoesPersonalizado(parciaisPenalizacao, this);
        listViewParcialPenalizacao.setAdapter(adapterParciaisPenalizacao);
        setListViewHeightBasedOnChildren(listViewParcialPenalizacao);

        listViewParcialPenalizacao.setOnItemClickListener(this);

        adicionaParcialPenalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParcialPenalizacao parcialPenalizacao = new ParcialPenalizacao();
                parcialPenalizacao.setDescricao(edit_penalizacao_descricao.getText().toString());
                parcialPenalizacao.setQtde(edit_quantidade_animais_penalizacao.getText().toString());
                parcialPenalizacao.setPeso(edit_peso_animais_penalizacao.getText().toString());
                parcialPenalizacao.setDesconto(edit_penalizacao_desconto.getText().toString());

                validaParcialPenalizacao(parcialPenalizacao);

//                    if (!edit_peso_animais_penalizacao.getText().toString().trim().isEmpty()) {
//                        Double pesoParcial = Double.parseDouble(edit_peso_animais_penalizacao.getText().toString());
//
//                        if (!edit_penalizacao_desconto.getText().toString().isEmpty()) {
//                            Double valorPenalizacaoParcial = Double.parseDouble(edit_penalizacao_desconto.getText().toString());
//                            Double valorTotalParcialPenalizacao = (pesoParcial / 15) * valorPenalizacaoParcial;
//                            parcialPenalizacao.setValorPenalizacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcialPenalizacao))));
//                        } else {
//                            Snackbar.make(parent_view, R.string.err_msg_valoresParcialPenalizacao, Snackbar.LENGTH_SHORT).show();
//                            edit_penalizacao_desconto.requestFocus();
//                        }
//
//                    } else if (!edit_penalizacao_desconto.getText().toString().trim().isEmpty()) {
//                        Double valorPenalizacaoParcial = Double.parseDouble(edit_penalizacao_desconto.getText().toString());
//                        Double valorTotalParcialPenalizacao = valorPenalizacaoParcial;
//                        parcialPenalizacao.setValorPenalizacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcialPenalizacao))));
//                    }
//
//                    parciaisPenalizacao.add(parcialPenalizacao);
//                    adapterParciaisPenalizacao.notifyDataSetChanged();
//                    setListViewHeightBasedOnChildren(listViewParcialPenalizacao);
//
//                    edit_penalizacao_descricao.setText("");
//                    edit_peso_animais_penalizacao.setText("");
//                    edit_penalizacao_desconto.setText("");
//                    edit_quantidade_animais_penalizacao.setText("");
//                    edit_penalizacao_descricao.requestFocus();
//                }
            }
        });

        final int[] qtdeAnimaisPenalizados = {0};

//        calcularPenalizacao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Double valorTotalPenalizacao = 0.0;
//                Double mediaTotalPenalizacao = 0.0;
//
//                for (ParcialPenalizacao parcialPenalizacao : parciaisPenalizacao) {
//
//                    valorTotalPenalizacao += Double.parseDouble(parcialPenalizacao.getValorPenalizacao());
//                    qtdeAnimaisPenalizados[0] += Integer.parseInt(parcialPenalizacao.getQtde());
//                }
//
//                if (validaQtdeAnimaisDoLote()) {
//                    mediaTotalPenalizacao = valorTotalPenalizacao / pesoTotalDoLote;
//                    edit_penalizacao_total.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalPenalizacao))));
//                    edit_penalizacao_media_do_lote.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalPenalizacao))) + "/@");
//                }
//            }
//        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qtdeAnimaisPenalizados[0] = 0;
                        valorTotalBonificacao = 0.0;
                        dialog.dismiss();
                    }
                });
        ((Button) dialog.findViewById(R.id.bt_save)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double mediaParcialPenalizacao = 0.0;
                        Double valorParcial = 0.0;

                        if (parciaisPenalizacao.size() > 0) {

                            for (ParcialPenalizacao parcialPenalizacao : parciaisPenalizacao) {

                                Penalizacao penalizacao = new Penalizacao();
                                penalizacao.setDescricao(parcialPenalizacao.getDescricao());

                                if (Integer.parseInt(parcialPenalizacao.getQtde()) > 1)
                                    penalizacao.setQuantidade(String.valueOf(parcialPenalizacao.getQtde()) + " animais penalizados");
                                else
                                    penalizacao.setQuantidade(String.valueOf(parcialPenalizacao.getQtde()) + " animal penalizado");

                                penalizacao.setObservacoes(edit_penalizacao_observacoes.getText().toString());

                                if (!parcialPenalizacao.getPeso().equals("")) {
                                    penalizacao.setPesoAnimaisPenalizados(parcialPenalizacao.getPeso());
                                    penalizacao.setTotal(parcialPenalizacao.getValorPenalizacao());
                                    valorParcial = ((Double.parseDouble(parcialPenalizacao.getValorPenalizacao()) / pesoTotalDoLote));

                                } else if (!parcialPenalizacao.getDesconto().equals("")) {
                                    penalizacao.setTotal(parcialPenalizacao.getValorPenalizacao());
                                    valorParcial = ((Double.parseDouble(parcialPenalizacao.getValorPenalizacao()) / pesoTotalDoLote));
                                }


                                if (validaQtdeAnimaisDoLote()) {
                                    mediaParcialPenalizacao = Double.valueOf(String.format(Locale.US, "%.2f", valorParcial));
                                    penalizacao.setMedia(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", mediaParcialPenalizacao))));
                                }

                                penalizacoes.add(penalizacao);
                                adapterPenalizacao.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(listViewPenalizacao);

                                atualizaTotalPenalizacao();

                                dialog.dismiss();
                            }
                        } else {
                            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                            TextView text = layout.findViewById(R.id.text);
                            text.setText(R.string.err_msg_parcialPenalizacao);

                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            edit_penalizacao_descricao.requestFocus();
                        }
                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private void showCustomDialogParcialPenalizacao(final ParcialPenalizacao parcialPenalizacao, final Activity activity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parciaisPenalizacao.remove(parcialPenalizacao);
                adapterParciaisPenalizacao = new AdapterParciaisPenalizacoesPersonalizado(parciaisPenalizacao, activity);
                listViewParcialPenalizacao.setAdapter(adapterParciaisPenalizacao);
                setListViewHeightBasedOnChildren(listViewParcialPenalizacao);

                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_quantidade_animais_penalizacao.setText(parcialPenalizacao.getQtde());
                edit_peso_animais_penalizacao.setText(parcialPenalizacao.getPeso());
                edit_penalizacao_desconto.setText(parcialPenalizacao.getDesconto());
                edit_penalizacao_descricao.setText(parcialPenalizacao.getDescricao());
//                edit_penalizacao_total.setText(parcialPenalizacao.getValorPenalizacao());

                parciaisPenalizacao.remove(parcialPenalizacao);
                adapterParciaisPenalizacao = new AdapterParciaisPenalizacoesPersonalizado(parciaisPenalizacao, activity);
                listViewParcialPenalizacao.setAdapter(adapterParciaisPenalizacao);
                setListViewHeightBasedOnChildren(listViewParcialPenalizacao);

                dialog.dismiss();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void atualizaTotalPenalizacao() {
        Double totalPenalizacao = 0.0;
        Double totalMediaPenalizacao = 0.0;

        for (Penalizacao penalizacaoRetornada : penalizacoes) {
            if (penalizacaoRetornada.getTotal() != null) {
                totalPenalizacao += Double.valueOf(penalizacaoRetornada.getTotal());
                totalMediaPenalizacao += Double.valueOf(penalizacaoRetornada.getMedia());
            }
        }

        if (!totalPenalizacao.equals(0.0))
            penalizacaoTotal.setText(String.format(Locale.US, "%.2f", totalPenalizacao) + " (R$ " + (String.format(Locale.US, "%.2f", totalMediaPenalizacao) + "/@)"));
        else
            penalizacaoTotal.setText("-");

        totalPenalizacao = 0.0;
    }

    private boolean validaParcialPenalizacao(ParcialPenalizacao parcialPenalizacao) {
        if (parcialPenalizacao.getQtde().equals("")) {
            View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = layout.findViewById(R.id.text);
            text.setText(R.string.err_msg_valoresParcialPenalizacao);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }

        if (!edit_peso_animais_penalizacao.getText().toString().trim().isEmpty()) {
            Double pesoParcial = Double.parseDouble(edit_peso_animais_penalizacao.getText().toString());

            if (!edit_penalizacao_desconto.getText().toString().isEmpty()) {
                Double valorPenalizacaoParcial = Double.parseDouble(edit_penalizacao_desconto.getText().toString());
                Double valorTotalParcialPenalizacao = (pesoParcial / 15) * valorPenalizacaoParcial;
                parcialPenalizacao.setValorPenalizacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcialPenalizacao))));
            } else {
                Toast.makeText(this, R.string.err_msg_valoresParcialPenalizacao, Toast.LENGTH_SHORT).show();
                edit_penalizacao_desconto.requestFocus();

                return false;
            }

        } else if (!edit_penalizacao_desconto.getText().toString().trim().isEmpty()) {
            Double valorPenalizacaoParcial = Double.parseDouble(edit_penalizacao_desconto.getText().toString());
            Double valorTotalParcialPenalizacao = valorPenalizacaoParcial;
            parcialPenalizacao.setValorPenalizacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcialPenalizacao))));
        }

        parciaisPenalizacao.add(parcialPenalizacao);
        adapterParciaisPenalizacao.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listViewParcialPenalizacao);

        edit_penalizacao_descricao.setText("");
        edit_peso_animais_penalizacao.setText("");
        edit_penalizacao_desconto.setText("");
        edit_quantidade_animais_penalizacao.setText("");
        edit_penalizacao_descricao.requestFocus();

        return true;
    }

    private void showCustomDialogPenalizacao(final Penalizacao penalizacao, final Activity activity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning_bonificacao);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                penalizacoes.remove(penalizacao);
                adapterPenalizacao = new AdapterPenalizacoesPersonalizado(penalizacoes, activity);
                listViewPenalizacao.setAdapter(adapterPenalizacao);
                setListViewHeightBasedOnChildren(listViewPenalizacao);

                atualizaTotalPenalizacao();

                dialog.dismiss();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showBonificacaoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_bonificacao);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final AppCompatSpinner spn_tipo_bonificacao = dialog.findViewById(R.id.spn_tipo_bonificacao);
        edit_quantidade_animais_bonificacao = dialog.findViewById(R.id.edit_quantidade_animais_bonificacao);
        edit_bonificacao_total = dialog.findViewById(R.id.edit_bonificacao_total);
        edit_bonificacao_media_do_lote = dialog.findViewById(R.id.edit_bonificacao_media_do_lote);
        edit_bonificacao_outras = dialog.findViewById(R.id.edit_bonificacao_outras);

        listViewParcialBonificacao = dialog.findViewById(R.id.listView_bonificacao_parcial);
        parciaisBonificacao = new ArrayList<>();
        final Button adicionaParcialBonificacao = dialog.findViewById(R.id.btn_add_parcial_bonificacao);
        final Button calcularBonificacao = dialog.findViewById(R.id.btn_calcular_bonificacao);

        edit_peso_animais_bonificacao = dialog.findViewById(R.id.edit_peso_animais_bonificacao);
        edit_valor_bonificacao = dialog.findViewById(R.id.edit_valor_bonificacao);
        edit_bonificacao_observacoes = dialog.findViewById(R.id.edit_bonificacao_observacoes);

        edit_bonificacao_observacoes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.edit_bonificacao_observacoes) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        adapterParciaisBonificacao = new AdapterParciaisBonificacoesPersonalizado(parciaisBonificacao, this);
        listViewParcialBonificacao.setAdapter(adapterParciaisBonificacao);
        setListViewHeightBasedOnChildren(listViewParcialBonificacao);

        listViewParcialBonificacao.setOnItemClickListener(this);

        spn_tipo_bonificacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 8) {

                    edit_bonificacao_outras.setEnabled(true);
                    requestFocus(edit_bonificacao_outras);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adicionaParcialBonificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double pesoParcial = 0.0;
                Double valorBonificacaoParcial = 0.0;
                Double valorTotalParcial = 0.0;

                ParcialBonificacao parcialBonificacao = new ParcialBonificacao();
                if (spn_tipo_bonificacao.getSelectedItem().toString().equals("Outros")) {
                    parcialBonificacao.setTipoBonificacao(edit_bonificacao_outras.getText().toString());
                } else {
                    parcialBonificacao.setTipoBonificacao(spn_tipo_bonificacao.getSelectedItem().toString());
                }
                parcialBonificacao.setQtde(edit_quantidade_animais_bonificacao.getText().toString());
                parcialBonificacao.setPeso(edit_peso_animais_bonificacao.getText().toString());
                parcialBonificacao.setValor_bonificacao(edit_valor_bonificacao.getText().toString());

                if (validaParcialBonificacao(parcialBonificacao)) {
                    parciaisBonificacao.add(parcialBonificacao);
                    adapterParciaisBonificacao.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(listViewParcialBonificacao);

                    if (!edit_peso_animais_bonificacao.getText().toString().trim().isEmpty() && !edit_valor_bonificacao.getText().toString().trim().isEmpty()) {
                        pesoParcial = Double.parseDouble(edit_peso_animais_bonificacao.getText().toString());
                        valorBonificacaoParcial = Double.parseDouble(edit_valor_bonificacao.getText().toString());
                        valorTotalParcial = (pesoParcial / 15) * valorBonificacaoParcial;
                        parcialBonificacao.setValor_bonificacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcial))));
                    } else if (!edit_valor_bonificacao.getText().toString().trim().isEmpty()) {
                        valorBonificacaoParcial = Double.parseDouble(edit_valor_bonificacao.getText().toString());
                        valorTotalParcial = valorBonificacaoParcial;
                        parcialBonificacao.setValor_bonificacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcial))));
                    }

                    edit_peso_animais_bonificacao.setText("");
                    edit_valor_bonificacao.setText("");
                    edit_quantidade_animais_bonificacao.setText("");
                    edit_quantidade_animais_bonificacao.requestFocus();
                }
            }
        });

        calcularBonificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double pesoParcial = 0.0;
                Double valorBonificacaoParcial = 0.0;
                Double valorTotalParcial = 0.0;

                valorTotalBonificacao = 0.0;
                qtdeAnimaisBonificados[0] = 0;

                for (ParcialBonificacao parcial : parciaisBonificacao) {
                    try {
                        validaParcialBonificacao(parcial);
                    } catch (Exception e) {
                        Snackbar.make(parent_view, R.string.err_msg_parcialBonificacao, Snackbar.LENGTH_SHORT).show();
                    }

//                    if (!parcial.getPeso().equals("0.0") && !parcial.getValor_bonificacao().equals("0.0")) {
//                        pesoParcial = Double.parseDouble(parcial.getPeso());
//                        valorBonificacaoParcial = Double.parseDouble(parcial.getValor_bonificacao());
//                        valorTotalParcial = (pesoParcial / 15) * valorBonificacaoParcial;
//                        parcial.setValor_bonificacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcial))));
//                    } else if (!parcial.getValor_bonificacao().equals("0.0")) {
//                        valorBonificacaoParcial = Double.parseDouble(parcial.getValor_bonificacao());
//                        valorTotalParcial = valorBonificacaoParcial;
//                        parcial.setValor_bonificacao(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalParcial))));
//                    }

                    if (!parcial.getValor_bonificacao().equals("")) {
                        valorTotalBonificacao += Double.parseDouble(parcial.getValor_bonificacao());
//                        qtdeAnimaisBonificados[0] += Integer.parseInt(parcial.getQtde());
                    }
                }

                if (!valorTotalBonificacao.equals(0.0)) {

                    if (validaQtdeAnimaisDoLote() && validaBonificacao(parciaisBonificacao.size())) {
                        mediaTotalBonificacao = valorTotalBonificacao / pesoTotalDoLote;
                        edit_bonificacao_total.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", valorTotalBonificacao))));
                        edit_bonificacao_media_do_lote.setText(String.valueOf(Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalBonificacao))));
                        bonificacaoValidada = true;
                    }
                }
            }
        });

        String[] bonificacoesTipo = getResources().getStringArray(R.array.bonificacao);
        ArrayAdapter<String> array = new ArrayAdapter<>(this, R.layout.simple_spinner_item, bonificacoesTipo);
        array.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spn_tipo_bonificacao.setAdapter(array);
        spn_tipo_bonificacao.setSelection(0);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qtdeAnimaisBonificados[0] = 0;
                        valorTotalBonificacao = 0.0;
                        dialog.dismiss();
                    }
                });
        ((Button) dialog.findViewById(R.id.bt_save)).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validaBonificacaoCalculada() && validaBonificacao(parciaisBonificacao.size())) {
                            Bonificacao bonificacao = new Bonificacao();
                            bonificacao.setTipo(spn_tipo_bonificacao.getSelectedItem().toString());

                            if (qtdeAnimaisBonificados[0] > 1)
                                bonificacao.setQtde(String.valueOf(qtdeAnimaisBonificados[0]) + " animais bonificados");
                            else
                                bonificacao.setQtde(String.valueOf(qtdeAnimaisBonificados[0]) + " animal bonificado");


                            bonificacao.setTotal(edit_bonificacao_total.getText().toString());
                            bonificacao.setMediaLote(edit_bonificacao_media_do_lote.getText().toString());
                            bonificacaoObservacoes = edit_bonificacao_observacoes.getText().toString();
                            bonificacao.setObservacoes(bonificacaoObservacoes);
                            mediaTotalLote += Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalBonificacao));
                            bonificacoes.add(bonificacao);
                            adapterBonificacao.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listViewBonificacao);

                            atualizaTotalBonificacao();

                            qtdeAnimaisBonificados[0] = 0;
                            valorTotalBonificacao = 0.0;
                            bonificacaoValidada = false;

                            dialog.dismiss();
                        } else {
                            if (bonificacaoValidada && validaBonificacao(parciaisBonificacao.size())) {
                                Bonificacao bonificacao = new Bonificacao();
                                bonificacao.setTipo(spn_tipo_bonificacao.getSelectedItem().toString());

                                if (qtdeAnimaisBonificados[0] > 1)
                                    bonificacao.setQtde(String.valueOf(qtdeAnimaisBonificados[0]) + " animais bonificados");
                                else
                                    bonificacao.setQtde(String.valueOf(qtdeAnimaisBonificados[0]) + " animal bonificado");

                                bonificacaoObservacoes = edit_bonificacao_observacoes.getText().toString();
                                bonificacao.setObservacoes(bonificacaoObservacoes);
                                bonificacoes.add(bonificacao);
                                adapterBonificacao.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(listViewBonificacao);

                                atualizaTotalBonificacao();

                                qtdeAnimaisBonificados[0] = 0;
                                valorTotalBonificacao = 0.0;
                                bonificacaoValidada = false;

                                dialog.dismiss();
                            } else {
                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText(R.string.err_msg_BonificacaoCalculada);

                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                                edit_bonificacao_total.requestFocus();
                            }
                        }

                    }
                });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private void showCustomDialogParcialBonificacao(final ParcialBonificacao parcialBonificacao, final Activity activity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parciaisBonificacao.remove(parcialBonificacao);
                qtdeAnimaisBonificados[0] -= Integer.parseInt(parcialBonificacao.getQtde());
                adapterParciaisBonificacao = new AdapterParciaisBonificacoesPersonalizado(parciaisBonificacao, activity);
                listViewParcialBonificacao.setAdapter(adapterParciaisBonificacao);
                setListViewHeightBasedOnChildren(listViewParcialBonificacao);

                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_quantidade_animais_bonificacao.setText(parcialBonificacao.getQtde());
                edit_peso_animais_bonificacao.setText(parcialBonificacao.getPeso());
                edit_valor_bonificacao.setText(parcialBonificacao.getValor_bonificacao());

                parciaisBonificacao.remove(parcialBonificacao);
                qtdeAnimaisBonificados[0] -= Integer.parseInt(parcialBonificacao.getQtde());
                adapterParciaisBonificacao = new AdapterParciaisBonificacoesPersonalizado(parciaisBonificacao, activity);
                listViewParcialBonificacao.setAdapter(adapterParciaisBonificacao);
                setListViewHeightBasedOnChildren(listViewParcialBonificacao);

                dialog.dismiss();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showCustomDialogBonificacao(final Bonificacao bonificacao, final Activity activity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning_bonificacao);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bonificacoes.remove(bonificacao);
                adapterBonificacao = new AdapterBonificacoesPersonalizado(bonificacoes, activity);
                listViewBonificacao.setAdapter(adapterBonificacao);
                setListViewHeightBasedOnChildren(listViewBonificacao);

                if (bonificacao.getMediaLote() != null)
                    mediaTotalLote -= Double.valueOf(bonificacao.getMediaLote());

                atualizaTotalBonificacao();

                dialog.dismiss();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void atualizaTotalBonificacao() {
        Double totalBonificacao = 0.0;

        for (Bonificacao bonificacaoRetornada : bonificacoes) {
            if (bonificacaoRetornada.getTotal() != null)
                totalBonificacao += Double.valueOf(bonificacaoRetornada.getTotal());
        }

        if (!totalBonificacao.equals(0.0)) {
            totalBonificacao = Double.valueOf(String.format(Locale.US, "%.2f", totalBonificacao));
            mediaTotalLote = Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalLote));

            bonificacaoTotal.setText(totalBonificacao + " (+ R$ " + mediaTotalLote + "/@)");
        } else
            bonificacaoTotal.setText("-");

        valorTotalBonificacao = 0.0;
    }

    private void atualizaTotalBonificacao(Abate abate) {
        Double totalBonificacao = 0.0;

        for (Bonificacao bonificacaoRetornada : bonificacoes) {
            if (bonificacaoRetornada.getTotal() != null)
                totalBonificacao += Double.valueOf(bonificacaoRetornada.getTotal());
        }

        if (!totalBonificacao.equals(0.0)) {
            totalBonificacao = Double.valueOf(String.format(Locale.US, "%.2f", totalBonificacao));

            mediaTotalLote = Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalLote));
//            if (mediaTotalLote.equals(0.0)) {
            pesoCarcacaKilo = (Double.valueOf(this.mViewHolder.pesoCarcaca.getText().toString()));
            resultadoPesoCarcacaArroba = pesoCarcacaKilo / CALCULOPESOCARCACA;
            qtdeAnimais = Integer.parseInt(abate.getLote().getQtdeAnimaisLote());
            pesoTotalDoLote = resultadoPesoCarcacaArroba * qtdeAnimais;

            mediaTotalLote = totalBonificacao / pesoTotalDoLote;
            mediaTotalBonificacao = totalBonificacao / pesoTotalDoLote;
            mediaTotalLote = Double.valueOf(String.format(Locale.US, "%.2f", mediaTotalBonificacao));
//            }
            bonificacaoTotal.setText(totalBonificacao + " (+ R$ " + mediaTotalLote + "/@)");
        } else
            bonificacaoTotal.setText("-");

        valorTotalBonificacao = 0.0;
    }

    private void showCategoriasDialog(String categoria, String Racial) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event_categoria);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        edt_grande = dialog.findViewById(R.id.edt_grande);
        edt_medio = dialog.findViewById(R.id.edt_medio);
        edt_pequeno = dialog.findViewById(R.id.edt_pequeno);
        final AppCompatSpinner spn_categoria = dialog.findViewById(R.id.spn_categoria);
        final AppCompatSpinner spn_racial = dialog.findViewById(R.id.spn_racial);
        final CheckBox check_bezerros = dialog.findViewById(R.id.check_bezerros);
        final LinearLayout layout_bezerros = dialog.findViewById(R.id.layout_bezerros);

        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> array = new ArrayAdapter<>(this, R.layout.simple_spinner_item, categorias);
        array.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spn_categoria.setAdapter(array);
        spn_categoria.setSelection(0);

        spn_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 4 && position <= 6) {

                    check_bezerros.setVisibility(View.VISIBLE);

                    check_bezerros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                layout_bezerros.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } else {

                    check_bezerros.setVisibility(View.INVISIBLE);

                    layout_bezerros.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] racial = getResources().getStringArray(R.array.racial);
        ArrayAdapter<String> arrayRacial = new ArrayAdapter<>(this, R.layout.simple_spinner_item, racial);
        array.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spn_racial.setAdapter(arrayRacial);
        spn_racial.setSelection(0);

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Event event = new Event();
                event.categoria = spn_categoria.getSelectedItem().toString();
                event.racial = spn_racial.getSelectedItem().toString();

                if (check_bezerros.isChecked()) {
                    event.qtdGrande = edt_grande.getText().toString();
                    event.qtdMedio = edt_medio.getText().toString();
                    event.qtdPequeno = edt_pequeno.getText().toString();

                } else {
                    event.qtdGrande = "0";
                    event.qtdMedio = "0";
                    event.qtdPequeno = "0";
                }

                displayDataResult(event);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void displayDataResult(Event event) {
        this.mViewHolder.txtCategoria.setText(event.categoria);
        this.mViewHolder.txtRacial.setText(event.racial);

        if ((!event.qtdGrande.equals(""))) {
            ((TextView) findViewById(R.id.tv_bezerros_grande)).setText(event.qtdGrande);
        }

        if ((!event.qtdMedio.equals(""))) {
            ((TextView) findViewById(R.id.tv_bezerros_medio)).setText(event.qtdMedio);
        }

        if ((!event.qtdPequeno.equals(""))) {
            ((TextView) findViewById(R.id.tv_bezerros_pequeno)).setText(event.qtdPequeno);
        }
    }

    private void setListeners() {
//        this.mViewHolder.mButtonSave.setOnClickListener(this);
    }

    private void handleSave() {

        if (!this.validateSave()) {
            return;
        }
        GuestEntity guestEntity = new GuestEntity();
        guestEntity.setName(this.mViewHolder.mEditNomeCliente.getText().toString());
        guestEntity.setDocument(this.mViewHolder.mEditDocument.getText().toString());

//        if (this.mViewHolder.mRadioNotConfirmed.isChecked()) {
//            guestEntity.setConfirmed(GuestConstants.CONFIRMATION.NOT_CONFIRMED);
//        } else if (this.mViewHolder.mRadioPresent.isChecked()) {
//            guestEntity.setConfirmed(GuestConstants.CONFIRMATION.PRESENT);
//        } else {
//            guestEntity.setConfirmed(GuestConstants.CONFIRMATION.ABSENT);
//        }

        if (this.mGuestID == 0) {
            //Salva entidade convidado no BD
            if (this.mGuestBusiness.insert(guestEntity)) {
                Toast.makeText(this, R.string.salvo_com_sucesso, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.erro_ao_salvar, LENGTH_SHORT).show();
            }
        } else {

            guestEntity.setId(this.mGuestID);

            //Edita a entidade convidado no BD
            if (this.mGuestBusiness.update(guestEntity)) {
                Toast.makeText(this, R.string.salvo_com_sucesso, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.erro_ao_salvar, LENGTH_SHORT).show();
            }
        }

        finish();

    }

    private boolean validateSave() {
        if (this.mViewHolder.mEditNomeCliente.getText().toString().equals("")) {
            this.mViewHolder.mEditNomeCliente.setError(getString(R.string.nome_obrigatorio));
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private static class ViewHolder {
        EditText mEditNomeCliente;
        EditText mEditDocument;
        EditText mEditFazenda;
        EditText mQtdeAnimais;
        EditText pesoFazenda;
        EditText pesoCarcaca;
        EditText rendimentoCarcaca;
        EditText edit_quantidade_ausente;
        EditText edit_percentual_ausente;
        EditText edit_quantidade_escasso_menos;
        EditText edit_percentual_escasso_menos;
        EditText edit_percentual_escasso;
        EditText edit_quantidade_escasso;
        EditText edit_quantidade_mediano;
        EditText edit_percentual_mediano;
        EditText edit_percentual_uniforme;
        EditText edit_quantidade_uniforme;
        EditText edit_percentual_excessivo;
        EditText edit_quantidade_excessivo;
        EditText edit_quantidade_0_dentes;
        EditText edit_quantidade_2_dentes;
        EditText edit_quantidade_4_dentes;
        EditText edit_quantidade_6_dentes;
        EditText edit_quantidade_8_dentes;
        EditText edit_percentual_0_dentes;
        EditText edit_percentual_2_dentes;
        EditText edit_percentual_4_dentes;
        EditText edit_percentual_6_dentes;
        EditText edit_percentual_8_dentes;
        TextView txtCategoria;
        TextView txtRacial;
        TextView text_arroba_fazenda;
        TextView text_arroba_carcaca;
        Button mButtonSave;
        Toolbar mToolbar;
        ImageButton bt_toggle_input_pasto;
        ImageButton bt_toggle_input_confinamento;

    }
}
