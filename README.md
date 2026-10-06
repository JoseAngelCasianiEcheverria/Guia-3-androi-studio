# Guía 3 — CRUD de Usuarios (Android + PHP + MySQL)

App Android (Java, Views) que gestiona una lista de usuarios contra una API REST propia en
PHP + MySQL/MariaDB. Tres pantallas: **login → listado → gestión**.

![Pantalla de login](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

## Qué hay aquí

| Ruta | Contenido |
|---|---|
| `app/` | Proyecto Android (Kotlin DSL, AGP 9.4.1, Gradle 9.6, minSdk 25, targetSdk 37) |
| `servidor/` | Backend PHP que se copia a `C:\xampp\htdocs\Phpproyecto` |
| `PRODUCT.md` | Contexto de producto (usuarios, propósito, restricciones) |

## Puesta en marcha

### 1. Base de datos (XAMPP: MariaDB, root sin clave)

```sql
CREATE DATABASE IF NOT EXISTS crudphpjson CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE crudphpjson;
CREATE TABLE IF NOT EXISTS Usuarios (
    email    VARCHAR(70) PRIMARY KEY NOT NULL,
    password VARCHAR(40) NOT NULL,   -- SHA-1 en hexadecimal (40 caracteres)
    nombre   VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
INSERT IGNORE INTO Usuarios VALUES ('prueba@gmail.com', SHA1('1234'), 'JUAN');
```

### 2. Backend

Copiar `servidor/` dentro de `C:\xampp\htdocs\` y abrirlo como proyecto PHP de NetBeans
(origen: `C:\xampp\htdocs\Phpproyecto`, URL: `http://localhost/Phpproyecto/`).

| Archivo | Rol |
|---|---|
| `bd/conexion_bd.php` | `conectar()` + `consultar($sql, $params)` con escape y `vsprintf` |
| `crud/operacion.php` | Acción por `?accion=`: `login`, `Agregar`, `editar`, `listar`, `eliminar` |
| `descubrir.php` | Devuelve la IP del equipo (solo en red local) para que la app lo encuentre |

Probar en el navegador:

```
http://localhost/Phpproyecto/crud/operacion.php?accion=listar
http://localhost/Phpproyecto/crud/operacion.php?accion=login&email=prueba@gmail.com&psw=1234
```

### 3. App

`app/src/main/res/values/strings.xml` → `url_base` (por defecto `http://192.168.1.8/Phpproyecto/`).
Desde el emulador usar `http://10.0.2.2/Phpproyecto/`.

```powershell
.\gradlew.bat :app:assembleDebug
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

La app **no necesita que la IP sea fija**: al arrancar pregunta al servidor por difusión
local (`descubrir.php`) y guarda la respuesta. Si no responde, usa `url_base` y, si tampoco,
el campo **Servidor** de la pantalla de login (editable y persistente).

## Detalles técnicos

- **Sin librerías JAR externas**: HTTP con `HttpURLConnection`, JSON con Gson 2.11.0.
- **Hilos**: `AsyncTask` (`TareaLoginEnSegunPlano`, `TareaGuardar`, `TareaListarTodo`) con
  `LinearProgressIndicator`; el resultado se comunica con un Snackbar con forma de "sello".
- **UI**: Material 3 con tema propio (tinta azul, rojo señal solo para destructivo y
  rechazos), claro y oscuro, edge-to-edge y layouts Inflammation en tres `activity_*.xml`.
- **Tabla**: `email` es la clave primaria, así que en modo edición el campo va deshabilitado.
- **Contraseñas**: SHA-1 por el `VARCHAR(40)` del enunciado. Es una decisión **académica**,
  no de producción: para producción usa bcrypt/Argon2 y HTTPS.

## Requisitos

- Windows, JDK 17 (Temurin) para Gradle — el `gradle.properties` ya lo fija.
- XAMPP (Apache 2.4 + PHP 8.2 + MariaDB 10.4).
- Android Studio con SDK 37.