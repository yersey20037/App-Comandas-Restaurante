package com.example.responsive;
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
import android.widget.EditText;
import android.widget.FrameLayout;
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
import androidx.recyclerview.widget.RecyclerView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
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
    private ArrayList<String> lista1 = new ArrayList<>();
    private ArrayList<Integer> lista2 = new ArrayList<>();
    private ArrayList<String> listaestado = new ArrayList<>();
    private ArrayList<String> listafecha = new ArrayList<>();
    private ArrayList<String> listanumero = new ArrayList<>();
    Handler handler = new Handler();
    private final int TIEMPO = 10000;
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
    Integer colortexto_remoto;
    SharedPreferences preferences;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private MyAdapter adapter;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;
    private AlertDialog alertDialogColores;
    private TextView tvpendiente;
    private TextView tvatencion;
    private TextView tvporentregar;
    private Button pendientes;
    private Button entregas;
    private Button atenciones;
    private Button colortexto;
    private int columnas;
    private TextView tvtitulo;
    private int currentBackgroundColor = 0xFFFF9900;
    private float alto;
    private float ancho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvpendiente = findViewById(R.id.pendiente);
        tvatencion=findViewById(R.id.atencion);
        tvporentregar=findViewById(R.id.entrega);
        tvtitulo=findViewById(R.id.textcomandassiges);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = getSharedPreferences("configuracion", Context.MODE_PRIVATE);
        rv1 = findViewById(R.id.rv1);
        rv1.addItemDecoration(new DividerItemDecoration(rv1.getContext(), DividerItemDecoration.VERTICAL));
        ancho=TextSizeAdjuster.ancho(MainActivity.this);
        alto=TextSizeAdjuster.alto(MainActivity.this);
        if (ancho < 4.0) {
            columnas=1;
        } else if (ancho >= 4.0 && ancho < 6.0) {
            columnas=2;
        } else if(ancho>=6 && ancho<10) {
            columnas=3;
        }else {
            columnas=5;
        }

        rv1.setLayoutManager(new GridLayoutManager(this, columnas));
        rv1.setHasFixedSize(true);
        adapter = new MyAdapter(lista1);
        rv1.setAdapter(adapter);

        rv1.requestFocus();

        rv1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
        ImageButton btnrecargar =findViewById(R.id.btnreload);
        btnrecargar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consulta();
                adapter.notifyDataSetChanged();
            }
        });
        //BOTON CONFIGURACION
        ImageButton btnconfig = findViewById(R.id.btnconfig);
        btnconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configdialog();
            }
        });
        //BOTON CAMBIAR COLOR
        ImageButton cambiarcolor = findViewById(R.id.btncolor);
        cambiarcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogColor();
            }
        });

        int configuracion = consulta_configuracion();
        if (configuracion == 0) {
            btnrecargar.setEnabled(false);
            configdialog();
        } else {
            connect = connectionclass();
            if (connect != null) {

                consulta();
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
    }
    private void alertDialogColor(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.layout_color, null); // Cambia aquí al diseño correcto
        builder.setView(customLayout);
        alertDialogColores = builder.create();
        alertDialogColores.show();
        // Después de inflar el diseño, busca los botones en el diseño correcto
        pendientes = customLayout.findViewById(R.id.btnpendiente);
        atenciones = customLayout.findViewById(R.id.btnatencion);
        entregas = customLayout.findViewById(R.id.btnentrega);
        colortexto=customLayout.findViewById(R.id.btncolortexto);
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
    }

    private void cambiarcolor(int objeto){
        switch (objeto){
            case 1:
                currentBackgroundColor=colorpendientes_remoto;
                break;
            case 2:
                currentBackgroundColor=coloratenciones_remoto;
                break;
            case 3:
                currentBackgroundColor=colorentregables_remoto;
                break;
            case 4:
                currentBackgroundColor=colortexto_remoto;
                break;
        };
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
                        switch (objeto){
                            case 1:
                                tvpendiente.setBackgroundColor(selectedColor);

                                editor.putInt("colorpendientes",selectedColor);

                                break;
                            case 2:
                                tvatencion.setBackgroundColor(selectedColor);

                                editor.putInt("coloratenciones",selectedColor);

                                break;
                            case 3:
                                tvporentregar.setBackgroundColor(selectedColor);
                                editor.putInt("colorentregables",selectedColor);

                                break;
                            case 4:
                                tvporentregar.setTextColor(selectedColor);
                                tvatencion.setTextColor(selectedColor);
                                tvpendiente.setTextColor(selectedColor);
                                editor.putInt("colortexto",selectedColor);
                        }
                        editor.commit();
                        consulta();
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
    };
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> data;
        public MyAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comandas, parent, false);
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();

            float alto=TextSizeAdjuster.alto(MainActivity.this);
            if (alto < 5.0) {
                lp.height = parent.getMeasuredHeight();
            } else if (alto>=5.0 && alto<10){
                lp.height = parent.getMeasuredHeight() / 2;
            }else {
                lp.height = parent.getMeasuredHeight() / 3;
            }
            view.setLayoutParams(lp);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                    /*width*/ RecyclerView.LayoutParams.MATCH_PARENT,
                    /*height*/ RecyclerView.LayoutParams.MATCH_PARENT
            );
            parametros.setMargins(5, 5, 5, 5);
            holder.textView.setLayoutParams(parametros);
            int color = colorpendientes_remoto;
            int estado = Integer.parseInt(listaestado.get(position));
            if (estado == 2) {
                color = coloratenciones_remoto;
            } else if (estado == 3) {
                color = colorentregables_remoto;
            }
            holder.textView.setTextColor(colortexto_remoto);
            //currentBackgroundColor = color;
            holder.textView.setBackgroundColor(color);
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(Color.RED);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textviewcomandas);
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
                                    }else {
                                         selectedPosition=itemCount-1;
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
    public int consulta_configuracion() {
        servidor_remoto = preferences.getString("servidor", "");
        puerto_remoto = preferences.getString("puerto", "");
        DB_remoto = preferences.getString("DB", "");
        usuario_remoto = preferences.getString("usuario", "");
        password_remoto = preferences.getString("password", "");
        instancia_remoto = preferences.getString("instancia", "");
        colorpendientes_remoto =preferences.getInt("colorpendientes", 0xFFFF9900);
        coloratenciones_remoto =preferences.getInt("coloratenciones", 0xFFFFFF00);
        colorentregables_remoto =preferences.getInt("colorentergables", 0xFF00FF00);
        colortexto_remoto=preferences.getInt("colorentergables", 0xFF252850);
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
            Integer colorpendientes_local=0xFFFF9900;
            Integer coloratenciones_local=0xFFFFFF00;
            Integer colorentregables_local=0xFF00FF00;
            Integer colortexto_local=0xFF252850;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("servidor", servidor_local);
            editor.putString("DB", DB_local);
            editor.putString("puerto", puerto_local);
            editor.putString("usuario", usuario_local);
            editor.putString("password", password_local);
            editor.putString("instancia", instancia_local);
            editor.putInt("colorpendientes",colorpendientes_local);
            editor.putInt("coloratenciones",coloratenciones_local);
            editor.putInt("colorentregables",colorentregables_local);
            editor.putInt("colortexto",colortexto_local);
            editor.commit();
        } catch (Throwable e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void ejecutarTarea() {
        handler.postDelayed(new Runnable() {
            public void run() {
                consulta();
                adapter.notifyDataSetChanged();
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
                listafecha.clear();
                listanumero.clear();
                listaestado.clear();
                JSONArray root = new JSONArray();
                while (resultSet.next()) {
                    String json = resultSet.getString("jsonComanda").replaceAll("[^\\x00-\\x7F]", "");
                    int id_comanda = resultSet.getInt("id");
                    String estado = resultSet.getString("estado");
                    Collections.addAll(comandalist, json);
                    JSONObject jsonObject = null;
                    try {
                        if (json != null) {
                            jsonObject = new JSONObject(json);

                            lista2.add(id_comanda);
                            listaestado.add(estado);
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


                        String cadena =
                                        "   Fecha:                    " + fecha + System.getProperty("line.separator") +
                                        "   Documento:          " + correlativo + System.getProperty("line.separator") +
                                        System.getProperty("line.separator") +
                                        "   Cantidad                                  Descripción" + System.getProperty("line.separator") +
                                        "_________________________________________________________"
                                        //+System.getProperty("line.separator") +
                                        //"1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-16-17-18-19-20-21-22-23-"
                                        ;
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            //String cdarticulo = itemjsonObject.getString("CDARTICULO");
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "        " + cantidad +
                                    "                    " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() < 1) {
                                Log.d("TIPOPANVACIO", String.valueOf(tipopan.length()));
                            } else {
                                Log.d("TIPOPANLLENO", String.valueOf(tipopan.length()));
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                          " +
                                        "                " + tipopan;
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
                                            "                          " +
                                            "                " + comentarios;
                                }
                            } catch (Throwable e) {
                                Log.d("ERRORCOMENTARIO", e.getMessage());
                            }
                            cadena = cadena + System.getProperty("line.separator");
                        }
                        ;
                        cadena = cadena + "_________________________________________________________" +
                                System.getProperty("line.separator") +
                                "   ** Comentarios **" +
                                System.getProperty("line.separator") +
                                "       " + comentario+ System.getProperty("line.separator");

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
    public String consultaid(int id) {
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
                                "   Fecha:                              " + fecha + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "   Documento:                    " + correlativo + System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "   Cantidad                                  Descripción" +
                                System.getProperty("line.separator") +
                                "____________________________________________________________";
                        for (int u = 0; u < itemroot.length(); u++) {
                            JSONObject itemjsonObject = itemroot.getJSONObject(u);
                            //String cdarticulo = itemjsonObject.getString("CDARTICULO");
                            String cantidad = itemjsonObject.getString("CANTIDAD");
                            String descripcion = itemjsonObject.getString("DSARTICULO");
                            cadena = cadena + System.getProperty("line.separator") +
                                    "        " + cantidad +
                                    "                    " + descripcion;
                            String tipopan = itemjsonObject.getString("TIPOPAN");
                            if (tipopan.length() < 1) {
                                Log.d("TIPOPANVACIO", String.valueOf(tipopan.length()));
                            } else {
                                Log.d("TIPOPANLLENO", String.valueOf(tipopan.length()));
                                cadena = cadena + System.getProperty("line.separator");
                                cadena = cadena +
                                        "                          " +
                                        "                " + tipopan;
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
                                            "                          " +
                                            "                " + comentarios;
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
                        return cadena;
                    }
                }
                connect.close();
            } catch (SQLException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return "no existe";
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
        ((Button) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_question);
        alertDialog2 = builder.create();
        view.findViewById(R.id.buttonYes).setFocusable(true);
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateestado(id, nuevoestado);
                consulta();
                adapter.notifyDataSetChanged();
                alertDialog2.dismiss();
                if (estado == 3) {
                    alertDialog1.dismiss();
                }
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ConfigDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_configuracion,
                (ConstraintLayout) findViewById(R.id.layoutDialogConfiguracion)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Configuración de Conexión " + System.getProperty("line.separator")+"Ancho: " + ancho + " Alto: "+ alto);
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
                    consulta();
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
        colorpendientes_remoto =preferences.getInt("colorpendientes",0xFFFF9900);
        coloratenciones_remoto =preferences.getInt("coloratenciones",0xFFFFFF00);
        colorentregables_remoto =preferences.getInt("colorentregables",0xFF00FF00);
        colortexto_remoto=preferences.getInt("colortexto",0xFF252850);
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


        comandapersonal.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // Manejar el doble toque aquí
                    int id = lista2.get(selectedPosition);
                    int estado = Integer.parseInt(listaestado.get(selectedPosition));
                    String fecha = listafecha.get(selectedPosition);
                    String numero = listanumero.get(selectedPosition);
                    alertdialog(id, estado, numero, fecha);
                    return true;
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
        alertDialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            int id = lista2.get(selectedPosition);
                            int estado = Integer.parseInt(listaestado.get(selectedPosition));
                            String fecha = listafecha.get(selectedPosition);
                            String numero = listanumero.get(selectedPosition);
                            alertdialog(id, estado, numero, fecha);
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
        alertDialog1.setCanceledOnTouchOutside(true);
        alertDialog1.show();
    }
}
