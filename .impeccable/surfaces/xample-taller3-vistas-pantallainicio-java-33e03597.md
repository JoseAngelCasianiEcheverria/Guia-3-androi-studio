---
version: 1
slug: "xample-taller3-vistas-pantallainicio-java-33e03597"
primary_target: "app/src/main/java/com/example/taller3/vistas/PantallaInicio.java"
related_targets: ["app/src/main/java/com/example/taller3/vistas/PantallaListado.java","app/src/main/java/com/example/taller3/vistas/PantallaCrudUsuario.java"]
---

# Surface brief — Taller3 (Android CRUD de Usuarios)

## Scope and visitor mode

Three native Android screens (Views/XML, Material 3) that make up the whole product: sign in, read the roster, edit one record. Mode: **operate**. The visitor is the owner of the machine, alone, several times a day, on a phone or an emulator, checking that a record is right and changing it. Nothing here is marketing; the job is the job.

## Audience, job, action, content, constraints

- **Job:** register a person, fix a name or a password, delete someone who no longer belongs.
- **Action:** one destructive or one committing action per screen, always resolvable into an honest outcome.
- **Real content:** `email`, `nombre`, the current roster from `crudphpjson.Usuarios` (`prueba@gmail.com`, `juan@gmail.com`), and the server's own Spanish refusal sentences. No invented counts, no invented people.
- **Constraints:** Spanish everywhere; `AsyncTask` shape fixed by the workshop statement; Activity class names kept; ids free; cleartext HTTP to `http://10.0.2.2/Phpproyecto/`; must survive 1.3x font scale; light and dark both designed; 48dp targets; system Back always unwinds.

## Chosen direction

**Telegrama / despacho telegráfico** — the typed message slip. A world of carbon copies, stencil-cut field labels in a fixed column, monospace machine content, hairline rules, torn perforated edges, and rubber stamps that record what happened to a message.

Its four contributions and nothing else: type (monospace for every machine value, system sans for prose, tracked all-caps stencil labels), palette (cool carbon ground, ink surfaces, deep ink-blue for the single primary action, signal red reserved for destructive and refused states only), density (the form's compact rhythm: 1dp rules, 56dp rows, a 96dp label column on the form screen, tabular numerals), and one signature move.

## Memorable moment

**The stamp press.** No toast ever. Every resolved operation lands as a stamped slip — a 1.5dp bordered chip, monospace, tracked all-caps verdict — that scales in from 0.94 and holds: `RECIBIDO`, `MODIFICADO`, `BORRADO`, `NO RECIBIDO`. And the confirmation that precedes a deletion is a torn slip: a dashed perforation rule across the dialog, the telegram's own way of saying this one gets torn off.

## Direction contract

**THESIS.** Every roster app looks like a list of avatars and chips; this one looks like a telegraph slip whose stamps are the product's real state — the interface reports outcomes the way an office reports a message, in stamped verdicts, and it refuses to hide a refusal behind a spinner.

**OWN-WORLD.** Cool carbon ground (`#EDF0F2`) under white ink surfaces, deep ink-blue `#12456E` for the one primary action, signal red `#B3261E` used for exactly two jobs — destructive and refused. Machine content is monospace, tabular, letterspaced; field labels are tracked all-caps sans at Label Small; a 1dp graphite rule separates rows; a dashed 1dp perforation is the only ornament in the product.

**STORY.** The visitor signs in with a postal code they already carry, lands on a roster of names and addresses, filters nothing and edits one line, and watches the office confirm it in ink: `MODIFICADO`. Deleting asks for the slip to be torn first.

**FIRST VIEWPORT.** Login: 24dp inset shell, a 44dp ring punch mark (the telegram's eyelet hole) above a stencil label `GESTIÓN DE USUARIOS`, two outlined fields on ruled baselines with helper text in mono, a filled `INGRESAR` spanning full width at 56dp, and a text button `SALIR` beneath it — primary action at thumb reach, nothing else competing.

**FORM.** Assigned direction is candidate 4 of the grounded list, seed key `6fe2c9fa` (roll degraded: no challengers, no quality-bar boards).

**FINISH.** unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance.

## Unresolved decisions

- App launcher label: the generated `Taller3` is wrong for a daily-use app; changing it to `Usuarios` pending the owner's confirmation, with the application id untouched.
- Dark theme is designed as a first-class scheme from the start, not derived by inversion.
