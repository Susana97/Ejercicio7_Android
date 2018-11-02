
package com.example.alumno.ejercicio7_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> listaTiposUsuarios;
    private ArrayAdapter<String> adaptador;
    private Spinner tiposUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaTiposUsuarios = new ArrayList<String>();
        listaTiposUsuarios.add("Normal");
        listaTiposUsuarios.add("Avanzado");
        listaTiposUsuarios.add("Administrador");

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                listaTiposUsuarios);

        tiposUsuarios = (Spinner)findViewById(R.id.spinnerTipoUsr);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiposUsuarios.setAdapter(adaptador);



    }
}
