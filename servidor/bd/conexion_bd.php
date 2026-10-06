<?php

const BD_HOST = '127.0.0.1';
const BD_PUERTO = 3306;
const BD_USUARIO = 'root';
const BD_CLAVE = '';
const BD_NOMBRE = 'crudphpjson';

function conectar()
{
    $conexion = @new mysqli(BD_HOST, BD_USUARIO, BD_CLAVE, BD_NOMBRE, BD_PUERTO);

    if ($conexion->connect_errno) {
        throw new RuntimeException('No se pudo conectar a MySQL: ' . $conexion->connect_error);
    }

    $conexion->set_charset('utf8mb4');

    return $conexion;
}

function consultar($conexion, $sql, $parametros = array())
{
    if (count($parametros) > 0) {
        $valores = array();

        foreach ($parametros as $parametro) {
            $valores[] = "'" . $conexion->real_escape_string($parametro) . "'";
        }

        $sql = vsprintf($sql, $valores);
    }

    $resultado = $conexion->query($sql);

    if ($resultado === false) {
        throw new RuntimeException('No se pudo ejecutar la consulta: ' . $conexion->error);
    }

    return $resultado;
}
