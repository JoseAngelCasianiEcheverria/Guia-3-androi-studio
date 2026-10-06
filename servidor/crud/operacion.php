<?php

require_once __DIR__ . '/../bd/conexion_bd.php';

header('Content-Type: application/json; charset=utf-8');

function responder($datos)
{
    echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    exit;
}

function fallar($mensaje)
{
    responder(array('mensaje' => 'ERROR', 'error' => $mensaje));
}

function parametro($nombre)
{
    if (!isset($_REQUEST[$nombre]) || trim($_REQUEST[$nombre]) === '') {
        fallar('Falta el parametro: ' . $nombre);
    }

    return trim($_REQUEST[$nombre]);
}

function existeUsuario($conexion, $email)
{
    $sentencia = consultar($conexion, 'SELECT email FROM Usuarios WHERE email = %s', array($email));

    return $sentencia->num_rows > 0;
}

$acciones = array(
    'login' => 'login',
    'agregar' => 'Agregar',
    'editar' => 'editar',
    'listar' => 'listar',
    'eliminar' => 'eliminar'
);

$peticion = isset($_REQUEST['accion']) ? strtolower($_REQUEST['accion']) : '';
$accion = isset($acciones[$peticion]) ? $acciones[$peticion] : null;

try {
    switch ($accion) {
        case 'login':
            $email = parametro('email');
            $clave = parametro('psw');
            $conexion = conectar();
            $fila = consultar(
                $conexion,
                'SELECT email, nombre FROM Usuarios WHERE email = %s AND password = %s',
                array($email, sha1($clave))
            )->fetch_assoc();

            if ($fila === null) {
                fallar('Correo o contrasena incorrectos');
            }

            responder(array(
                'mensaje' => 'OK',
                'email' => $fila['email'],
                'nombre' => $fila['nombre']
            ));
            break;

        case 'Agregar':
            $email = parametro('email');
            $clave = parametro('psw');
            $nombre = parametro('nombre');

            if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                fallar('El correo no tiene un formato valido');
            }

            $conexion = conectar();

            if (existeUsuario($conexion, $email)) {
                fallar('El correo ya esta registrado');
            }

            consultar(
                $conexion,
                'INSERT INTO Usuarios (email, password, nombre) VALUES (%s, %s, %s)',
                array($email, sha1($clave), $nombre)
            );

            responder(array('mensaje' => 'OK'));
            break;

        case 'editar':
            $email = parametro('email');
            $clave = parametro('psw');
            $nombre = parametro('nombre');
            $conexion = conectar();

            if (!existeUsuario($conexion, $email)) {
                fallar('El usuario no existe');
            }

            consultar(
                $conexion,
                'UPDATE Usuarios SET password = %s, nombre = %s WHERE email = %s',
                array(sha1($clave), $nombre, $email)
            );

            responder(array('mensaje' => 'OK'));
            break;

        case 'listar':
            $conexion = conectar();
            $sentencia = consultar($conexion, 'SELECT email, nombre FROM Usuarios ORDER BY nombre');
            $usuarios = array();

            while ($fila = $sentencia->fetch_assoc()) {
                $usuarios[] = array(
                    'email' => $fila['email'],
                    'nombre' => $fila['nombre']
                );
            }

            responder($usuarios);
            break;

        case 'eliminar':
            $email = parametro('email');
            $conexion = conectar();

            if (!existeUsuario($conexion, $email)) {
                fallar('El usuario no existe');
            }

            consultar($conexion, 'DELETE FROM Usuarios WHERE email = %s', array($email));

            responder(array('mensaje' => 'OK'));
            break;

        default:
            fallar('Accion no reconocida');
    }
} catch (Throwable $error) {
    fallar($error->getMessage());
}
