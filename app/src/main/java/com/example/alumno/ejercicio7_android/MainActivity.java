
package com.example.alumno.ejercicio7_android;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.os.Environment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private ArrayList<String> listaTiposUsuarios;
    private ArrayAdapter<String> adaptador;
    private Spinner tiposUsuarios;
    private ImageButton botonImagenes;
    private EditText textoDescripcion;
    private ProgressDialog progressDoalog;
    private Handler handle;
    private Button botonEnviar;
    private boolean terminado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonImagenes = (ImageButton) findViewById(R.id.imageButtonSeleccionar);
        botonEnviar = (Button) findViewById(R.id.buttonEnviar);
        textoDescripcion = (EditText) findViewById(R.id.editTextDesc);
        listaTiposUsuarios = new ArrayList<String>();
        listaTiposUsuarios.add("Normal");
        listaTiposUsuarios.add("Avanzado");
        listaTiposUsuarios.add("Administrador");

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                listaTiposUsuarios);

        tiposUsuarios = (Spinner) findViewById(R.id.spinnerTipoUsr);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiposUsuarios.setAdapter(adaptador);

        botonImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        //Se activa el progress Dialog.
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDoalog = new ProgressDialog(MainActivity.this);
                progressDoalog.setMax(100);
                progressDoalog.setMessage("Enviando....");

                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDoalog.setCancelable(false);
                terminado = true;
                progressDoalog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast mensaje = Toast.makeText(getApplicationContext(), "Se ha cancelado el envio", Toast.LENGTH_SHORT);
                        mensaje.show();
                    }
                });
                progressDoalog.show();

                handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        progressDoalog.incrementProgressBy(1);
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (progressDoalog.getProgress() <= progressDoalog
                                    .getMax()) {
                                Thread.sleep(100);
                                handle.sendMessage(handle.obtainMessage());
                                if (progressDoalog.getProgress() == progressDoalog
                                        .getMax()) {
                                    progressDoalog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    /*Toast mensaje = Toast.makeText(getApplicationContext(), "Se ha enviado correctamente", Toast.LENGTH_SHORT);
                                    mensaje.show(); */

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "Esta versión no es Android 6 o posterior. " + Build.VERSION.SDK_INT,
                    Toast.LENGTH_LONG).show();
        } else {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PICK_FROM_GALLERY);
                Toast.makeText(this, "Solicitando permisos", Toast.LENGTH_LONG).show();
            } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Los permisos ya han sido concedidos", Toast.LENGTH_LONG).show();
                openGalery();
            }
        }
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    private void openGalery () {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

    }
}


