package com.cesarbassani.pecbr.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.cesarbassani.pecbr.views.ConfigFragment;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Fragment fragment, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {

            List<String> listaPermissoes = new ArrayList<>();

            //Percorre as permissoes passadas verificando uma a uma se já tem a permissao liberada
            for (String permissao : permissoes) {
                Boolean temPermissao = ContextCompat.checkSelfPermission(fragment.getContext(), permissao) == PackageManager.PERMISSION_GRANTED;

                if (!temPermissao)
                    listaPermissoes.add(permissao);

            }

            if (listaPermissoes.isEmpty())
                return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita permissao
            fragment.requestPermissions(novasPermissoes, requestCode);
        }

        return true;
    }

    public static void alertaValidacaoPermissao(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permissões Negadas");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
