package com.example.taller3.vistas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taller3.R;
import com.example.taller3.controladores.Interfaz;
import com.example.taller3.controladores.RespuestaServidor;
import com.example.taller3.controladores.Sello;
import com.example.taller3.datos.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class PantallaListado extends PantallaBase {

    public static final String EXTRA_CORREO = "correo";
    public static final String EXTRA_NOMBRE = "nombre";

    private static final String LETRA_TODOS = "TODOS";

    private RecyclerView listaUsuarios;
    private LinearLayout indiceLetras;
    private LinearProgressIndicator progreso;
    private MaterialToolbar barra;
    private View estadoVacio;
    private View estadoError;
    private TextView vacioDetalle;
    private TextView letraActiva;
    private AdaptadorUsuarios adaptador;

    private List<Usuario> fichas = new ArrayList<>();
    private String correoUsuario;
    private String nombreUsuario;

    private final ActivityResultLauncher<Intent> editorUsuario = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK) {
                    cargarFichas();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_listado);

        mostrarHoja(findViewById(R.id.contenedor), false);

        listaUsuarios = findViewById(R.id.lista_usuarios);
        indiceLetras = findViewById(R.id.indice_letras);
        progreso = findViewById(R.id.progreso);
        barra = findViewById(R.id.barra);
        estadoVacio = findViewById(R.id.estado_vacio);
        estadoError = findViewById(R.id.estado_error);
        vacioDetalle = findViewById(R.id.vacio_detalle);

        correoUsuario = getIntent().getStringExtra(EXTRA_CORREO);
        nombreUsuario = getIntent().getStringExtra(EXTRA_NOMBRE);

        adaptador = new AdaptadorUsuarios();
        adaptador.setEscucha(this::abrirEditor);
        listaUsuarios.setLayoutManager(new LinearLayoutManager(this));
        listaUsuarios.setAdapter(adaptador);

        barra.inflateMenu(R.menu.menu_pantalla_listado);
        barra.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accion_editar_tus_datos) {
                abrirEditorPropio();
                return true;
            }
            return false;
        });

        findViewById(R.id.btn_reintentar).setOnClickListener(v -> cargarFichas());
        findViewById(R.id.btn_agregar).setOnClickListener(v -> {
            Intent destino = new Intent(this, PantallaCrudUsuario.class);
            destino.putExtra(PantallaCrudUsuario.EXTRA_MODO, PantallaCrudUsuario.MODO_AGREGAR);
            editorUsuario.launch(destino);
        });

        cargarFichas();
    }

    private void abrirEditor(Usuario usuario) {
        Intent destino = new Intent(this, PantallaCrudUsuario.class);
        destino.putExtra(PantallaCrudUsuario.EXTRA_MODO, PantallaCrudUsuario.MODO_EDITAR);
        destino.putExtra(PantallaCrudUsuario.EXTRA_CORREO, usuario.getEmail());
        destino.putExtra(PantallaCrudUsuario.EXTRA_NOMBRE, usuario.getNombre());
        editorUsuario.launch(destino);
    }

    private void abrirEditorPropio() {
        Intent destino = new Intent(this, PantallaCrudUsuario.class);
        destino.putExtra(PantallaCrudUsuario.EXTRA_MODO, PantallaCrudUsuario.MODO_EDITAR);
        destino.putExtra(PantallaCrudUsuario.EXTRA_CORREO, correoUsuario);
        destino.putExtra(PantallaCrudUsuario.EXTRA_NOMBRE, nombreUsuario);
        editorUsuario.launch(destino);
    }

    private void cargarFichas() {
        progreso.setVisibility(View.VISIBLE);
        new TareaListarTodo().execute();
    }

    private void mostrarFichas(List<Usuario> nuevas) {
        fichas = nuevas == null ? new ArrayList<>() : nuevas;
        adaptador.actualizar(fichas);

        boolean vacio = fichas.isEmpty();

        listaUsuarios.setVisibility(vacio ? View.GONE : View.VISIBLE);
        estadoVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        estadoError.setVisibility(View.GONE);
        vacioDetalle.setText(R.string.vacio_detalle);

        barra.getMenu().findItem(R.id.accion_editar_tus_datos).setVisible(!vacio);
        actualizarSubtitulo(fichas.size());
        construirIndice();
    }

    private void mostrarSinConexion() {
        listaUsuarios.setVisibility(View.GONE);
        indiceLetras.setVisibility(View.GONE);
        estadoVacio.setVisibility(View.GONE);
        estadoError.setVisibility(View.VISIBLE);
        barra.setSubtitle("");
        Sello.rechazado(findViewById(R.id.contenedor), getString(R.string.error_conexion_titulo));
    }

    private void actualizarSubtitulo(int total) {
        barra.setSubtitle(getResources().getQuantityString(R.plurals.registros, total, total));
    }

    private void filtrarPorInicial(String inicial) {
        List<Usuario> filtradas = new ArrayList<>();

        for (Usuario ficha : fichas) {
            String nombre = ficha.getNombre() == null ? "" : ficha.getNombre().trim();

            if (inicial.equals(LETRA_TODOS) || nombre.toUpperCase(Locale.getDefault()).startsWith(inicial)) {
                filtradas.add(ficha);
            }
        }

        adaptador.actualizar(filtradas);

        boolean vacio = filtradas.isEmpty();

        listaUsuarios.setVisibility(vacio ? View.GONE : View.VISIBLE);
        estadoVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        vacioDetalle.setText(R.string.vacio_filtro);

        if (!vacio) {
            actualizarSubtitulo(filtradas.size());
        }
    }

    private void construirIndice() {
        indiceLetras.removeAllViews();
        letraActiva = null;
        indiceLetras.setVisibility(fichas.isEmpty() ? View.GONE : View.VISIBLE);

        if (fichas.isEmpty()) {
            return;
        }

        List<String> iniciales = new ArrayList<>();
        iniciales.add(LETRA_TODOS);

        for (char letra = 'A'; letra <= 'Ñ'; letra++) {
            for (Usuario ficha : fichas) {
                String nombre = ficha.getNombre() == null ? "" : ficha.getNombre().trim();

                if (!nombre.isEmpty() && Character.toUpperCase(nombre.charAt(0)) == letra) {
                    iniciales.add(String.valueOf(letra));
                    break;
                }
            }
        }

        for (String inicial : iniciales) {
            indiceLetras.addView(crearLetra(inicial));
        }

        marcarLetra(indiceLetras.getChildAt(0));
    }

    private View crearLetra(String inicial) {
        TextView letra = new TextView(this);
        letra.setText(inicial);
        letra.setGravity(Gravity.CENTER);
        letra.setTextAppearance(R.style.TextAppearance_Taller3_MonoEtiqueta);
        letra.setTextColor(ContextCompat.getColor(this, R.color.md_on_surface_variant));
        letra.setMinWidth(Interfaz.dp(this, 48));
        letra.setMinHeight(Interfaz.dp(this, 48));
        letra.setPadding(0, 0, 0, 0);
        letra.setClickable(true);
        letra.setFocusable(true);
        letra.setContentDescription(inicial);

        LinearLayout.LayoutParams medidas = new LinearLayout.LayoutParams(
                Interfaz.dp(this, 48), Interfaz.dp(this, 48));
        medidas.setMarginEnd(Interfaz.dp(this, 2));
        letra.setLayoutParams(medidas);

        TypedValue valor = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, valor, true)) {
            letra.setBackgroundResource(valor.resourceId);
        }

        letra.setOnClickListener(vista -> {
            marcarLetra((TextView) vista);
            filtrarPorInicial(inicial);
        });

        return letra;
    }

    private void marcarLetra(View letra) {
        if (letraActiva != null && letraActiva != letra) {
            letraActiva.setBackgroundResource(android.R.color.transparent);
            letraActiva.setTextColor(ContextCompat.getColor(this, R.color.md_on_surface_variant));
        }

        letraActiva = (TextView) letra;

        GradientDrawable activo = new GradientDrawable();
        activo.setColor(ContextCompat.getColor(this, R.color.md_primary));
        activo.setCornerRadius(Interfaz.dp(this, 6));
        letraActiva.setBackground(activo);
        letraActiva.setTextColor(ContextCompat.getColor(this, R.color.md_on_primary));
    }

    private class TareaListarTodo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... nada) {
            return enviarPeticion("accion", "listar");
        }

        @Override
        protected void onPostExecute(String respuesta) {
            progreso.setVisibility(View.GONE);

            if (!RespuestaServidor.esRespuestaValida(respuesta)) {
                mostrarSinConexion();
                return;
            }

            Type tipoLista = new TypeToken<List<Usuario>>() {
            }.getType();

            List<Usuario> leidas = new Gson().fromJson(respuesta.trim(), tipoLista);
            mostrarFichas(leidas);
        }
    }
}
