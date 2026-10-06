package com.example.taller3.vistas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.WindowCompat;


import com.example.taller3.R;
import com.example.taller3.controladores.RespuestaServidor;
import com.example.taller3.controladores.Sello;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

@SuppressWarnings("deprecation")
public class PantallaCrudUsuario extends PantallaBase {

    public static final String EXTRA_MODO = "modo";
    public static final String EXTRA_CORREO = "correo";
    public static final String EXTRA_NOMBRE = "nombre";

    public static final String MODO_AGREGAR = "agregar";
    public static final String MODO_EDITAR = "editar";

    private TextView campoCorreo;
    private TextView campoClave;
    private TextView campoNombre;
    private MaterialToolbar barra;
    private LinearProgressIndicator progreso;
    private View botonEliminar;
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_crud_usuario);

        mostrarHoja(findViewById(R.id.contenedor), true);

        campoCorreo = findViewById(R.id.campo_correo);
        campoClave = findViewById(R.id.campo_clave);
        campoNombre = findViewById(R.id.campo_nombre);
        barra = findViewById(R.id.barra);
        progreso = findViewById(R.id.progreso);
        botonEliminar = findViewById(R.id.btn_eliminar);

        String recibido = getIntent().getStringExtra(EXTRA_MODO);
        modo = MODO_AGREGAR.equals(recibido) ? MODO_AGREGAR : MODO_EDITAR;

        boolean esEdicion = modo.equals(MODO_EDITAR);

        barra.setTitle(esEdicion ? R.string.titulo_editar_usuario : R.string.titulo_nuevo_usuario);
        barra.setNavigationOnClickListener(v -> finish());

        campoCorreo.setText(getIntent().getStringExtra(EXTRA_CORREO));
        campoNombre.setText(getIntent().getStringExtra(EXTRA_NOMBRE));

        if (esEdicion) {
            campoCorreo.setEnabled(false);
            botonEliminar.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_guardar).setOnClickListener(v -> guardar());
        findViewById(R.id.btn_cancelar).setOnClickListener(v -> finish());
        botonEliminar.setOnClickListener(v ->
                confirmarDesprender(campoNombre.getText().toString(), this::desprender));
    }

    private boolean esAgregar() {
        return modo.equals(MODO_AGREGAR);
    }

    private void guardar() {
        String correo = campoCorreo.getText().toString().trim();
        String clave = campoClave.getText().toString().trim();
        String nombre = campoNombre.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty() || nombre.isEmpty()) {
            Sello.rechazado(findViewById(R.id.contenedor), getString(R.string.aviso_campos_vacios));
            return;
        }

        progreso.setVisibility(View.VISIBLE);
        new TareaGuardar().execute(esAgregar() ? "Agregar" : "editar", correo, clave, nombre);
    }

    private void desprender() {
        progreso.setVisibility(View.VISIBLE);
        new TareaGuardar().execute("eliminar", campoCorreo.getText().toString().trim(), "", "");
    }

    private void cerrarConCambios() {
        setResult(RESULT_OK);
        finish();
    }

    private class TareaGuardar extends AsyncTask<String, Void, String> {

        private String accion;

        @Override
        protected String doInBackground(String... datos) {
            accion = datos[0];

            if (accion.equals("eliminar")) {
                return enviarPeticion("accion", accion, "email", datos[1]);
            }

            return enviarPeticion("accion", accion, "email", datos[1], "psw", datos[2], "nombre", datos[3]);
        }

        @Override
        protected void onPostExecute(String respuesta) {
            progreso.setVisibility(View.GONE);

            if (!RespuestaServidor.esRespuestaValida(respuesta)) {
                Sello.rechazado(findViewById(R.id.contenedor), getString(R.string.error_conexion_titulo));
                return;
            }

            if (!RespuestaServidor.esOk(respuesta)) {
                Sello.rechazado(findViewById(R.id.contenedor), RespuestaServidor.mensaje(respuesta));
                return;
            }

            Sello.recibido(findViewById(R.id.contenedor), getString(veredictoDe(accion)));
            cerrarConCambios();
        }

        private int veredictoDe(String accionRealizada) {
            switch (accionRealizada) {
                case "eliminar":
                    return R.string.sello_borrado;
                case "Agregar":
                    return R.string.sello_creado;
                default:
                    return R.string.sello_modificado;
            }
        }
    }
}
