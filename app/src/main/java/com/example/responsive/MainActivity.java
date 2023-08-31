package com.example.responsive;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    EditText servidor;
    EditText DB;
    EditText usuario;
    EditText password;
    EditText puerto;
    EditText instancia;
    private RecyclerView rv1;
    //private TextView detalle;
    private ArrayList<String> lista1 = new ArrayList<>();
    private ArrayList<Integer> lista2 = new ArrayList<>();
    private ArrayList<String> listaestado = new ArrayList<>();
    Handler handler = new Handler();
    private final int TIEMPO = 5000;
    AdaptadorNumero adaptadorNumero = new AdaptadorNumero();
    Connection connect;
    String servidor_remoto;
    String puerto_remoto;
    String DB_remoto;
    String usuario_remoto;
    String password_remoto;
    String instancia_remoto;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = getSharedPreferences("configuracion", Context.MODE_PRIVATE);
        rv1 = findViewById(R.id.rv1);
        //detalle = findViewById(R.id.textViewDetalle);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(this,5);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv1.setLayoutManager(gridLayoutManager);
        rv1.addItemDecoration(new DividerItemDecoration(rv1.getContext(), DividerItemDecoration.VERTICAL));
        llamaradaptador();
        ImageButton btnrecargar = (ImageButton) findViewById(R.id.btnreload);
        btnrecargar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consulta();
                adaptadorNumero.notifyDataSetChanged();
            }
        });
        int configuracion = consulta_configuracion();
        //Toast.makeText(this,configuracion+" no se encuentra la configuracion",Toast.LENGTH_LONG).show();
        if (configuracion == 0) {
            btnrecargar.setEnabled(false);
            configdialog();
        } else {
            connect = connectionclass();
            if (connect != null) {
                consulta();
                onMapReady();
                btnrecargar.setEnabled(true);
            } else {
                Toast.makeText(this, "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
            }

        }
        //TextView detalle = findViewById(R.id.textViewDetalle);
        //detalle.setMovementMethod(new ScrollingMovementMethod());
        ImageButton btnconfig = (ImageButton) findViewById(R.id.btnconfig);
        btnconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configdialog();
            }
        });
    }

    public int consulta_configuracion() {
        servidor_remoto = preferences.getString("servidor", "");
        puerto_remoto = preferences.getString("puerto", "");
        DB_remoto = preferences.getString("DB", "");
        usuario_remoto = preferences.getString("usuario", "");
        password_remoto = preferences.getString("password", "");
        instancia_remoto =preferences.getString("instancia","");
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
            String instancia_local=instancia.getText().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("servidor", servidor_local);
            editor.putString("DB", DB_local);
            editor.putString("puerto", puerto_local);
            editor.putString("usuario", usuario_local);
            editor.putString("password", password_local);
            editor.putString("instancia",instancia_local);
            editor.commit();
        } catch (Throwable e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void llamaradaptador() {
        rv1.setAdapter(adaptadorNumero);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adaptadorNumero.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                int id = lista2.get(viewHolder.getAbsoluteAdapterPosition());
                int estado = Integer.parseInt(listaestado.get(viewHolder.getAbsoluteAdapterPosition()));
                String nroyfecha = lista1.get(viewHolder.getAbsoluteAdapterPosition());
                alertdialog(id, estado, nroyfecha);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv1);
    }

    public void ejecutarTarea() {
        handler.postDelayed(new Runnable() {

            public void run() {
                consulta();
                adaptadorNumero.notifyDataSetChanged();
                handler.postDelayed(this, TIEMPO);
            }
        }, TIEMPO);
    }

    public void onMapReady() {
        ejecutarTarea();
    }

    public void consulta() {
        try {
            connect = connectionclass();
            if (connect != null) {
                String query = "Select * from Comandas order by estado DESC";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                List comandalist = new ArrayList();
                lista1.clear();
                lista2.clear();
                listaestado.clear();
                JSONArray root = new JSONArray();
                while (resultSet.next()) {
                    String json = resultSet.getString("jsonComanda").replaceAll("[^\\x00-\\x7F]", "");

                    int id_comanda = resultSet.getInt("id");
                    String estado = resultSet.getString("estado");
                    Collections.addAll(comandalist, json);

                    JSONObject jsonObject=null;
                    try {
                        if (json != null){
                            jsonObject = new JSONObject(json);

                            lista2.add(id_comanda);
                            listaestado.add(estado);
                            root.put(jsonObject);
                        }

                    }catch (JSONException e){
                        Log.e("JSON-LETRAS",e.getMessage());
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
                        Collections.addAll(itemList, jsonitem.substring(1, jsonitem.length() - 1));
                        JSONArray itemroot = new JSONArray(itemList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                        String cadena = "       Fecha:                              " + fecha + System.getProperty("line.separator") +
                                "       Documento:                    " + correlativo + System.getProperty("line.separator") +
                                "       Código          Cantidad          Descripción" + System.getProperty("line.separator") +
                                "____________________________________________________________";
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            String cdarticulo = itemjsonObject.getString("CDARTICULO");
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "      " + cdarticulo +
                                    "          " + cantidad +
                                    "          " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() < 1) {
                                Log.d("TIPOPANVACIO", String.valueOf(tipopan.length()));
                            } else {
                                Log.d("TIPOPANLLENO", String.valueOf(tipopan.length()));
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                                                   " +
                                        "      " + tipopan;
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
                                            "                                                   " +
                                            "      " + comentarios;
                                }
                            } catch (Throwable e) {
                                Log.d("ERRORCOMENTARIO", e.getMessage());
                            }
                            cadena = cadena + System.getProperty("line.separator");
                        }
                        ;
                        cadena = cadena + "____________________________________________________________" +
                                System.getProperty("line.separator") +
                                "       ** Comentarios **" +
                                System.getProperty("line.separator") +
                                "       " + comentario;
                        //TextView detalle = findViewById(R.id.textViewDetalle);
                        //detalle.setText(cadena);


                        //////////////////////////////////
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

    private class AdaptadorNumero extends RecyclerView.Adapter<AdaptadorNumero.AdaptadorNumeroHolder> {
        @NonNull
        @Override
        public AdaptadorNumeroHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comandas, parent, false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) v.getLayoutParams();
            lp.height = parent.getMeasuredHeight() / 2;

            v.setLayoutParams(lp);
            return new AdaptadorNumeroHolder(v);

            //return new AdaptadorNumeroHolder(getLayoutInflater().inflate(R.layout.layout_comandas, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorNumeroHolder holder, int position) {
            holder.imprimir(position);
        }

        @Override
        public int getItemCount() {
            return lista1.size();
        }

        class AdaptadorNumeroHolder extends RecyclerView.ViewHolder {
            TextView tvcomanda;

            public AdaptadorNumeroHolder(@NonNull View itemView) {
                super(itemView);
                tvcomanda = itemView.findViewById(R.id.textviewcomandas);

                ////////////////////////ESTO ES PARA EL SCROLL DEL TEXTVIEW//////////////////////////////////
                tvcomanda.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                tvcomanda.setMovementMethod(new ScrollingMovementMethod());
                //////////////////////////////////////////////////////////////////////////////////////////////
            }

            @SuppressLint("ResourceType")
            public void imprimir(int position) {
                tvcomanda.setText(lista1.get(position));
                tvcomanda.setId(lista2.get(position));
                int color = 0xFFFF9900;
                int estado = Integer.parseInt(listaestado.get(position));
                if (estado == 2) {
                    color = 0xFFFFFF00;
                } else if (estado == 3) {
                    color = 0xFF00FF00;
                }
                tvcomanda.setBackgroundColor(color);
                LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                        /*width*/ RecyclerView.LayoutParams.MATCH_PARENT,
                        /*height*/ RecyclerView.LayoutParams.MATCH_PARENT
                );
                parametros.setMargins(5, 5, 5, 5);
                tvcomanda.setLayoutParams(parametros);

            }
        }
    }

    public void consultaid(int id) {
        connect = connectionclass();
        if (connect != null) {
            String query = "Select * from Comandas where id=" + String.valueOf(id);
            Statement statement = null;
            try {
                statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String json = resultSet.getString("jsonComanda");
                    List myList = new ArrayList();
                    myList.clear();
                    Collections.addAll(myList, json);
                    JSONArray root = new JSONArray(myList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                    for (int i = 0; i < root.length(); i++) {
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
                        Collections.addAll(itemList, jsonitem.substring(1, jsonitem.length() - 1));
                        JSONArray itemroot = new JSONArray(itemList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                        String cadena = System.getProperty("line.separator") +
                                "       Fecha:                              " + fecha + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "       Documento:                    " + correlativo + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "       Código          Cantidad          Descripción" +
                                System.getProperty("line.separator") +
                                "____________________________________________________________";
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            String cdarticulo = itemjsonObject.getString("CDARTICULO");
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "      " + cdarticulo +
                                    "          " + cantidad +
                                    "          " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() < 1) {
                                Log.d("TIPOPANVACIO", String.valueOf(tipopan.length()));
                            } else {
                                Log.d("TIPOPANLLENO", String.valueOf(tipopan.length()));
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                                                   " +
                                        "      " + tipopan;
                            }
                            try {
                                String jsondetalle = itemjsonObject.getString("COMENTARIOS");
                                Log.d("EXISTECOMENTARIO", jsondetalle);
                                List detalleList = new ArrayList();
                                itemList.clear();
                                Collections.addAll(detalleList, jsondetalle.substring(1, jsondetalle.length() - 1));
                                JSONArray detalleroot = new JSONArray(detalleList.toString().replaceAll("[^\\x00-\\x7F]", ""));
                                for (int d = 0; d < detalleroot.length(); d++) {
                                    JSONObject detallejsonObject = detalleroot.getJSONObject(d);
                                    String comentarios = detallejsonObject.getString("COMENT");
                                    cadena = cadena + System.getProperty("line.separator") +
                                            "                                                   " +
                                            "      " + comentarios;
                                }
                            } catch (Throwable e) {
                                Log.d("ERRORCOMENTARIO", e.getMessage());
                            }
                            cadena = cadena + System.getProperty("line.separator");
                        }
                        ;
                        cadena = cadena + "____________________________________________________________" +
                                System.getProperty("line.separator") +
                                "       ** Comentarios **" +
                                System.getProperty("line.separator") +
                                "       " + comentario;
                        //TextView detalle = findViewById(R.id.textViewDetalle);
                        //detalle.setText(cadena);
                    }
                }
                connect.close();
            } catch (SQLException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateestado(int id, int nuevoestado) {
        try {
            connect = connectionclass();
            if (connect != null) {
                int estado = nuevoestado;
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
                    case 2:
                        query = "Update Comandas set estado=" + estado + ",FecAtencion='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                    case 3:
                        query = "Update Comandas set estado=" + estado + ",FecPorEntregar='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                    case 4:
                        query = "Update Comandas set estado=" + estado + ",FecEntregado='" + fechayhoraactual + "' where id=" + id_comanda;
                        break;
                }
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    consulta();
                    adaptadorNumero.notifyDataSetChanged();
                }
            }
            connect.close();
        } catch (SQLException e) {
            Log.e("UPDATECOMANDA", e.getMessage());
        }
    }

    public void alertdialog(int id, int estado, String nroyfecha) {
        int estadoactual = estado;
        int nuevoestado = estadoactual + 1;
        String mensajeestadoactual = "";
        String mensajeestadonuevo = "";
        String nro = nroyfecha.substring(0, 17);
        String fecha = "Fecha: " + nroyfecha.substring(17, 40);
        switch (estadoactual) {
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
        ((TextView) view.findViewById(R.id.textTitle)).setText("Cambio de Estado de la Comanda");
        ((TextView) view.findViewById(R.id.textMessage)).setText("¿Seguro de cambiar el Estado de la Comanda?"
                + System.getProperty("line.separator") +
                "Nro: " + nro +
                System.getProperty("line.separator") +
                fecha
                + System.getProperty("line.separator") +
                "Estado Actual: " + mensajeestadoactual
                + System.getProperty("line.separator") +
                "Estado Nuevo: " + mensajeestadonuevo);
        ((Button) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_question);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateestado(id, nuevoestado);
                consulta();
                adaptadorNumero.notifyDataSetChanged();
                alertDialog.dismiss();
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

    @SuppressLint("MissingInflatedId")
    public void configdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ConfigDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_configuracion,
                (ConstraintLayout) findViewById(R.id.layoutDialogConfiguracion)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Configuración de Conexión");
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
                    consulta();
                    onMapReady();
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
        instancia_remoto =preferences.getString("instancia", "");
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
                    ";instance=" +instancia_remoto+
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
}
