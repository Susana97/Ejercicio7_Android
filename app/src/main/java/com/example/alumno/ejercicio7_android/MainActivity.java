
package com.example.alumno.ejercicio7_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Environment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 1;
    private ArrayList<String> listaTiposUsuarios;
    private ArrayAdapter<String> adaptador;
    private Spinner tiposUsuarios;
    private ImageButton botonImagenes;
    private EditText textoDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonImagenes = (ImageButton)findViewById(R.id.imageButtonSeleccionar);
        textoDescripcion = (EditText)findViewById(R.id.editTextDesc);
        listaTiposUsuarios = new ArrayList<String>();
        listaTiposUsuarios.add("Normal");
        listaTiposUsuarios.add("Avanzado");
        listaTiposUsuarios.add("Administrador");

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                listaTiposUsuarios);

        tiposUsuarios = (Spinner)findViewById(R.id.spinnerTipoUsr);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiposUsuarios.setAdapter(adaptador);

        botonImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT,
                    Toast.LENGTH_LONG).show();
        } else {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PICK_FROM_GALLERY);
                Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();
            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
                openGalery();
            }
        }
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(PICK_FROM_GALLERY == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK Permissions granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                openGalery();
            } else {
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void openGalery() {
        Intent galleryIntent  = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);

    }
}
