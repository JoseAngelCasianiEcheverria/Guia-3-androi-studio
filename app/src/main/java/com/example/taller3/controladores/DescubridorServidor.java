package com.example.taller3.controladores;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class DescubridorServidor {

    private static final int ESPERA_MS = 2500;

    public static String descubrir(String rutaRelativa, String basePorDefecto) {
        try {
            String difusion = direccionDeDifusion();

            if (difusion == null) {
                return null;
            }

            String ruta = java.net.URI.create(basePorDefecto).getPath();
            String url = "http://" + difusion + ruta + rutaRelativa;

            HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(ESPERA_MS);
            conexion.setReadTimeout(ESPERA_MS);
            conexion.setRequestProperty("Accept", "application/json");

            if (conexion.getResponseCode() != 200) {
                conexion.disconnect();
                return null;
            }

            String cuerpo = leer(conexion.getInputStream());
            conexion.disconnect();

            JSONObject respuesta = new JSONObject(cuerpo);
            JSONArray candidatas = respuesta.optJSONArray("ips");
            String elegida = respuesta.optString("ip");

            if (candidatas != null) {
                String miRed = redDe(miIpv4());

                for (int i = 0; i < candidatas.length(); i++) {
                    if (miRed != null && miRed.equals(redDe(candidatas.optString(i)))) {
                        elegida = candidatas.optString(i);
                        break;
                    }
                }
            }

            return elegida.isEmpty() ? null : elegida;
        } catch (Exception e) {
            return null;
        }
    }

    public static String miIpv4() {
        NetworkInterface red = redDelDispositivo(true);

        return red == null ? null : ipv4De(red);
    }

    private static NetworkInterface redDelDispositivo(boolean preferirWiFi) {
        try {
            NetworkInterface wifi = null;
            NetworkInterface cualquiera = null;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface red = interfaces.nextElement();

                if (!red.isUp() || red.isLoopback() || ipv4De(red) == null) {
                    continue;
                }

                if (red.getName().toLowerCase().startsWith("wlan")) {
                    wifi = red;
                    break;
                }

                cualquiera = red;
            }

            return preferirWiFi && wifi != null ? wifi : cualquiera;
        } catch (Exception e) {
            return null;
        }
    }

    private static String ipv4De(NetworkInterface red) {
        Enumeration<InetAddress> direcciones = red.getInetAddresses();

        while (direcciones != null && direcciones.hasMoreElements()) {
            InetAddress local = direcciones.nextElement();

            if (local instanceof Inet4Address && !local.isLoopbackAddress()) {
                return local.getHostAddress();
            }
        }

        return null;
    }

    private static String direccionDeDifusion() {
        String ip = miIpv4();

        if (ip == null) {
            return null;
        }

        String[] partes = ip.split("\\.");
        int[] mascara = mascara();

        if (partes.length != 4 || mascara == null) {
            return null;
        }

        StringBuilder difusion = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                difusion.append('.');
            }

            int octeto = Integer.parseInt(partes[i]) | (~mascara[i] & 0xFF);
            difusion.append(octeto);
        }

        return difusion.toString();
    }

    private static int[] mascara() {
        try {
            NetworkInterface red = redDelDispositivo(true);

            if (red == null) {
                return new int[]{255, 255, 255, 0};
            }

            for (InterfaceAddress direccion : red.getInterfaceAddresses()) {
                if (direccion.getAddress() instanceof Inet4Address) {
                    return aOctetos(direccion.getNetworkPrefixLength());
                }
            }
        } catch (Exception e) {
            return new int[]{255, 255, 255, 0};
        }

        return new int[]{255, 255, 255, 0};
    }

    private static int[] aOctetos(int prefijo) {
        int[] octetos = new int[4];
        int restante = prefijo;

        for (int i = 0; i < 4 && restante > 0; i++) {
            int bits = Math.min(restante, 8);
            octetos[i] = (0xFF << (8 - bits)) & 0xFF;
            restante -= bits;
        }

        return octetos;
    }

    private static String redDe(String ip) {
        if (ip == null) {
            return null;
        }

        String[] partes = ip.split("\\.");

        return partes.length == 4 ? partes[0] + "." + partes[1] + "." + partes[2] : null;
    }

    private static String leer(InputStream entrada) throws Exception {
        StringBuilder texto = new StringBuilder();

        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                texto.append(linea);
            }
        }

        return texto.toString();
    }
}
