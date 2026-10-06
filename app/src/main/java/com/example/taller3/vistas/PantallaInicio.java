package com.example.taller3.vistas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.view.WindowCompat;

import com.example.taller3.R;
import com.example.taller3.controladores.DescubridorServidor;
import com.example.taller3.controladores.PreferenciasServidor;
import com.example.taller3.controladores.RespuestaServidor;
import com.example.taller3.controladores.Sello;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URI;

@SuppressWarnings("deprecation")
public class PantallaInicio extends PantallaBase {

    private TextView campoCorreo;
    private TextView campoClave;
    private TextView campoNombre;
    private TextView campoServidor;
    private TextInputLayout capaNombre;
    private TextInputLayout capaServidor;
    private MaterialCheckBox casoRegistro;
    private MaterialButton botonIngresar;
    private LinearProgressIndicator progreso;
    private View contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);

        contenedor = findViewById(R.id.contenedor);
        mostrarHoja(contenedor, true);

        campoCorreo = findViewById(R.id.campo_correo);
        campoClave = findViewById(R.id.campo_clave);
        campoNombre = findViewById(R.id.campo_nombre);
        campoServidor = findViewById(R.id.campo_servidor);
        capaNombre = findViewById(R.id.capa_nombre);
        capaServidor = findViewById(R.id.capa_servidor);
        casoRegistro = findViewById(R.id.caso_registro);
        botonIngresar = findViewById(R.id.btn_ingresar);
        progreso = findViewById(R.id.progreso);

        campoServidor.setText(PreferenciasServidor.baseEnUso(this));

        casoRegistro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton boton, boolean marcado) {
                capaNombre.setVisibility(marcado ? View.VISIBLE : View.GONE);
                botonIngresar.setText(marcado ? R.string.accion_crear_ficha : R.string.accion_ingresar);

                if (!marcado) {
                    campoNombre.setText("");
                }
            }
        });

        findViewById(R.id.btn_ingresar).setOnClickListener(v -> enviar());
        findViewById(R.id.btn_salir).setOnClickListener(v -> finish());

        findViewById(R.id.btn_servidor).setOnClickListener(v ->
                capaServidor.setVisibility(capaServidor.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE));

        campoServidor.setOnFocusChangeListener((vista, tieneFoco) -> {
            if (!tieneFoco) {
                guardarServidor();
            }
        });

        new TareaDescubrirServidor().execute();
    }

    private void guardarServidor() {
        String base = normalizar(campoServidor.getText().toString());

        if (base.isEmpty()) {
            campoServidor.setText(PreferenciasServidor.basePorDefecto(this));
            return;
        }

        PreferenciasServidor.guardarBase(this, base);
        campoServidor.setText(base);
    }

    static String normalizar(String texto) {
        String base = texto.trim();

        if (base.isEmpty()) {
            return "";
        }

        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            base = "http://" + base;
        }

        return base.endsWith("/") ? base : base + "/";
    }

    private void enviar() {
        guardarServidor();

        String correo = campoCorreo.getText().toString().trim();
        String clave = campoClave.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Sello.rechazado(contenedor, getString(R.string.aviso_campos_vacios));
            return;
        }

        progreso.setVisibility(View.VISIBLE);

        if (casoRegistro.isChecked()) {
            String nombre = campoNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                progreso.setVisibility(View.GONE);
                Sello.rechazado(contenedor, getString(R.string.aviso_campos_vacios));
                return;
            }

            new TareaRegistro().execute(correo, clave, nombre);
        } else {
            new TareaLoginEnSegunPlano().execute(correo, clave);
        }
    }

    private void abrirListado(String correo, String nombre) {
        Intent destino = new Intent(this, PantallaListado.class);
        destino.putExtra(PantallaListado.EXTRA_CORREO, correo);
        destino.putExtra(PantallaListado.EXTRA_NOMBRE, nombre);
        startActivity(destino);
    }

    private void falla(String veredicto) {
        progreso.setVisibility(View.GONE);
        Sello.rechazado(contenedor, veredicto);
    }

    private void operacionCerrada() {
        progreso.setVisibility(View.GONE);
    }

    private class TareaDescubrirServidor extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... nada) {
            return DescubridorServidor.descubrir(getString(R.string.ruta_descubrimiento),
                    PreferenciasServidor.basePorDefecto(PantallaInicio.this));
        }

        @Override
        protected void onPostExecute(String ip) {
            if (ip == null) {
                return;
            }

            String base = "http://" + ip + URI.create(PreferenciasServidor.basePorDefecto(PantallaInicio.this)).getPath();

            PreferenciasServidor.guardarBase(PantallaInicio.this, base);
            campoServidor.setText(base);
            Sello.recibido(contenedor, getString(R.string.sello_servidor, ip));
        }
    }

    private class TareaLoginEnSegunPlano extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... datos) {
            return enviarPeticion("accion", "login", "email", datos[0], "psw", datos[1]);
        }

        @Override
        protected void onPostExecute(String respuesta) {
            if (!RespuestaServidor.esRespuestaValida(respuesta)) {
                falla(getString(R.string.error_conexion_titulo));
                return;
            }

            if (RespuestaServidor.esOk(respuesta)) {
                campoClave.setText("");
                operacionCerrada();
                Sello.recibido(contenedor, getString(R.string.sello_recibido));
                abrirListado(campoCorreo.getText().toString().trim(), RespuestaServidor.nombre(respuesta));
            } else {
                falla(RespuestaServidor.mensaje(respuesta));
            }
        }
    }

    private class TareaRegistro extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... datos) {
            return enviarPeticion("accion", "Agregar", "email", datos[0], "psw", datos[1], "nombre", datos[2]);
        }

        @Override
        protected void onPostExecute(String respuesta) {
            if (!RespuestaServidor.esRespuestaValida(respuesta)) {
                falla(getString(R.string.error_conexion_titulo));
                return;
            }

            if (RespuestaServidor.esOk(respuesta)) {
                String nombre = campoNombre.getText().toString().trim();
                campoClave.setText("");
                operacionCerrada();
                Sello.recibido(contenedor, getString(R.string.sello_creado));
                abrirListado(campoCorreo.getText().toString().trim(), nombre);
            } else {
                falla(RespuestaServidor.mensaje(respuesta));
            }
        }
    }
}
