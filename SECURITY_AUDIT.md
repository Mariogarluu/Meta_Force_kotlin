# Auditoría de seguridad Kotlin / Android — Meta-Force

> Este documento resume el estado de seguridad de la app Android
> (módulo `kotlin/app`) y complementa la auditoría OWASP ZAP del backend.

## 1. Build de release endurecido

- [x] `isMinifyEnabled = true` y `isShrinkResources = true` en `buildTypes.release`.
- [x] `proguard-rules.pro` actualizado con reglas mínimas para:
  - Jetpack Compose.
  - Hilt / DI.
  - Supabase Kotlin (`kotlinx.serialization`).
  - Supresión segura de `org.slf4j.impl.StaticLoggerBinder`.
- [x] `./gradlew assembleRelease` ejecutado con éxito.

APK generado (local, no versionado en git):

- Ruta aproximada: `kotlin/app/build/outputs/apk/release/app-release.apk`

## 2. Análisis estático (detekt)

- [x] Plugin `io.gitlab.arturbosch.detekt` integrado en el módulo `app`.
- [x] Configuración base en `kotlin/config/detekt/detekt.yml`.
- [x] Comando ejecutado:

```bash
cd kotlin
./gradlew detekt
```

### 2.1 Resumen de hallazgos (primer pase)

> NOTA: Rellenar de forma resumida con los principales tipos de issues.

- Categorías frecuentes detectadas:
  - `WildcardImport`
  - `MaxLineLength`
  - `MagicNumber`
  - `UnusedPrivateProperty`
  - `NewLineAtEndOfFile`
- Estado actual:
  - [ ] Issues críticos abiertos
  - [x] Solo issues de estilo / mantenibilidad (no bloqueantes para release)

## 3. Análisis dinámico APK (MobSF)

> Este apartado requiere ejecutar MobSF en un entorno local o en la nube.
> Desde aquí solo se documenta el procedimiento recomendado.

### 3.1 Procedimiento recomendado

1. Generar APK release:

```bash
cd kotlin
./gradlew assembleRelease
```

2. Lanzar instancia de MobSF (Docker recomendado):

```bash
docker run --rm -it -p 8000:8000 opensecurity/mobile-security-framework-mobsf
```

3. Acceder a `http://localhost:8000` y subir:

- `kotlin/app/build/outputs/apk/release/app-release.apk`

4. Revisar secciones clave del informe:

- Permisos peligrosos (`READ_SMS`, `WRITE_EXTERNAL_STORAGE`, etc.).
- Exportación de `Activities`, `Services` y `BroadcastReceivers`.
- Uso de almacenamiento inseguro (SharedPreferences sin cifrado, ficheros world-readable).
- Exposición de claves en recursos (`strings.xml`, código, `BuildConfig`).
- Configuración de `networkSecurityConfig` y certificados.

### 3.2 Checklist de hallazgos MobSF

Marcar una vez revisado el informe real:

- [ ] No se han detectado claves Supabase (`SUPABASE_KEY`, `SUPABASE_URL`) de tipo `service_role` dentro del APK.
- [ ] No hay permisos de alto riesgo innecesarios para la funcionalidad actual.
- [ ] No hay componentes exportados sin necesidad (`exported=\"true\"`).
- [ ] No se almacenan credenciales ni tokens en texto plano.
- [ ] No se permite tráfico en claro (HTTP) salvo endpoints de desarrollo controlados.

## 4. Conclusión

Completar tras la revisión de MobSF:

- Nivel de riesgo residual aceptado para la versión de release.
- Acciones de hardening adicionales planificadas (si las hubiera).

