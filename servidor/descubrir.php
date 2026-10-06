<?php

header('Content-Type: application/json; charset=utf-8');

function esRedLocal($ip)
{
    if (strpos($ip, '10.') === 0 || strpos($ip, '192.168.') === 0 || strpos($ip, '127.') === 0) {
        return true;
    }

    if ($ip === '::1' || strpos($ip, '::ffff:127.') === 0) {
        return true;
    }

    if (strpos($ip, '172.') === 0) {
        $segmento = (int) explode('.', $ip)[1];

        return $segmento >= 16 && $segmento <= 31;
    }

    return false;
}

function direccionesLocales()
{
    $direcciones = array();

    foreach (gethostbynamel(gethostname()) as $candidata) {
        if (filter_var($candidata, FILTER_VALIDATE_IP, FILTER_FLAG_IPV4)
            && strpos($candidata, '127.') !== 0
            && strpos($candidata, '169.254.') !== 0) {
            $direcciones[] = $candidata;
        }
    }

    return $direcciones;
}

$cliente = isset($_SERVER['REMOTE_ADDR']) ? $_SERVER['REMOTE_ADDR'] : '';

if (!esRedLocal($cliente)) {
    http_response_code(403);
    echo json_encode(array('mensaje' => 'ERROR', 'error' => 'Solo responde en la red local'));
    exit;
}

$ips = direccionesLocales();

if (isset($_SERVER['SERVER_ADDR']) && filter_var($_SERVER['SERVER_ADDR'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4)
    && strpos($_SERVER['SERVER_ADDR'], '127.') !== 0
    && !in_array($_SERVER['SERVER_ADDR'], $ips, true)) {
    $ips[] = $_SERVER['SERVER_ADDR'];
}

$preferida = '';

foreach ($ips as $candidata) {
    if (strpos($candidata, '192.168.') === 0) {
        $preferida = $candidata;
        break;
    }
}

if ($preferida === '' && count($ips) > 0) {
    $preferida = $ips[0];
}

echo json_encode(array(
    'ip' => $preferida,
    'ips' => $ips,
    'host' => gethostname(),
    'base' => rtrim(str_replace('\\', '/', dirname($_SERVER['SCRIPT_NAME'])), '/') . '/',
    'cliente' => $cliente
));
