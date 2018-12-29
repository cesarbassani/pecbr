package com.cesarbassani.pecbr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

public class BoasVindas extends AppCompatActivity {

    Spinner spinnerCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        spinnerCategorias = findViewById(R.id.select_categorias);
    }
}
