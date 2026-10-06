package com.example.taller3.controladores;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taller3.R;

public class PreferenciasServidor {

    private static final String ARCHIVO = "servidor";
    private static final String CLAVE_BASE = "base";

    public static String baseGuardada(Context contexto) {
        return preferencias(contexto).getString(CLAVE_BASE, null);
    }

    public static void guardarBase(Context contexto, String base) {
        preferencias(contexto).edit().putString(CLAVE_BASE, base).apply();
    }

    public static String basePorDefecto(Context contexto) {
        return contexto.getString(R.string.url_base);
    }

    public static String baseEnUso(Context contexto) {
        String guardada = baseGuardada(contexto);

        return guardada == null || guardada.trim().isEmpty() ? basePorDefecto(contexto) : guardada;
    }

    private static SharedPreferences preferencias(Context contexto) {
        return contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }
}
