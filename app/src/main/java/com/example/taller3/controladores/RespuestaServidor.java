package com.example.taller3.controladores;

import org.json.JSONException;
import org.json.JSONObject;

public class RespuestaServidor {

    private static final String MENSAJE = "mensaje";
    private static final String ERROR = "error";
    private static final String NOMBRE = "nombre";
    private static final String OK = "OK";

    public static boolean esRespuestaValida(String respuesta) {
        return respuesta != null && !respuesta.trim().isEmpty();
    }

    public static boolean esOk(String respuesta) {
        try {
            return OK.equals(new JSONObject(respuesta).optString(MENSAJE));
        } catch (JSONException e) {
            return false;
        }
    }

    public static String mensaje(String respuesta) {
        try {
            JSONObject json = new JSONObject(respuesta);
            String error = json.optString(ERROR);

            return error.isEmpty() ? json.optString(MENSAJE) : error;
        } catch (JSONException e) {
            return "Respuesta inválida del servidor";
        }
    }

    public static String nombre(String respuesta) {
        try {
            return new JSONObject(respuesta).optString(NOMBRE);
        } catch (JSONException e) {
            return "";
        }
    }
}
