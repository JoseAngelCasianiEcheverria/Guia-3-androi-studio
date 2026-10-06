# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

Primary user: the owner of the machine, working alone from an Android phone or emulator, several times a day. The job is small and concrete: register a person, correct their name or password, remove someone who no longer belongs. There are no roles, no teams, no sharing; every row is reachable and every row is editable.

Secondary constraint on the audience: this app doubles as a graded workshop deliverable (taller) that must visibly work in front of a professor, so its flows have to be demonstrable in seconds.

## Product Purpose

Manage a list of users — `email`, `password`, `nombre` — held in a local MySQL/MariaDB table and reached only through a small PHP REST layer (`crud/operacion.php`, five actions: `login`, `Agregar`, `editar`, `listar`, `eliminar`).

Success is narrow and testable: from the phone, sign in, see the current roster, change a record, delete a record, and see each of those operations confirmed or refused with an honest reason.

## Positioning

The app is the only client of the `crudphpjson` API; the API has no other front end. That makes the phone the single place where the data can be read or changed, and it means the app has to be honest about the server's state rather than optimistic about it.

## Operating Context

- Backend on the same Windows machine: XAMPP Apache (port 80) + MariaDB 10.4 (`crudphpjson`, root without password), started manually per session by `iniciar-entorno-XAMPP.bat`.
- Transport is cleartext HTTP. From the emulator the host is `http://10.0.2.2/Phpproyecto/`; from a physical phone on the same Wi-Fi it is `http://192.168.1.8/Phpproyecto/`, configured in one string resource.
- App is built and installed from Android Studio (`Taller3`), Java + Views/XML, AGP 9.4.1, Gradle 9.6, minSdk 25, targetSdk 37, Gson 2.11 for JSON.
- Network work runs in `AsyncTask` subclasses with a progress indicator, because that is the shape the workshop statement specifies.
- Credentials never travel back from the server: `listar` returns only `email` and `nombre`, so an edit that does not retype the password cannot be sent as-is.
- Passwords are stored as SHA-1 hex in a `VARCHAR(40)` column, a workshop-era choice that fits the stated schema and is not a production scheme.

## Capabilities and Constraints

- Three screens, always in this order: login → roster → user editor. Back must always unwind it.
- Session shape: the app authenticates one email at a time and then acts as that identity; there is no separate "registration" screen, so a failed login offers to become one.
- Roster rows are also the entry point for editing or deleting any user, by long press.
- Deleting is destructive and always confirmed.
- Network failure, empty roster, and server refusal are three distinct states the user must be able to tell apart.
- Layout must survive the system font-size setting up to at least 1.3x.
- Spanish is the product language for every string, including errors.
- Free to rename view ids and to replace every widget: the workshop statement's ids are not binding. Activity class names (`PantallaInicio`, `PantallaListado`, `PantallaCrudUsuario`) are kept for traceability to the statement.
- No image-generation tooling is available in this environment, so the visual world is authored directly in code.

## Evidence on Hand

Real data in `crudphpjson.Usuarios` (`prueba@gmail.com`, `juan@gmail.com`, both with real SHA-1 hashes) and the live PHP endpoints answering over Apache. No brand assets, no logo, no typography license, no product photography; the launcher icon is still the Android Studio placeholder and must be authored as vectors. Nothing about commercial claims, usage scale, or user counts may be invented anywhere in this product.

## Product Principles

1. One job per screen, and the job is named in the top app bar.
2. The server is the source of truth; every operation resolves into a visible, honest outcome (done, refused, unreachable) — never a silent no-op.
3. Never hold or display more of a credential than the task needs; the password is write-only.
4. Anything irreversible is confirmed first, and the confirmation names the record it will destroy.
5. The same build must work on the emulator and on a phone on the local network without code changes beyond the one URL string.
