
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
import android.widget.TextView;
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
    private TextView textViewPrincipal;
    private TextView textViewTipoUsr;
    private TextView textViewDescrip;
    private boolean terminado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String normal = getResources().getString(R.string.usu_normal);
        String avanzado = getResources().getString(R.string.usu_avanz);
        String admin = getResources().getString(R.string.usu_admin);


        botonImagenes = (ImageButton) findViewById(R.id.imageButtonSeleccionar);
        botonEnviar = (Button) findViewById(R.id.buttonEnviar);
        textoDescripcion = (EditText) findViewById(R.id.editTextDesc);
        listaTiposUsuarios = new ArrayList<String>();
        listaTiposUsuarios.add(normal);
        listaTiposUsuarios.add(avanzado);
        listaTiposUsuarios.add(admin);

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                listaTiposUsuarios);

        tiposUsuarios = (Spinner) findViewById(R.id.spinnerTipoUsr);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiposUsuarios.setAdapter(adaptador);

        String tvPrinc = getResources().getString(R.string.titulo);
        textViewPrincipal = (TextView)findViewById(R.id.TextViewPrincipal);
        textViewPrincipal.setText(tvPrinc);

        String tipoUsr = getResources().getString(R.string.tipo_usuario);
        textViewTipoUsr = (TextView)findViewById(R.id.textViewTipoUsr);
        textViewTipoUsr.setText(tipoUsr);

        String tv_desc = getResources().getString(R.string.descripcion);
        textViewDescrip = (TextView)findViewById(R.id.textViewDesc);
        textViewDescrip.setText(tv_desc);

        String et_desc = getResources().getString(R.string.descripcion_text);
        textoDescripcion = (EditText)findViewById(R.id.editTextDesc);
        textoDescripcion.setHint(et_desc);

        String btn_enviar = getResources().getString(R.string.btn_enviar);
        botonEnviar = (Button)findViewById(R.id.buttonEnviar);
        botonEnviar.setText(btn_enviar);

        botonImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // comprobar version actual de android que estamos corriendo
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // Comprobar si ha aceptado, no ha aceptado, o nunca se le ha preguntado
                    if (CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Ha aceptado
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            return;
                        startActivity(i);
                    } else {
                        // Ha denegado o es la primera vez que se le pregunta
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // No se le ha preguntado aún
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                        } else {
                            // Ha denegado
                            String toastDecline2 = getResources().getString(R.string.perm_Noconc2);
                            Toast.makeText(MainActivity.this, toastDecline2, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(i);
                        }
                    }
                } else {
                    OlderVersions();
                }

            }

            private void OlderVersions() {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    openGalery();
                } else {
                    String toastDecline = getResources().getString(R.string.perm_Noconc);
                    Toast.makeText(MainActivity.this, toastDecline, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Se activa el progress Dialog.
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDoalog = new ProgressDialog(MainActivity.this);
                progressDoalog.setMax(100);
                String enviando = getResources().getString(R.string.enviando);
                progressDoalog.setMessage(enviando);

                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDoalog.setCancelable(false);
                terminado = true;
                String cancel = getResources().getString(R.string. cancel);
                progressDoalog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String cancelado = getResources().getString(R.string. cancelado);
                        Toast mensaje = Toast.makeText(getApplicationContext(), cancelado, Toast.LENGTH_SHORT);
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

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            String enviadoOK = getResources().getString(R.string. okenv);
                                            Toast mensaje = Toast.makeText(getApplicationContext(), enviadoOK, Toast.LENGTH_SHORT);
                                            mensaje.show();
                                        }
                                    });

                                    break;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String permission = permissions[0];
        int result = grantResults[0];

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Comprobar si ha sido aceptado o denegado la petición de permiso
            if (result == PackageManager.PERMISSION_GRANTED) {
                // Concedió su permiso
                String concedido = getResources().getString(R.string. perm_conc);
                Toast.makeText(this, concedido, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) return;
                openGalery();
            }
            else {
                // No concendió su permiso
                String noconcedido = getResources().getString(R.string. perm_Noconc);
                Toast.makeText(MainActivity.this, noconcedido, Toast.LENGTH_SHORT).show();
            }
        }
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

    private boolean CheckPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void openGalery () {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

    }
}


