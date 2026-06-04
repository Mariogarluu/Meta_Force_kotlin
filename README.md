# 💪 Meta Force

<div align="center">

![Meta Force Logo](app/src/main/res/drawable/app_logo.png)

**Aplicación Android de gestión fitness integral con IA integrada**

[![Android](https://img.shields.io/badge/Android-26%2B-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-purple)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-DI-orange)](https://dagger.dev/hilt/)
[![API](https://img.shields.io/badge/API-Supabase-3ECF8E?logo=supabase)](https://qybgnrlszozjhimewkel.supabase.co)

</div>

---

## 📖 Descripción

**Meta Force** es una aplicación Android moderna y completa para la gestión de tu vida fitness. Combina planes de entrenamiento personalizados, seguimiento nutricional, gestión de clases en el gimnasio y un **asistente de IA** que genera planes adaptados a tus objetivos.

Diseñada con una interfaz oscura y elegante inspirada en Material Design 3, Meta Force ofrece una experiencia premium para tanto usuarios de gimnasio como administradores de centros deportivos.

---

## ✨ Características principales

### 🔐 Autenticación y perfil
- Registro e inicio de sesión seguro con JWT
- Gestión completa del perfil: nombre, altura, peso, edad, género, nivel de actividad y objetivos fitness
- Subida de foto de perfil
- Persistencia de sesión con Jetpack DataStore

### 🏋️ Entrenamientos
- Consulta tus planes de entrenamiento personalizados
- Visualización de ejercicios organizados por día de la semana
- Detalle de cada ejercicio: series, repeticiones, peso, descanso y notas
- Información de grupos musculares trabajados
- Creación y eliminación de rutinas

### 🥗 Dietas y nutrición
- Planes de alimentación personalizados
- Distribución de comidas con macronutrientes (calorías, proteínas, carbohidratos, grasas)
- Objetivo calórico diario
- Creación y eliminación de dietas

### 📅 Clases grupales
- Catálogo completo de clases disponibles en cada centro
- Inscripción y baja en clases con un toque
- Horarios detallados con día y hora de inicio/fin
- Información del entrenador asignado
- Filtrado por centro deportivo
- Panel de administración para gestionar clases

### 🏢 Centros deportivos
- Listado de todos los centros disponibles con dirección y contacto
- Equipamiento disponible en cada centro (cardio, fuerza, peso libre, funcional)
- Estado de las máquinas en tiempo real (operativo, mantenimiento, fuera de servicio)
- Funciones de administración para gestionar centros y equipos

### 🤖 Asistente de IA
- Chat interactivo con un coach de fitness impulsado por IA
- Generación de planes de entrenamiento y dieta personalizados en lenguaje natural
- Historial de conversaciones con múltiples sesiones
- Guardado de planes generados directamente en tu perfil

### 📲 Código QR
- Generación de tu código QR personal como miembro
- Identificación rápida en el acceso al centro deportivo

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| **Lenguaje** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material Design 3 |
| **Arquitectura** | MVVM + Clean Architecture |
| **Inyección de dependencias** | Dagger Hilt |
| **Navegación** | Jetpack Compose Navigation |
| **Red** | Retrofit 2 + OkHttp3 |
| **Serialización JSON** | Gson |
| **Almacenamiento local** | Jetpack DataStore (Preferences) |
| **Carga de imágenes** | Coil |
| **Asincronía** | Kotlin Coroutines + StateFlow |
| **Android mínimo** | API 26 (Android 8.0) |
| **Android objetivo** | API 36 (Android 14) |

---

## 🏗️ Arquitectura

Meta Force sigue el patrón **MVVM (Model-View-ViewModel)** con una separación clara de responsabilidades:

```
app/src/main/java/com/meta_force/meta_force/
│
├── data/
│   ├── local/          # Gestión de sesión local (DataStore)
│   ├── model/          # Modelos de datos (Workout, Diet, User, ...)
│   ├── network/        # Interfaces de la API (Retrofit)
│   └── repository/     # Repositorios (contratos + implementaciones)
│
├── di/
│   ├── NetworkModule.kt       # Proveedores Hilt para Retrofit y OkHttp
│   └── RepositoryModule.kt   # Bindings de repositorios
│
└── ui/
    ├── auth/           # Pantallas de login y registro
    ├── dashboard/      # Pantalla principal con acceso a módulos
    ├── workouts/       # Listado y detalle de entrenamientos
    ├── diets/          # Listado y detalle de dietas
    ├── classes/        # Clases grupales y horarios
    ├── centers/        # Centros deportivos y equipamiento
    ├── aichat/         # Asistente de IA con historial
    ├── qr/             # Generador de código QR
    ├── profile/        # Perfil de usuario
    └── theme/          # Colores, tipografía y tema Material3
```

### Flujo de datos

```
UI (Composable) → ViewModel → Repository → API (Retrofit) → Backend
                                         ↗
                             DataStore (sesión local)
```

---

## 📋 Requisitos previos

- **Android Studio** (última versión estable recomendada)
- **JDK 17** o superior
- **Android SDK** con API 26+
- Conexión a internet para consumir la API REST del backend

---

## 🚀 Instalación y puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/Mariogarluu/Meta_Force_kotlin.git
cd Meta_Force_kotlin
```

### 2. Abrir en Android Studio

1. Abre **Android Studio**
2. Selecciona **File → Open** y elige la carpeta del proyecto
3. Espera a que Gradle sincronice las dependencias

### 3. Configurar la URL y credenciales de Supabase

El backend REST API tradicional ha sido migrado a **Supabase**. Las credenciales de acceso se cargan de forma segura desde `local.properties`:

1. Copia el archivo `local.properties.example` y renómbralo a `local.properties`.
2. Introduce las credenciales de tu proyecto Supabase:
   ```properties
   supabase.url=https://YOUR_SUPABASE_PROJECT_REF.supabase.co
   supabase.key=YOUR_SUPABASE_PUBLISHABLE_OR_ANON_KEY
   ```
3. El script de Gradle de la app inyectará dinámicamente estas propiedades como variables de compilación (`BuildConfig.SUPABASE_URL` y `BuildConfig.SUPABASE_ANON_KEY`), las cuales son consumidas por `NetworkModule` para inicializar Retrofit y el cliente de red.

### 4. Ejecutar la aplicación

- Conecta un dispositivo Android (API 26+) o inicia un emulador
- Pulsa **Run → Run 'app'** o usa el atajo `Shift+F10`

---

## 📁 Estructura del proyecto

```
Meta_Force_kotlin/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/meta_force/meta_force/
│   │   │   │   ├── MainActivity.kt              # Punto de entrada, navegación raíz
│   │   │   │   ├── MainViewModel.kt             # Estado de autenticación global
│   │   │   │   ├── MetaForceApplication.kt      # Application con @HiltAndroidApp
│   │   │   │   ├── data/                        # Capa de datos
│   │   │   │   ├── di/                          # Módulos de inyección de dependencias
│   │   │   │   └── ui/                          # Capa de presentación
│   │   │   └── res/                             # Recursos (layouts, strings, drawables)
│   │   └── test/                                # Tests unitarios
│   └── build.gradle.kts                         # Configuración del módulo app
├── build.gradle.kts                             # Configuración raíz
├── settings.gradle.kts                          # Módulos del proyecto
└── gradle/
    └── libs.versions.toml                       # Catálogo de versiones de dependencias
```

---

## 🔌 API REST & Supabase Integration

La aplicación consume la API de **Supabase** (PostgREST para consultas a tablas y Edge Functions serverless para lógica específica).

**Base URL:** `${BuildConfig.SUPABASE_URL}/`

| Módulo | Tipo de Integración | Recurso / RPC / Edge Function |
|--------|---------------------|-------------------------------|
| **Auth** | Supabase Auth (REST) | `/auth/v1/signup`, `/auth/v1/token?grant_type=password`, RPC `get_my_role` |
| **Workouts** | PostgREST (Tablas) | `/rest/v1/Workout`, `/rest/v1/WorkoutExercise`, `/rest/v1/Exercise` |
| **Diets** | PostgREST (Tablas) | `/rest/v1/Diet`, `/rest/v1/DietMeal`, `/rest/v1/Meal` |
| **Classes** | PostgREST (Tablas) | `/rest/v1/GymClass`, `/rest/v1/ClassCenterSchedule`, `/rest/v1/ClassTrainer` |
| **Centers & Machines** | PostgREST (Tablas) | `/rest/v1/Center`, `/rest/v1/Machine` |
| **AI Chat** | Edge Function (Deno) | `/functions/v1/ai-chat` (asistente de IA), RPC `save-ai-plan` |
| **QR Firma Acceso** | Edge Function (Deno) | `/functions/v1/qr-sign` (generación de firma de acceso temporal) |
| **Suscripciones** | PostgREST (Tablas) | `/rest/v1/subscriptions`, `/rest/v1/invoices` |

### Autenticación

Todas las peticiones autenticadas requieren el header:

```
Authorization: Bearer <token>
```

El token se obtiene al iniciar sesión y se renueva automáticamente mediante un interceptor OkHttp.

---

## 🎨 Diseño y tema

Meta Force utiliza una paleta de colores oscura y moderna:

| Color | Hex | Uso |
|-------|-----|-----|
| **Fondo principal** | `#0A192F` | Fondo oscuro de pantallas |
| **Superficie de tarjetas** | `#112240` | Cards y contenedores |
| **Acento primario** | `#64FFDA` | Botones y elementos activos |
| **Texto principal** | `#E6F1FF` | Texto sobre fondos oscuros |
| **Texto secundario** | `#8892B0` | Subtítulos e información secundaria |
| **Error** | `#EF4444` | Estados de error |

---

## 🌐 Idiomas

La aplicación está disponible en:
- 🇪🇸 **Español** (por defecto)
- 🇬🇧 **Inglés** (carpeta `values-en/`)

---

## 🤝 Contribución

¡Las contribuciones son bienvenidas! Sigue estos pasos:

1. **Fork** el repositorio
2. Crea tu rama: `git checkout -b feature/nueva-funcionalidad`
3. Realiza tus cambios siguiendo la arquitectura MVVM del proyecto
4. Haz commit: `git commit -m "feat: añadir nueva funcionalidad"`
5. Haz push: `git push origin feature/nueva-funcionalidad`
6. Abre un **Pull Request** describiendo los cambios

### Convenciones de commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` – nueva funcionalidad
- `fix:` – corrección de errores
- `refactor:` – refactorización de código
- `docs:` – cambios en documentación
- `style:` – cambios de formato (no funcionales)

---

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT**. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

<div align="center">

Hecho con ❤️ y Kotlin por [Mariogarluu](https://github.com/Mariogarluu)

</div>
