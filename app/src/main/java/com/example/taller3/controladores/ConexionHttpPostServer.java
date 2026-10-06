package com.example.taller3.controladores;

import android.content.Context;
import android.util.Pair;

import com.example.taller3.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConexionHttpPostServer {

    public static String urlDeLaServidor(Context contexto) {
        return PreferenciasServidor.baseEnUso(contexto) + contexto.getString(R.string.ruta_servicio);
    }

    public static Pair<String, String> parametro(String nombre, String valor) {
        return new Pair<>(nombre, valor == null ? "" : valor);
    }

    public String conexionConElServidor(List<Pair<String, String>> parametros, String rutaDeLaAplicacionWeb) throws IOException {
        HttpURLConnection conexion = (HttpURLConnection) new URL(rutaDeLaAplicacionWeb).openConnection();
        conexion.setRequestMethod("POST");
        conexion.setDoOutput(true);
        conexion.setConnectTimeout(10000);
        conexion.setReadTimeout(10000);
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conexion.setRequestProperty("Accept", "application/json");

        try (OutputStream salida = conexion.getOutputStream()) {
            salida.write(cuerpoDeLaPeticion(parametros).getBytes(StandardCharsets.UTF_8));
        }

        return procesarRespuestaDelServidor(conexion);
    }

    private String cuerpoDeLaPeticion(List<Pair<String, String>> parametros) throws IOException {
        StringBuilder cuerpo = new StringBuilder();

        if (parametros == null) {
            return cuerpo.toString();
        }

        for (Pair<String, String> parametro : parametros) {
            if (cuerpo.length() > 0) {
                cuerpo.append("&");
            }
            cuerpo.append(URLEncoder.encode(parametro.first, "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(parametro.second, "UTF-8"));
        }

        return cuerpo.toString();
    }

    private String procesarRespuestaDelServidor(HttpURLConnection conexion) throws IOException {
        int codigo = conexion.getResponseCode();
        InputStream entrada = (codigo >= 200 && codigo < 300)
                ? conexion.getInputStream()
                : conexion.getErrorStream();

        StringBuilder respuesta = new StringBuilder();

        if (entrada != null) {
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }
            }
        }

        conexion.disconnect();

        return respuesta.toString();
    }
}
