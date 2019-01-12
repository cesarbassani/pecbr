package com.cesarbassani.pecbr.views;

import android.Manifest;
import android.app.AlertDialog;
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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.constants.DataBaseConstants;
import com.cesarbassani.pecbr.helper.Permissao;
import com.cesarbassani.pecbr.helper.UsuarioFirebase;
import com.cesarbassani.pecbr.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ConfigFragment extends Fragment {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private CircleImageView circularImageViewPerfil;
    private EditText editPerfilNome;
    private ImageView imageAtualizarNome;
    private View view;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private Usuario usuarioLogado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Validar permissoes
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config, container, false);

        final Context context = view.getContext();

        initComponent(view);

        imageButtonCamera = view.findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = view.findViewById(R.id.imageButtonGaleria);
        circularImageViewPerfil = view.findViewById(R.id.circleImageViewFotoPerfil);
        editPerfilNome = view.findViewById(R.id.editPerfilNome);
        imageAtualizarNome = view.findViewById(R.id.imageAtualizarNome);

        //Configuracoes iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Recuperar dados do usuário
        final FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null) {

            GlideApp.with(this.getActivity().getApplicationContext())
                    .load(url.toString())
                    .into(circularImageViewPerfil);
        } else {
            circularImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        editPerfilNome.setText(usuario.getDisplayName());

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentCamera.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intentCamera, DataBaseConstants.SELECAO_CAMERA);
                }
            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intentGaleria.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intentGaleria, DataBaseConstants.SELECAO_GALERIA);
                }
            }
        });

        imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editPerfilNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
                if (retorno) {

                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();

                    Snackbar snackbar = Snackbar.make(view, "Nome alterado com sucesso!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });


        return view;
    }

    private void initComponent(View view) {
        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case DataBaseConstants.SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case DataBaseConstants.SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imagem != null) {
                circularImageViewPerfil.setImageBitmap(imagem);

                //Recuperar os dados da imagem para o Firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosDaImagem = baos.toByteArray();

                //Salvar imagem no Firebase
                final StorageReference imagemRef = storageReference
                        .child("imagens")
                        .child("perfil")
//                        .child(identificadorUsuario)
                        .child(identificadorUsuario + ".jpeg");

                UploadTask uploadTask = imagemRef.putBytes(dadosDaImagem);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(view, "Erro ao fazer upload da imagem!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar snackbar = Snackbar.make(view, "Sucesso ao fazer upload da imagem!", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                        url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                atualizaFotoUsuario(uri);
                            }
                        });
                    }
                });
            }
        }
    }

    private void atualizaFotoUsuario(Uri url) {
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if (retorno) {
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();

            Snackbar snackbar = Snackbar.make(view, "Sua foto foi alterada com sucesso!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                Permissao.alertaValidacaoPermissao(getActivity());
            }
        }
    }

//    private void alertaValidacaoPermissao() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
//        builder.setTitle("Permissões Negadas");
//        builder.setCancelable(false);
//        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
//        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                getActivity().finish();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

}
