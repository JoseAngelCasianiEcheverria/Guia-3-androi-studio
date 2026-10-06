# Reglas de ProGuard/R8 para la release.
# El proyecto no usa reflexion ni serializacion que requiera reglas extra:
# Gson accede a los campos por nombre, asi que sus clases deben conservarse.
-keepattributes Signature
-keepattributes *Annotation*

# Gson: conserva los modelos que se deserializan
-keep class com.example.taller3.datos.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Apache Commons / HTTP no se usan (HttpURLConnection nativo), pero por si acaso
-dontwarn org.apache.**