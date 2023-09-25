package com.corporacioncmyfv.comandassiges;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    private final int TIEMPO = 10000;
    EditText servidor;
    EditText DB;
    EditText usuario;
    EditText password;
    EditText puerto;
    EditText instancia;
    EditText edtcolumnas;
    EditText edtfilas;
    EditText edtttexto;
    EditText edtttextoampli;
    Handler handler = new Handler();
    Connection connect;
    String servidor_remoto;
    String puerto_remoto;
    String DB_remoto;
    String usuario_remoto;
    String password_remoto;
    String instancia_remoto;
    Integer colorpendientes_remoto;
    Integer coloratenciones_remoto;
    Integer colorentregables_remoto;
    Integer colorentregadas_remoto;
    Integer coloranuladas_remoto;
    Integer colortexto_remoto;
    Integer ttexto_remoto;
    Integer ttextoampli_remoto;
    Boolean checkpendiente_remoto;
    Boolean checkatencion_remoto;
    Boolean checkentregado_remoto;
    Boolean checkentregadas_remoto;
    Boolean checkanuladas_remoto;
    Boolean checkbloqueoestado_remoto;
    Boolean checkbloqueoanulacion_remoto;
    Boolean checkTI_remoto;
    Boolean checkDT_remoto;
    Boolean checkTI_remoto_historico;
    Boolean checkDT_remoto_historico;
    String password_app_remoto;
    Boolean layout_historico = false;

    SharedPreferences preferences;
    private RecyclerView rv1;
    private ArrayList<String> lista1 = new ArrayList<>();
    private ArrayList<Integer> lista2 = new ArrayList<>();
    private ArrayList<String> listaestado = new ArrayList<>();
    private ArrayList<String> listafecha = new ArrayList<>();
    private ArrayList<String> listanumero = new ArrayList<>();
    private ArrayList<Boolean> listamodifica = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private MyAdapter adapter;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;
    private AlertDialog alertDialogpassword;
    private AlertDialog alertDialogColores;
    private AlertDialog alertDialogVistas;
    private TextView tvpendiente;
    private TextView tvatencion;
    private TextView tvporentregar;
    private TextView tventregadas;
    private TextView tvanuladas;
    private TextView pendientes;
    private TextView entregas;
    private TextView atenciones;
    private TextView colortexto;
    private TextView entregadas;
    private TextView anuladas;
    private TextView tvtitulo;
    private int currentBackgroundColor = 0xFFFF9900;
    private int filas;
    private int columnas;
    private int ttexto;
    private int ttextoampli;
    private CheckBox checkpendiente;
    private CheckBox checkatencion;
    private CheckBox checkentregado;
    private CheckBox checkentregadas;
    private CheckBox checkanuladas;
    private CheckBox checkTI;
    private CheckBox checkDT;
    private CheckBox checkbloqueoestado;
    private CheckBox checkbloqueoanulacion;
    private ConstraintLayout cl_estados;
    private ConstraintLayout cl_estados_historicos;
    private ConstraintLayout cl_cambiacolor_historicos;
    private Boolean seguridad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvpendiente = findViewById(R.id.pendiente);
        tvatencion = findViewById(R.id.atencion);
        tvporentregar = findViewById(R.id.entrega);
        tvtitulo = findViewById(R.id.textcomandassiges);
        tventregadas = findViewById(R.id.entregadas);
        tvanuladas = findViewById(R.id.anuladas);
        cl_estados = findViewById(R.id.estados);
        cl_estados_historicos = findViewById(R.id.estados_historico);
        seguridad = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = getSharedPreferences("configuracion", Context.MODE_PRIVATE);
        rv1 = findViewById(R.id.rv1);
        rv1.addItemDecoration(new DividerItemDecoration(rv1.getContext(), DividerItemDecoration.VERTICAL));

        columnas = preferences.getInt("columnas", 1);

        rv1.setLayoutManager(new GridLayoutManager(this, columnas));
        rv1.setHasFixedSize(true);
        adapter = new MyAdapter(lista1);
        rv1.setAdapter(adapter);
        rv1.requestFocus();

        rv1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                columnas = preferences.getInt("columnas", 1);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    int rowCount = adapter.getItemCount() / columnas; // Número de filas
                    int itemCount = adapter.getItemCount();

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (selectedPosition >= columnas) {
                                selectedPosition -= columnas;
                                rv1.smoothScrollToPosition(selectedPosition);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (selectedPosition + columnas < itemCount) {
                                selectedPosition += columnas;
                                rv1.smoothScrollToPosition(selectedPosition);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (selectedPosition > 0) {
                                selectedPosition--;
                                rv1.smoothScrollToPosition(selectedPosition);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (selectedPosition < itemCount - 1) {
                                selectedPosition++;
                                rv1.smoothScrollToPosition(selectedPosition);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            //showToast("Item seleccionado: " + lista2.get(selectedPosition));
                            showAlertDialog(selectedPosition);
                            break;

                    }

                    return true;
                }
                return false;
            }
        });
        //BOTON RECARGAR
        ImageButton btnrecargar = findViewById(R.id.btnreload);
        btnrecargar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("checkTI",true);
                editor.putBoolean("checkDT",true);
                editor.commit();
                if (layout_historico==false){
                    layout_historico=true;
                }else{
                    layout_historico=false;
                }
                if (layout_historico==true){
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);
                }else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });


        final boolean[] isExpanded = {false};
        FloatingActionButton menuFlotante = findViewById(R.id.menuflotante);
        FloatingActionButton btnconexion = findViewById(R.id.btnconexionflotante);
        FloatingActionButton btnfiltros = findViewById(R.id.btnfiltrosflotante);
        FloatingActionButton btncolores = findViewById(R.id.btncoloresflotante);
        ConstraintLayout menuContenedorFlotante = findViewById(R.id.menucontenedorflotante);
        ConstraintLayout constraintLayout = findViewById(R.id.menucontenedorflotante);
        password_app_remoto = preferences.getString("passwordapp", "");
        if (password_app_remoto == "") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            // Inflar el diseño personalizado del AlertDialog
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_password, null);
            builder.setView(dialogView);

            // Obtener referencias a elementos del diseño personalizado
            final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
            final ImageView showHidePassword = dialogView.findViewById(R.id.showHidePassword);
            final TextView titleTextView = dialogView.findViewById(R.id.titleTextView);

            // Configurar el título del AlertDialog desde Java
            titleTextView.setText("Crea una contraseña");

            // Configurar la EditText para ser de tipo contraseña y permitir ver u ocultar el texto
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showHidePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int inputType = passwordEditText.getInputType();
                    if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    passwordEditText.setSelection(passwordEditText.getText().length()); // Colocar el cursor al final del texto
                }
            });

            // Configurar el botón de aceptar
            builder.setPositiveButton("Grabar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String password = passwordEditText.getText().toString();

                    if (!TextUtils.isEmpty(password)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("passwordapp", password);
                        editor.commit();
                        alertDialogpassword.dismiss();
                        int configuracion = consulta_configuracion();
                        if (configuracion == 0) {
                            btnrecargar.setEnabled(false);
                            configdialog();
                        } else {
                            connect = connectionclass();
                            if (connect != null) {

                                if (layout_historico == true) {
                                    consulta_historico();
                                    cl_estados.setVisibility(View.GONE);
                                    cl_estados_historicos.setVisibility(View.VISIBLE);
                                } else {
                                    consulta();
                                    cl_estados.setVisibility(View.VISIBLE);
                                    cl_estados_historicos.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                                onMapReady();

                                btnrecargar.setEnabled(true);
                            } else {
                                Toast.makeText(MainActivity.this, "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
                            }
                        }
                        tvpendiente.setBackgroundColor(colorpendientes_remoto);
                        tvpendiente.setTextColor(colortexto_remoto);
                        tvatencion.setBackgroundColor(coloratenciones_remoto);
                        tvatencion.setTextColor(colortexto_remoto);
                        tvporentregar.setBackgroundColor(colorentregables_remoto);
                        tvporentregar.setTextColor(colortexto_remoto);
                        tventregadas.setBackgroundColor(colorentregadas_remoto);
                        tventregadas.setTextColor(colortexto_remoto);
                        tvanuladas.setBackgroundColor(coloranuladas_remoto);
                        tvanuladas.setTextColor(colortexto_remoto);
                    } else {
                        // La contraseña está vacía, mostrar un mensaje o tomar otra acción si es necesario
                        //Toast.makeText(MainActivity.this, "La contraseña está vacía", Toast.LENGTH_SHORT).show();
                        passwordEditText.setError("La contraseña no puede estar vacía");
                        // No cierres el diálogo aquí
                    }
                }
            });

            // Crear y mostrar el AlertDialog
            alertDialogpassword = builder.create();
            alertDialogpassword.show();
            // Personalizar el comportamiento de cierre del diálogo
            alertDialogpassword.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = passwordEditText.getText().toString();

                    // Verificar si la contraseña está vacía
                    if (TextUtils.isEmpty(password)) {
                        // La contraseña está vacía, mostrar un mensaje o tomar otra acción si es necesario
                        //Toast.makeText(MainActivity.this, "La contraseña está vacía", Toast.LENGTH_SHORT).show();
                        passwordEditText.setError("La contraseña no puede estar vacía");
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("passwordapp", password);
                        editor.commit();
                        alertDialogpassword.dismiss();
                        int configuracion = consulta_configuracion();
                        if (configuracion == 0) {
                            btnrecargar.setEnabled(false);
                            configdialog();
                        } else {
                            connect = connectionclass();
                            if (connect != null) {

                                if (layout_historico == true) {
                                    consulta_historico();
                                    cl_estados.setVisibility(View.GONE);
                                    cl_estados_historicos.setVisibility(View.VISIBLE);
                                } else {
                                    consulta();
                                    cl_estados.setVisibility(View.VISIBLE);
                                    cl_estados_historicos.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                                onMapReady();

                                btnrecargar.setEnabled(true);
                            } else {
                                Toast.makeText(MainActivity.this, "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
                            }
                        }
                        tvpendiente.setBackgroundColor(colorpendientes_remoto);
                        tvpendiente.setTextColor(colortexto_remoto);
                        tvatencion.setBackgroundColor(coloratenciones_remoto);
                        tvatencion.setTextColor(colortexto_remoto);
                        tvporentregar.setBackgroundColor(colorentregables_remoto);
                        tvporentregar.setTextColor(colortexto_remoto);
                        tventregadas.setBackgroundColor(colorentregadas_remoto);
                        tventregadas.setTextColor(colortexto_remoto);
                        tvanuladas.setBackgroundColor(coloranuladas_remoto);
                        tvanuladas.setTextColor(colortexto_remoto);// Cerrar el diálogo solo si la contraseña no está vacía
                    }
                }
            });
        } else {
            int configuracion = consulta_configuracion();
            if (configuracion == 0) {
                btnrecargar.setEnabled(false);
                configdialog();
            } else {
                connect = connectionclass();
                if (connect != null) {

                    if (layout_historico == true) {
                        consulta_historico();
                        cl_estados.setVisibility(View.GONE);
                        cl_estados_historicos.setVisibility(View.VISIBLE);
                    } else {
                        consulta();
                        cl_estados.setVisibility(View.VISIBLE);
                        cl_estados_historicos.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    onMapReady();

                    btnrecargar.setEnabled(true);
                } else {
                    Toast.makeText(this, "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
                }
            }
            tvpendiente.setBackgroundColor(colorpendientes_remoto);
            tvpendiente.setTextColor(colortexto_remoto);
            tvatencion.setBackgroundColor(coloratenciones_remoto);
            tvatencion.setTextColor(colortexto_remoto);
            tvporentregar.setBackgroundColor(colorentregables_remoto);
            tvporentregar.setTextColor(colortexto_remoto);
            tventregadas.setBackgroundColor(colorentregadas_remoto);
            tventregadas.setTextColor(colortexto_remoto);
            tvanuladas.setBackgroundColor(coloranuladas_remoto);
            tvanuladas.setTextColor(colortexto_remoto);
        }

        menuFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define un conjunto de restricciones para el estado expandido
                ConstraintSet expandedConstraintSet = new ConstraintSet();
                expandedConstraintSet.clone(constraintLayout); // Clona el estado actual
                if (isExpanded[0]) {
                    expandedConstraintSet.setVisibility(R.id.btnconexionflotante, ConstraintSet.INVISIBLE);
                    expandedConstraintSet.setVisibility(R.id.tvconexion, ConstraintSet.INVISIBLE);
                    expandedConstraintSet.setVisibility(R.id.btnfiltrosflotante, ConstraintSet.INVISIBLE);
                    expandedConstraintSet.setVisibility(R.id.tvfiltros, ConstraintSet.INVISIBLE);
                    expandedConstraintSet.setVisibility(R.id.btncoloresflotante, ConstraintSet.INVISIBLE);
                    expandedConstraintSet.setVisibility(R.id.tvcolores, ConstraintSet.INVISIBLE);
                    menuFlotante.setImageResource(R.drawable.recurso_1);
                    Transition transition = new AutoTransition();
                    transition.setDuration(300); // Duración de la animación en milisegundos

                    // Aplica el conjunto de restricciones modificado con la animación
                    TransitionManager.beginDelayedTransition(menuContenedorFlotante, transition);
                    expandedConstraintSet.applyTo(menuContenedorFlotante);

                    // Cambia el estado de expansión
                    isExpanded[0] = !isExpanded[0];

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    //builder.setCancelable(false);
                    // Inflar el diseño personalizado del AlertDialog
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_password, null);
                    builder.setView(dialogView);

                    // Obtener referencias a elementos del diseño personalizado
                    final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
                    final ImageView showHidePassword = dialogView.findViewById(R.id.showHidePassword);
                    final TextView titleTextView = dialogView.findViewById(R.id.titleTextView);

                    // Configurar el título del AlertDialog desde Java
                    titleTextView.setText("Ingrese su contraseña");

                    // Configurar la EditText para ser de tipo contraseña y permitir ver u ocultar el texto
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showHidePassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int inputType = passwordEditText.getInputType();
                            if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            } else {
                                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }
                            passwordEditText.setSelection(passwordEditText.getText().length()); // Colocar el cursor al final del texto
                        }
                    });

                    // Configurar el botón de aceptar
                    builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String password = passwordEditText.getText().toString();

                            if (!TextUtils.isEmpty(password)) {
                                password_app_remoto = preferences.getString("passwordapp", "");
                                if (password.equals(password_app_remoto)) {
                                    alertDialogpassword.dismiss();
                                    // Si no está expandido, muestra los elementos
                                    expandedConstraintSet.setVisibility(R.id.btnconexionflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvconexion, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.btnfiltrosflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvfiltros, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.btncoloresflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvcolores, ConstraintSet.VISIBLE);
                                    menuFlotante.setImageResource(R.drawable.abierto);
                                    // Configura una animación de transición
                                    Transition transition = new AutoTransition();
                                    transition.setDuration(300); // Duración de la animación en milisegundos

                                    // Aplica el conjunto de restricciones modificado con la animación
                                    TransitionManager.beginDelayedTransition(menuContenedorFlotante, transition);
                                    expandedConstraintSet.applyTo(menuContenedorFlotante);

                                    // Cambia el estado de expansión
                                    isExpanded[0] = !isExpanded[0];
                                } else {
                                    passwordEditText.setError("Contraseña Incorrecta");
                                }

                            } else {
                                // La contraseña está vacía, mostrar un mensaje o tomar otra acción si es necesario
                                //Toast.makeText(MainActivity.this, "La contraseña está vacía", Toast.LENGTH_SHORT).show();
                                passwordEditText.setError("Ingrese Contraseña");
                                // No cierres el diálogo aquí
                            }
                        }
                    });

                    // Crear y mostrar el AlertDialog
                    alertDialogpassword = builder.create();
                    alertDialogpassword.show();
                    // Personalizar el comportamiento de cierre del diálogo
                    alertDialogpassword.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String password = passwordEditText.getText().toString();

                            if (!TextUtils.isEmpty(password)) {
                                password_app_remoto = preferences.getString("passwordapp", "");
                                if (password.equals(password_app_remoto)) {
                                    alertDialogpassword.dismiss();
                                    // Si no está expandido, muestra los elementos
                                    expandedConstraintSet.setVisibility(R.id.btnconexionflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvconexion, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.btnfiltrosflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvfiltros, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.btncoloresflotante, ConstraintSet.VISIBLE);
                                    expandedConstraintSet.setVisibility(R.id.tvcolores, ConstraintSet.VISIBLE);
                                    menuFlotante.setImageResource(R.drawable.abierto);
                                    // Configura una animación de transición
                                    Transition transition = new AutoTransition();
                                    transition.setDuration(300); // Duración de la animación en milisegundos

                                    // Aplica el conjunto de restricciones modificado con la animación
                                    TransitionManager.beginDelayedTransition(menuContenedorFlotante, transition);
                                    expandedConstraintSet.applyTo(menuContenedorFlotante);

                                    // Cambia el estado de expansión
                                    isExpanded[0] = !isExpanded[0];
                                } else {
                                    //Toast.makeText(MainActivity.this, password, Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(MainActivity.this, password_app_remoto, Toast.LENGTH_SHORT).show();
                                    passwordEditText.setError("Contraseña Incorrecta");
                                }

                            } else {
                                // La contraseña está vacía, mostrar un mensaje o tomar otra acción si es necesario
                                //Toast.makeText(MainActivity.this, "La contraseña está vacía", Toast.LENGTH_SHORT).show();
                                passwordEditText.setError("Ingrese Contraseña");
                                // No cierres el diálogo aquí
                            }
                        }
                    });
                }


            }
        });
        btnconexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configdialog();
            }
        });
        btnfiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogVistas();
            }
        });
        btncolores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogColor();
            }
        });


    }
    private void alertDialogColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.layout_color, null); // Cambia aquí al diseño correcto
        builder.setView(customLayout);
        alertDialogColores = builder.create();
        customLayout.findViewById(R.id.buttonYesdiseno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String columnas_local = edtcolumnas.getText().toString();
                String filas_local = edtfilas.getText().toString();
                String ttexto_local = edtttexto.getText().toString();
                String ttextoampli_local = edtttextoampli.getText().toString();
                if (ttexto_local.isEmpty() || ttexto_local.equals("0")) {
                    ttexto_local = "1";
                }
                ;
                if (filas_local.isEmpty() || filas_local.equals("0")) {
                    filas_local = "1";
                }
                ;
                if (columnas_local.isEmpty() || columnas_local.equals("0")) {
                    columnas_local = "1";
                }
                ;
                if (ttextoampli_local.isEmpty() || ttextoampli_local.equals("0")) {
                    ttextoampli_local = "1";
                }
                ;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("columnas", Integer.parseInt(columnas_local));
                editor.putInt("filas", Integer.parseInt(filas_local));
                editor.putInt("ttexto", Integer.parseInt(ttexto_local));
                editor.putInt("ttextoampli", Integer.parseInt(ttextoampli_local));
                editor.commit();

                alertDialogColores.dismiss();

                GridLayoutManager layoutManager = (GridLayoutManager) rv1.getLayoutManager();
                layoutManager.setSpanCount(Integer.parseInt(columnas_local));


                adapter.actualizarAlturaElemento(Integer.parseInt(filas_local));

                if (layout_historico == true) {
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);

                } else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        customLayout.findViewById(R.id.buttonNodiseno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogColores.dismiss();
            }
        });
        if (alertDialogColores.getWindow() != null) {
            alertDialogColores.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        // Después de inflar el diseño, busca los botones en el diseño correcto
        pendientes = customLayout.findViewById(R.id.tvcolorpendiente);
        atenciones = customLayout.findViewById(R.id.tvcoloratencion);
        entregas = customLayout.findViewById(R.id.tvcolorporentregar);
        colortexto = customLayout.findViewById(R.id.tvcolortexto);
        entregadas = customLayout.findViewById(R.id.tvcolorentregada);
        anuladas = customLayout.findViewById(R.id.tvcoloranulada);
        cl_cambiacolor_historicos = customLayout.findViewById(R.id.layoutDialog2);
        edtcolumnas = customLayout.findViewById(R.id.edtcolumnas);
        edtfilas = customLayout.findViewById(R.id.edtfilas);
        edtttexto = customLayout.findViewById(R.id.edtttexto);
        edtttextoampli = customLayout.findViewById(R.id.edtttextoampli);
        columnas = preferences.getInt("columnas", 1);
        filas = preferences.getInt("filas", 1);
        ttexto = preferences.getInt("ttexto", 14);
        ttextoampli = preferences.getInt("ttextoampli", 14);
        colorpendientes_remoto = preferences.getInt("colorpendientes", 0xFFFF9900);
        coloratenciones_remoto = preferences.getInt("coloratenciones", 0xFFFFFF00);
        colorentregables_remoto = preferences.getInt("colorentergables", 0xFF00FF00);
        colorentregadas_remoto = preferences.getInt("colorentregadas", 0xFFFF9900);
        coloranuladas_remoto = preferences.getInt("coloranuladas", 0xFFFFFF00);
        colortexto_remoto = preferences.getInt("colorentergables", 0xFF252850);
        edtcolumnas.setText(String.valueOf(columnas));
        edtfilas.setText(String.valueOf(filas));
        edtttexto.setText(String.valueOf(ttexto));
        edtttextoampli.setText(String.valueOf(ttextoampli));
        pendientes.setBackgroundColor(colorpendientes_remoto);
        atenciones.setBackgroundColor(coloratenciones_remoto);
        entregas.setBackgroundColor(colorentregables_remoto);
        colortexto.setBackgroundColor(colortexto_remoto);
        entregadas.setBackgroundColor(colorentregadas_remoto);
        anuladas.setBackgroundColor(coloranuladas_remoto);

        alertDialogColores.show();
        pendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(1);
            }
        });
        atenciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(2);
            }
        });
        entregas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(3);
            }
        });
        colortexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(4);
            }
        });
        entregadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(5);
            }
        });
        anuladas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarcolor(6);
            }
        });
        if (alertDialogColores.getWindow() != null) {
            alertDialogColores.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
    }
    private void alertDialogVistas() {
        checkpendiente_remoto = preferences.getBoolean("checkpendiente", true);
        checkatencion_remoto = preferences.getBoolean("checkatencion", true);
        checkentregado_remoto = preferences.getBoolean("checkentregado", true);
        checkentregadas_remoto = preferences.getBoolean("checkentregadas", true);
        checkanuladas_remoto = preferences.getBoolean("checkanuladas", true);
        checkDT_remoto = preferences.getBoolean("checkDT", true);
        checkTI_remoto = preferences.getBoolean("checkTI", true);
        checkbloqueoestado_remoto = preferences.getBoolean("checkbloqueoestado", true);
        checkbloqueoanulacion_remoto = preferences.getBoolean("checkbloqueoanulacion", true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.layout_vistas, null); // Cambia aquí al diseño correcto
        builder.setView(customLayout);
        alertDialogVistas = builder.create();

        checkpendiente = customLayout.findViewById(R.id.checkpendiente);
        checkatencion = customLayout.findViewById(R.id.checkatencion);
        checkentregado = customLayout.findViewById(R.id.checkentregado);
        checkentregadas = customLayout.findViewById(R.id.checkentregadas);
        checkanuladas = customLayout.findViewById(R.id.checkanuladas);
        checkTI = customLayout.findViewById(R.id.checkTI);
        checkDT = customLayout.findViewById(R.id.checkDT);
        checkbloqueoestado = customLayout.findViewById(R.id.checkbloqueoestado);
        checkbloqueoanulacion = customLayout.findViewById(R.id.checkbloqueoanulacion);

        checkpendiente.setChecked(checkpendiente_remoto);
        checkatencion.setChecked(checkatencion_remoto);
        checkentregado.setChecked(checkentregado_remoto);
        checkentregadas.setChecked(checkentregadas_remoto);
        checkanuladas.setChecked(checkanuladas_remoto);
        checkbloqueoestado.setChecked(checkbloqueoestado_remoto);
        checkbloqueoanulacion.setChecked(checkbloqueoanulacion_remoto);
        checkTI.setChecked(checkTI_remoto);
        checkDT.setChecked(checkDT_remoto);


        final AlertDialog alertDialogVistas = builder.create();
        customLayout.findViewById(R.id.buttonYesvista).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checkpendiente_local = checkpendiente.isChecked();
                Boolean checkatencion_local = checkatencion.isChecked();
                Boolean checkentregado_local = checkentregado.isChecked();
                Boolean checkentregadas_local = checkentregadas.isChecked();
                Boolean checkanuladas_local = checkanuladas.isChecked();
                Boolean checkbloqueoestado_local = checkbloqueoestado.isChecked();
                Boolean checkbloqueoanulacion_local = checkbloqueoanulacion.isChecked();
                Boolean checkTI_local = checkTI.isChecked();
                Boolean checkDT_local = checkDT.isChecked();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("checkpendiente", checkpendiente_local);
                editor.putBoolean("checkatencion", checkatencion_local);
                editor.putBoolean("checkentregado", checkentregado_local);
                editor.putBoolean("checkentregadas", checkentregadas_local);
                editor.putBoolean("checkanuladas", checkanuladas_local);
                editor.putBoolean("checkbloqueoestado", checkbloqueoestado_local);
                editor.putBoolean("checkbloqueoanulacion", checkbloqueoanulacion_local);
                editor.putBoolean("checkTI", checkTI_local);
                editor.putBoolean("checkDT", checkDT_local);
                editor.commit();
                alertDialogVistas.dismiss();

                if (layout_historico == true) {
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);

                } else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        customLayout.findViewById(R.id.buttonNovista).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogVistas.dismiss();
            }
        });
        if (alertDialogVistas.getWindow() != null) {
            alertDialogVistas.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialogVistas.show();
    }
    private void cambiarcolor(int objeto) {
        switch (objeto) {
            case 1:
                currentBackgroundColor = colorpendientes_remoto;
                break;
            case 2:
                currentBackgroundColor = coloratenciones_remoto;
                break;
            case 3:
                currentBackgroundColor = colorentregables_remoto;
                break;
            case 4:
                currentBackgroundColor = colortexto_remoto;
                break;
            case 5:
                currentBackgroundColor=colorentregadas_remoto;
                break;
            case 6:
                currentBackgroundColor=coloranuladas_remoto;
                break;
        }
        ;
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        currentBackgroundColor = selectedColor;
                        SharedPreferences.Editor editor = preferences.edit();
                        switch (objeto) {
                            case 1:
                                tvpendiente.setBackgroundColor(selectedColor);
                                pendientes.setBackgroundColor(selectedColor);
                                editor.putInt("colorpendientes", selectedColor);

                                break;
                            case 2:
                                tvatencion.setBackgroundColor(selectedColor);
                                atenciones.setBackgroundColor(selectedColor);
                                editor.putInt("coloratenciones", selectedColor);

                                break;
                            case 3:
                                tvporentregar.setBackgroundColor(selectedColor);
                                entregas.setBackgroundColor(selectedColor);
                                editor.putInt("colorentregables", selectedColor);

                                break;
                            case 4:
                                tvporentregar.setTextColor(selectedColor);
                                tvatencion.setTextColor(selectedColor);
                                tvpendiente.setTextColor(selectedColor);
                                tvanuladas.setTextColor(selectedColor);
                                tventregadas.setTextColor(selectedColor);
                                colortexto.setBackgroundColor(selectedColor);
                                editor.putInt("colortexto", selectedColor);
                                break;
                            case 5:
                                tventregadas.setBackgroundColor(selectedColor);
                                entregadas.setBackgroundColor(selectedColor);
                                editor.putInt("colorentregadas",selectedColor);
                                break;
                            case 6:
                                tvanuladas.setBackgroundColor(selectedColor);
                                anuladas.setBackgroundColor(selectedColor);
                                editor.putInt("coloranuladas",selectedColor);
                                break;
                        }
                        editor.commit();
                        if (layout_historico==true){
                            consulta_historico();
                            cl_estados.setVisibility(View.GONE);
                            cl_estados_historicos.setVisibility(View.VISIBLE);
                        }else {
                            consulta();
                            cl_estados.setVisibility(View.VISIBLE);
                            cl_estados_historicos.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
    public int consulta_configuracion() {

        servidor_remoto = preferences.getString("servidor", "");
        puerto_remoto = preferences.getString("puerto", "");
        DB_remoto = preferences.getString("DB", "");
        usuario_remoto = preferences.getString("usuario", "");
        password_remoto = preferences.getString("password", "");
        instancia_remoto = preferences.getString("instancia", "");
        colorpendientes_remoto = preferences.getInt("colorpendientes", 0xFFFF9900);
        coloratenciones_remoto = preferences.getInt("coloratenciones", 0xFFFFFF00);
        colorentregables_remoto = preferences.getInt("colorentergables", 0xFF00FF00);
        colorentregadas_remoto = preferences.getInt("colorentregadas", 0xFFFF9900);
        coloranuladas_remoto = preferences.getInt("coloranuladas", 0xFFFFFF00);
        colortexto_remoto = preferences.getInt("colorentergables", 0xFF252850);
        ttexto_remoto = preferences.getInt("ttexto", 14);
        ttextoampli_remoto = preferences.getInt("ttextoampli", 14);
        columnas = preferences.getInt("columnas", 1);
        filas = preferences.getInt("filas", 1);
        checkpendiente_remoto = preferences.getBoolean("checkpendiente", true);
        checkatencion_remoto = preferences.getBoolean("checkatencion", true);
        checkentregado_remoto = preferences.getBoolean("checkentregado", true);
        checkTI_remoto = preferences.getBoolean("checkTI", true);
        checkDT_remoto = preferences.getBoolean("checkDT", true);
        checkbloqueoestado_remoto=preferences.getBoolean("checkbloqueoestado",true);
        checkbloqueoanulacion_remoto=preferences.getBoolean("checkbloqueoanulacion",true);
        if (servidor_remoto == "") {
            Toast.makeText(this, "CONFIGURE CONEXIÓN", Toast.LENGTH_LONG).show();
            return 0;
        } else {
            return 1;
        }
    }
    public void cargar_configuracion() {
        try {
            String servidor_local = servidor.getText().toString();
            String puerto_local = puerto.getText().toString();
            String DB_local = DB.getText().toString();
            String usuario_local = usuario.getText().toString();
            String password_local = password.getText().toString();
            String instancia_local = instancia.getText().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("servidor", servidor_local);
            editor.putString("DB", DB_local);
            editor.putString("puerto", puerto_local);
            editor.putString("usuario", usuario_local);
            editor.putString("password", password_local);
            editor.putString("instancia", instancia_local);
            editor.commit();
        } catch (Throwable e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void ejecutarTarea() {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (layout_historico==true){
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);
                }else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                handler.postDelayed(this, TIEMPO);
            }
        }, TIEMPO);
    }
    public void onMapReady() {
        ejecutarTarea();
    }
    public void consulta_historico() {

        checkentregadas_remoto=preferences.getBoolean("checkentregadas",true);
        checkanuladas_remoto=preferences.getBoolean("checkanuladas",true);
        checkDT_remoto_historico=preferences.getBoolean("checkDT",true);
        checkTI_remoto_historico=preferences.getBoolean("checkTI",true);
        try {
            connect = connectionclass();
            if (connect != null) {
                String query = "SELECT TOP 20 * FROM Comandas_hist ";
                String queryorigen = "";
                List<String> condiciones = new ArrayList<>();
                if (checkentregadas_remoto.booleanValue() == true) {
                    condiciones.add("estado = '4'");
                }
                if (checkanuladas_remoto.booleanValue() == true) {
                    condiciones.add("estado = 'X'");
                }
                if (checkTI_remoto_historico.booleanValue() == true && checkDT_remoto_historico.booleanValue() == true) {
                    queryorigen = "";
                } else if (checkTI_remoto_historico.booleanValue() == true) {
                    queryorigen = "AND origen = 'TI' ";
                } else if (checkDT_remoto_historico.booleanValue() == true) {
                    queryorigen = "AND origen = 'DT' ";
                } else {
                    queryorigen = "AND origen<> 'TI' AND origen <> 'DT'";
                }
                if (!condiciones.isEmpty()) {
                    String condicionesSql = TextUtils.join(" OR ", condiciones);
                    query += " WHERE (" + condicionesSql + ") ";
                } else {

                    condiciones.add("estado <> 'X'");
                    condiciones.add("estado <> '4'");
                    String condicionesSql = TextUtils.join(" AND ", condiciones);
                    query += " WHERE (" + condicionesSql + ") ";

                }
                if (!queryorigen.isEmpty()) {
                    query += queryorigen;
                }
                query += " ORDER BY  fecHora DESC";
                Log.e("QHERY", query);
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                List comandalist = new ArrayList();
                lista1.clear();
                lista2.clear();
                listafecha.clear();
                listanumero.clear();
                listaestado.clear();
                listamodifica.clear();
                JSONArray root = new JSONArray();
                while (resultSet.next()) {
                    String json = resultSet.getString("jsonComanda").replaceAll("[^\\x00-\\x7F]", "");
                    int id_comanda = resultSet.getInt("id");
                    String estado = resultSet.getString("estado");
                    boolean modificado = resultSet.getBoolean("modificado");
                    Collections.addAll(comandalist, json);
                    JSONObject jsonObject = null;
                    try {
                        if (json != null) {
                            jsonObject = new JSONObject(json);

                            lista2.add(id_comanda);
                            listaestado.add(estado);
                            listamodifica.add(modificado);
                            root.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON-LETRAS", e.getMessage());
                    }
                }
                for (int i = 0; i < root.length(); i++) {
                    try {
                        JSONObject jsonObject = root.getJSONObject(i);
                        String centroemisor = jsonObject.getString("centroemisor");
                        String serie = jsonObject.getString("nrodocumento").substring(0, 3);
                        String nro = jsonObject.getString("nrodocumento").substring(3, 12);
                        String correlativo = centroemisor + "-" + serie + "-" + nro;
                        String fecha = jsonObject.getString("fecdocumento");
                        String comentario = jsonObject.getString("comentario");
                        String jsonitem = jsonObject.getString("Items");
                        List itemList = new ArrayList();
                        itemList.clear();
                        listafecha.add(fecha);
                        listanumero.add(correlativo);
                        Collections.addAll(itemList, jsonitem.substring(1, jsonitem.length() - 1));
                        JSONArray itemroot = new JSONArray(itemList.toString().replaceAll("[^\\x00-\\x7F]", ""));

                        String cadena = "";
                        cadena = cadena +
                                System.getProperty("line.separator") + "   Fecha:                    " + fecha + System.getProperty("line.separator") +
                                "   Documento:          " + correlativo + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "   Cantidad                 Descripción" + System.getProperty("line.separator") +
                                "______________________________________________"
                        ;
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "      " + cantidad +
                                    "        " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() >= 1) {
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                            " + tipopan;
                            }
                            try {
                                String jsondetalle = itemjsonObject.getString("COMENTARIOS");
                                List detalleList = new ArrayList();
                                itemList.clear();
                                Collections.addAll(detalleList, jsondetalle.substring(1, jsondetalle.length() - 1));
                                JSONArray detalleroot = new JSONArray(detalleList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                                for (int d = 0; d < detalleroot.length(); d++) {
                                    JSONObject detallejsonObject = detalleroot.getJSONObject(d);
                                    String comentarios = detallejsonObject.getString("COMENT");
                                    cadena = cadena + System.getProperty("line.separator") +
                                            "                            " + comentarios;
                                }
                            } catch (Throwable e) {
                                Log.d("ERRORCOMENTARIO", e.getMessage());
                            }
                            cadena = cadena + System.getProperty("line.separator");
                        }
                        ;
                        cadena = cadena + "______________________________________________" +
                                System.getProperty("line.separator") +
                                "   ** Comentarios **" +
                                System.getProperty("line.separator") +
                                "       " + comentario + System.getProperty("line.separator");

                        lista1.add(cadena);
                    } catch (Throwable e) {
                        Log.e("ERRORJSON", "json error: " + e.getMessage());
                    }
                }
                connect.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void consulta() {

        checkpendiente_remoto = preferences.getBoolean("checkpendiente", false);
        checkatencion_remoto = preferences.getBoolean("checkatencion", false);
        checkentregado_remoto = preferences.getBoolean("checkentregado", false);
        checkbloqueoestado_remoto = preferences.getBoolean("checkbloqueoestado", false);
        checkbloqueoanulacion_remoto = preferences.getBoolean("checkbloqueoanulacion", false);
        checkanuladas_remoto = preferences.getBoolean("checkanuladas", false);
        checkentregadas_remoto = preferences.getBoolean("checkentregadas", false);
        checkDT_remoto = preferences.getBoolean("checkDT", false);
        checkTI_remoto = preferences.getBoolean("checkTI", false);
        try {
            connect = connectionclass();
            if (connect != null) {
                String query = "SELECT * FROM Comandas";
                String queryorigen = "";
                List<String> condiciones = new ArrayList<>();
                if (checkpendiente_remoto.booleanValue() == true) {
                    condiciones.add("estado = '1'");
                }
                if (checkatencion_remoto.booleanValue() == true) {
                    condiciones.add("estado = '2'");
                }
                if (checkentregado_remoto.booleanValue() == true) {
                    condiciones.add("estado = '3'");
                }

                if (checkTI_remoto.booleanValue() == true && checkDT_remoto.booleanValue() == true) {
                    queryorigen = "";
                } else if (checkTI_remoto.booleanValue() == true && checkDT_remoto.booleanValue() == false) {
                    queryorigen = "AND origen = 'TI' ";
                } else if (checkDT_remoto.booleanValue() == true && checkTI_remoto.booleanValue() == false) {
                    queryorigen = "AND origen = 'DT' ";
                } else {
                    queryorigen = "AND origen<> 'TI' AND origen <> 'DT'";
                }
                if (!condiciones.isEmpty()) {
                    String condicionesSql = TextUtils.join(" OR ", condiciones);
                    query += " WHERE (" + condicionesSql + ") ";
                } else {

                    condiciones.add("estado <> '1'");
                    condiciones.add("estado <> '2'");
                    condiciones.add("estado <> '3'");
                    String condicionesSql = TextUtils.join(" AND ", condiciones);
                    query += " WHERE (" + condicionesSql + ") ";

                }
                if (!queryorigen.isEmpty()) {
                    query += queryorigen;
                }
                query += " ORDER BY estado DESC, fecdocumento ASC";
                Log.e("SQL", query);
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                List comandalist = new ArrayList();
                lista1.clear();
                lista2.clear();
                listafecha.clear();
                listanumero.clear();
                listaestado.clear();
                listamodifica.clear();
                JSONArray root = new JSONArray();
                while (resultSet.next()) {
                    String json = resultSet.getString("jsonComanda").replaceAll("[^\\x00-\\x7F]", "");
                    int id_comanda = resultSet.getInt("id");
                    String estado = resultSet.getString("estado");
                    boolean modificado = resultSet.getBoolean("modificado");
                    Collections.addAll(comandalist, json);
                    JSONObject jsonObject = null;
                    try {
                        if (json != null) {
                            jsonObject = new JSONObject(json);

                            lista2.add(id_comanda);
                            listaestado.add(estado);
                            listamodifica.add(modificado);
                            root.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON-LETRAS", e.getMessage());
                    }
                }
                for (int i = 0; i < root.length(); i++) {
                    try {
                        JSONObject jsonObject = root.getJSONObject(i);
                        String centroemisor = jsonObject.getString("centroemisor");
                        String serie = jsonObject.getString("nrodocumento").substring(0, 3);
                        String nro = jsonObject.getString("nrodocumento").substring(3, 12);
                        String correlativo = centroemisor + "-" + serie + "-" + nro;
                        String fecha = jsonObject.getString("fecdocumento");
                        String comentario = jsonObject.getString("comentario");
                        String jsonitem = jsonObject.getString("Items");
                        List itemList = new ArrayList();
                        itemList.clear();
                        listafecha.add(fecha);
                        listanumero.add(correlativo);
                        Collections.addAll(itemList, jsonitem.substring(1, jsonitem.length() - 1));
                        JSONArray itemroot = new JSONArray(itemList.toString().replaceAll("[^\\x00-\\x7F]", ""));

                        String cadena = "";
                        cadena = cadena +
                                System.getProperty("line.separator") + "   Fecha:                    " + fecha + System.getProperty("line.separator") +
                                "   Documento:          " + correlativo + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "   Cantidad                 Descripción" + System.getProperty("line.separator") +
                                "______________________________________________"
                        ;
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "      " + cantidad +
                                    "        " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() >= 1) {
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                            " + tipopan;
                            }
                            try {
                                String jsondetalle = itemjsonObject.getString("COMENTARIOS");
                                List detalleList = new ArrayList();
                                itemList.clear();
                                Collections.addAll(detalleList, jsondetalle.substring(1, jsondetalle.length() - 1));
                                JSONArray detalleroot = new JSONArray(detalleList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                                for (int d = 0; d < detalleroot.length(); d++) {
                                    JSONObject detallejsonObject = detalleroot.getJSONObject(d);
                                    String comentarios = detallejsonObject.getString("COMENT");
                                    cadena = cadena + System.getProperty("line.separator") +
                                            "                            " + comentarios;
                                }
                            } catch (Throwable e) {
                                Log.d("ERRORCOMENTARIO", e.getMessage());
                            }
                            cadena = cadena + System.getProperty("line.separator");
                        }
                        ;
                        cadena = cadena + "______________________________________________" +
                                System.getProperty("line.separator") +
                                "   ** Comentarios **" +
                                System.getProperty("line.separator") +
                                "       " + comentario + System.getProperty("line.separator");

                        lista1.add(cadena);
                    } catch (Throwable e) {
                        Log.e("ERRORJSON", "json error: " + e.getMessage());
                    }
                }
                connect.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateestado(int id, String nuevoestado) {
        try {
            connect = connectionclass();
            if (connect != null) {
                String estado = nuevoestado;
                int id_comanda = id;
                //******************OBTENER FECHA Y HORA ACTUAL*************//
                TimeZone timeZone = TimeZone.getTimeZone("America/Lima");
                Date date = new Date();
                String dayFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                DateFormat requiredFormat = new SimpleDateFormat(dayFormat);
                requiredFormat.setTimeZone(timeZone);
                String fechayhoraactual = requiredFormat.format(date).toUpperCase();
                //******************OBTENER FECHA Y HORA ACTUAL*************//
                String query = "";
                switch (estado) {
                    case "2":
                        query = "Update Comandas set estado='" + estado + "',FecAtencion='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                    case "3":
                        query = "Update Comandas set estado='" + estado + "',FecPorEntregar='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                    case "4":
                        query = "Update Comandas set estado='" + estado + "',FecEntregado='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                    case "X":
                        query = "Update Comandas set estado='" + estado + "' where id=" + id_comanda;
                        Log.e("SEGUIMIENTO",query);
                        break;
                }
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    if (layout_historico==true){
                        consulta_historico();
                        cl_estados.setVisibility(View.GONE);
                        cl_estados_historicos.setVisibility(View.VISIBLE);
                    }else {
                        consulta();
                        cl_estados.setVisibility(View.VISIBLE);
                        cl_estados_historicos.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            connect.close();
        } catch (SQLException e) {
            Log.e("UPDATECOMANDA", e.getMessage());
        }
    }
    public void alertdialog(int id, int estado, String numero, String fecha) {

        int nuevoestado = estado + 1;

        String mensajeestadoactual = "";
        String mensajeestadonuevo = "";
        switch (estado) {
            case 1:
                mensajeestadoactual = "PENDIENTE";
                mensajeestadonuevo = "ATENCIÓN";
                break;
            case 2:
                mensajeestadoactual = "ATENCIÓN";
                mensajeestadonuevo = "POR ENTREGAR";
                break;
            case 3:
                mensajeestadoactual = "POR ENTREGAR";
                mensajeestadonuevo = "ENTREGADO";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Cambio de Estado de la Comanda ");
        ((TextView) view.findViewById(R.id.textMessage)).setText("¿Seguro de cambiar el Estado de la Comanda?"
                + System.getProperty("line.separator") +
                "Nro: " + numero +
                System.getProperty("line.separator") +
                fecha
                + System.getProperty("line.separator") +
                "Estado Actual: " + mensajeestadoactual
                + System.getProperty("line.separator") +
                "Estado Nuevo: " + mensajeestadonuevo);


        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_question);
        alertDialog2 = builder.create();
        view.findViewById(R.id.btncambiar).requestFocus();
        view.findViewById(R.id.btncambiar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();

                alertDialog1.dismiss();

                updateestado(id, String.valueOf(nuevoestado));
                if (layout_historico == true) {
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);
                } else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.btnanular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
                alertDialog1.dismiss();
                updateestado(id, "X");
                if (layout_historico == true) {
                    consulta_historico();
                    cl_estados.setVisibility(View.GONE);
                    cl_estados_historicos.setVisibility(View.VISIBLE);
                } else {
                    consulta();
                    cl_estados.setVisibility(View.VISIBLE);
                    cl_estados_historicos.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        checkbloqueoestado_remoto = preferences.getBoolean("checkbloqueoestado", true);
        checkbloqueoanulacion_remoto = preferences.getBoolean("checkbloqueoanulacion", true);
        if (checkbloqueoestado_remoto == false) {
            view.findViewById(R.id.btncambiar).setVisibility(View.GONE);
        }
        if (checkbloqueoanulacion_remoto == false) {
            view.findViewById(R.id.btnanular).setVisibility(View.GONE);
        }
        alertDialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:

                        break;
                    case 0:
                        alertDialog2.dismiss();
                        break;
                }
                return false;
            }
        });
        if (alertDialog2.getWindow() != null) {
            alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog2.show();
    }
    @SuppressLint("MissingInflatedId")
    public void configdialog() {
        float pulgadas = TextSizeAdjuster.getScreenSizeInInches(this);
        float resolucionW = TextSizeAdjuster.getScreenWidth(this);
        float resolucionH = TextSizeAdjuster.getScreenHeight(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ConfigDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_configuracion,
                (ConstraintLayout) findViewById(R.id.layoutDialogConfiguracion)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Configuración de Conexión ");
        ((TextView) view.findViewById(R.id.textServidor)).setText("IP del Servidor");
        ((TextView) view.findViewById(R.id.textDatabase)).setText("Nombre de la base de datos");
        ((TextView) view.findViewById(R.id.textPuerto)).setText("Puerto de Conexión");
        ((TextView) view.findViewById(R.id.textUser)).setText("Usuario de la BD");
        ((TextView) view.findViewById(R.id.textPassword)).setText("Contraseña de Usuario de la BD");
        ((TextView) view.findViewById(R.id.textInstancia)).setText("Instancia de la BD");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Guardar");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        servidor = (EditText) view.findViewById(R.id.edittextServidor);
        DB = (EditText) view.findViewById(R.id.edittextDatabase);
        puerto = (EditText) view.findViewById(R.id.edittextPuerto);
        usuario = (EditText) view.findViewById(R.id.edittextUser);
        password = (EditText) view.findViewById(R.id.edittextPassword);
        instancia = (EditText) view.findViewById(R.id.edittextInstancia);
        consulta_configuracion();
        servidor.setText(servidor_remoto);
        DB.setText(DB_remoto);
        puerto.setText(puerto_remoto);
        usuario.setText(usuario_remoto);
        password.setText(password_remoto);
        instancia.setText(instancia_remoto);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                cargar_configuracion();
                connect = connectionclass();
                if (connect != null) {
                    onMapReady();
                    if (layout_historico==true){
                        consulta_historico();
                        cl_estados.setVisibility(View.GONE);
                        cl_estados_historicos.setVisibility(View.VISIBLE);
                    }else {
                        consulta();
                        cl_estados.setVisibility(View.VISIBLE);
                        cl_estados_historicos.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    ImageButton btnrecargar = (ImageButton) findViewById(R.id.btnreload);
                    btnrecargar.setEnabled(true);
                } else {
                    Log.e("ERRORDECONEXION", "NO CONECTO");
                    alertDialog.show();
                }
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public Connection connectionclass() {
        SharedPreferences preferences = getSharedPreferences("configuracion", Context.MODE_PRIVATE);
        servidor_remoto = preferences.getString("servidor", "");
        puerto_remoto = preferences.getString("puerto", "");
        DB_remoto = preferences.getString("DB", "");
        usuario_remoto = preferences.getString("usuario", "");
        password_remoto = preferences.getString("password", "");
        instancia_remoto = preferences.getString("instancia", "");
        colorpendientes_remoto = preferences.getInt("colorpendientes", 0xFFFF9900);
        coloratenciones_remoto = preferences.getInt("coloratenciones", 0xFFFFFF00);
        colorentregables_remoto = preferences.getInt("colorentregables", 0xFF00FF00);
        colorentregadas_remoto=preferences.getInt("colorentregadas",0xFFFF9900);
        coloranuladas_remoto=preferences.getInt("coloranuladas",0xFFFFFF00);
        colortexto_remoto = preferences.getInt("colortexto", 0xFF252850);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" +
                    servidor_remoto + ":" +
                    puerto_remoto + ";" +
                    "databasename=" + DB_remoto +
                    ";user=" + usuario_remoto +
                    ";password=" + password_remoto +
                    ";instance=" + instancia_remoto +
                    ";loginTimeout=5;";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "No hay conexión", Toast.LENGTH_SHORT).show();
        }
        return connection;
    }
    private void showAlertDialog(Integer codigo) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.comanda_personal, null);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        frameLayout.setBackgroundResource(R.drawable.transparent_background);
        frameLayout.addView(customLayout);
        builder1.setView(frameLayout);
        alertDialog1 = builder1.create();
        TextView comandapersonal = customLayout.findViewById(R.id.textViewComandaPersonal);
        comandapersonal.setMovementMethod(new ScrollingMovementMethod());
        comandapersonal.setText(lista1.get(selectedPosition));
        ttextoampli_remoto = preferences.getInt("ttextoampli", 14);
        comandapersonal.setTextSize(ttextoampli_remoto);
        alertDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Aquí puedes poner el código para manejar el cierre del AlertDialog
                // Puedes colocar el código para establecer el foco en el primer elemento del RecyclerView y actualizar selectedPosition aquí
                if (adapter.getItemCount() > 0) {
                    //Toast.makeText(MainActivity.this,"se cerro el Dialogo",Toast.LENGTH_SHORT).show();
                    //selectedPosition = 0;
                    rv1.requestFocus();
                    //rv1.getChildAt(selectedPosition).requestFocus();
                    //adapter.notifyDataSetChanged();
                }
            }
        });
        if (layout_historico == false) {
            checkbloqueoestado_remoto = preferences.getBoolean("checkbloqueoestado", true);
            checkbloqueoanulacion_remoto = preferences.getBoolean("checkbloqueoanulacion", true);

            comandapersonal.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (checkbloqueoestado_remoto == false && checkbloqueoanulacion_remoto == false) {
                            Toast.makeText(MainActivity.this, "Modificaciones Bloqueadas", Toast.LENGTH_SHORT);
                            return false;
                        } else {
                            // Manejar el doble toque aquí
                            int id = lista2.get(selectedPosition);
                            int estado = Integer.parseInt(listaestado.get(selectedPosition));
                            String fecha = listafecha.get(selectedPosition);
                            String numero = listanumero.get(selectedPosition);
                            alertdialog(id, estado, numero, fecha);
                            return true;
                        }

                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean consumed = gestureDetector.onTouchEvent(event);

                    if (!consumed) {
                        // Si no se consumió el evento, permitir que el ScrollView lo maneje
                        return false;
                    } else {
                        // Si se consumió el evento, retornar true para indicar que fue manejado
                        return true;
                    }
                }
            });
            alertDialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (checkbloqueoestado_remoto == false && checkbloqueoanulacion_remoto == false) {
                                    Toast.makeText(MainActivity.this, "Modificaciones Bloqueadas", Toast.LENGTH_SHORT);
                                    return false;
                                } else {
                                    int id = lista2.get(selectedPosition);
                                    int estado = Integer.parseInt(listaestado.get(selectedPosition));
                                    String fecha = listafecha.get(selectedPosition);
                                    String numero = listanumero.get(selectedPosition);
                                    alertdialog(id, estado, numero, fecha);
                                }

                            }
                            break;
                        case 0:
                            if (alertDialog1 != null && alertDialog1.isShowing()) {
                                alertDialog1.dismiss();
                            }
                            break;
                    }
                    return false;
                }
            });
        }
        alertDialog1.setCanceledOnTouchOutside(true);
        alertDialog1.show();
    }
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> data;
        private int alturaElemento = GridLayoutManager.LayoutParams.WRAP_CONTENT;
        public MyAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comandas, parent, false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();

            filas = preferences.getInt("filas", 1);
            lp.height = parent.getMeasuredHeight() / filas;
            view.setLayoutParams(lp);
            return new ViewHolder(view);
        }

        public void actualizarAlturaElemento(int nuevaAltura) {
            alturaElemento = nuevaAltura;
            notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado.
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                    /*width*/ RecyclerView.LayoutParams.MATCH_PARENT,
                    /*height*/ RecyclerView.LayoutParams.MATCH_PARENT
            );
            //parametros.setMargins(5, 5, 5, 5);
            holder.textView.setLayoutParams(parametros);

            filas = preferences.getInt("filas", 1);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = rv1.getMeasuredHeight() / filas; // Establece la altura dinámica aquí
            holder.itemView.setLayoutParams(layoutParams);

            int color = 0;
            String estado = listaestado.get(position);
            //Log.e("COLOR-ESTADO: ",estado);
            switch (estado) {
                case "1":
                    color = colorpendientes_remoto;
                    Log.e("COLOR: PENDIENTES ", String.valueOf(color));
                    break;
                case "2":
                    color = coloratenciones_remoto;
                    Log.e("COLOR: ATENCIONES ", String.valueOf(color));
                    break;
                case "3":
                    color = colorentregables_remoto;
                    Log.e("COLOR: ENTREGABLE ", String.valueOf(color));
                    break;
                case "4":
                    color=colorentregadas_remoto;
                    Log.e("COLOR: ENTREGADO ", String.valueOf(color));
                    break;
                case "X":
                    color=coloranuladas_remoto;
                    Log.e("COLOR: ANULADA ", String.valueOf(color));
                    break;
            }


            boolean modificado=listamodifica.get(position);
            if (layout_historico==false){
                if (modificado == true) {
                    holder.textViewmodificado.setVisibility(View.VISIBLE);
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(holder.textViewmodificado, "alpha", 0.0f, 1.0f);
                    alphaAnimator.setDuration(1000); // Duración de un segundo
                    alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                    alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);

                    // Inicia la animación
                    alphaAnimator.start();
                } else {
                    holder.textViewmodificado.setVisibility(View.GONE);
                }
                holder.textViewmodificado.setBackgroundColor(Color.RED);
            } else {
                holder.textViewmodificado.setVisibility(View.GONE);

            }
            ttexto_remoto = preferences.getInt("ttexto", 14);
            holder.textView.setTextColor(colortexto_remoto);
            holder.textView.setTextSize(ttexto_remoto);
            holder.itemView.setBackgroundColor(color);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            params.setMargins(3, 3, 3, 3);
            holder.itemView.setLayoutParams(params);
            if (position == selectedPosition) {
                holder.textView.setBackgroundResource(R.drawable.custom_border_comanda);
                //holder.textViewmodificado.setBackgroundResource(R.drawable.custom_border);
            } else {
                holder.textView.setBackgroundResource(0);
                //holder.textViewmodificado.setBackgroundResource(0);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            TextView textViewmodificado;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textviewcomandas);
                textViewmodificado=itemView.findViewById(R.id.textviewmodificado);
                TextSizeAdjuster.adjustTextSizeBasedOnScreenSize(MainActivity.this, textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int previousSelectedPosition = selectedPosition;
                        selectedPosition = getAbsoluteAdapterPosition();

                        // Notifica el cambio en la posición seleccionada para actualizar la vista
                        notifyItemChanged(previousSelectedPosition);
                        notifyItemChanged(selectedPosition);
                        showAlertDialog(selectedPosition);
                        //oast.makeText(MainActivity.this,"hiciste clic"+selectedPosition,Toast.LENGTH_SHORT).show();
                    }
                });
                textView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            int rowCount = adapter.getItemCount() / columnas; // Número de columnas
                            int itemCount = adapter.getItemCount();

                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    if (selectedPosition >= columnas) {
                                        selectedPosition -= columnas;
                                        rv1.smoothScrollToPosition(selectedPosition);
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    if (selectedPosition + columnas < itemCount) {
                                        selectedPosition += columnas;
                                        rv1.smoothScrollToPosition(selectedPosition);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        selectedPosition = itemCount - 1;
                                        rv1.smoothScrollToPosition(selectedPosition);
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                    if (selectedPosition > 0) {
                                        selectedPosition--;
                                        rv1.smoothScrollToPosition(selectedPosition);
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    if (selectedPosition < itemCount - 1) {
                                        selectedPosition++;
                                        rv1.smoothScrollToPosition(selectedPosition);
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case KeyEvent.KEYCODE_ENTER:
                                    //showToast("Item seleccionado: " + lista2.get(selectedPosition));
                                    showAlertDialog(selectedPosition);
                                    //Toast.makeText(MainActivity.this,"hiciste clic"+selectedPosition,Toast.LENGTH_SHORT).show();
                                    break;

                            }

                            return true;
                        }

                        return false;
                    }
                });
            }
        }
    }


}

