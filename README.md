# 💪 MetaForce

**MetaForce** es una aplicación Android de gestión fitness desarrollada en **Kotlin** con **Jetpack Compose**. Permite a los usuarios gestionar sus rutinas de entrenamiento, dietas, clases del gimnasio y perfil personal, todo conectado a un backend REST.

---

## 📸 Características

- 🔐 **Autenticación** — Registro e inicio de sesión con JWT
- 🏠 **Dashboard** — Panel de acceso rápido a todas las secciones
- 🏋️ **Rutinas** — Visualiza y gestiona tus entrenamientos y ejercicios
- 🥗 **Dietas** — Consulta y sigue tus planes nutricionales
- 📅 **Clases** — Consulta horarios y reserva clases del gimnasio
- 👤 **Perfil** — Edita tu información personal y foto de perfil

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| [Kotlin](https://kotlinlang.org/) | Lenguaje principal |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | UI declarativa |
| [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Navegación entre pantallas |
| [Hilt (Dagger)](https://dagger.dev/hilt/) | Inyección de dependencias |
| [Retrofit 2](https://square.github.io/retrofit/) | Cliente HTTP / API REST |
| [OkHttp](https://square.github.io/okhttp/) | Interceptores HTTP |
| [Gson](https://github.com/google/gson) | Serialización JSON |
| [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Persistencia de sesión |
| [Coil](https://coil-kt.github.io/coil/) | Carga de imágenes |
| [Material 3](https://m3.material.io/) | Sistema de diseño |

---

## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)** con una arquitectura por capas:

```
app/
└── src/main/java/com/meta_force/meta_force/
    ├── data/
    │   ├── local/          # SessionManager (DataStore)
    │   ├── model/          # Data classes (Workout, Diet, GymClass, User…)
    │   ├── network/        # Interfaces Retrofit (AuthApi, WorkoutApi, DietApi, ClassApi)
    │   └── repository/     # Implementaciones de repositorios
    ├── di/                 # Módulos Hilt (NetworkModule, RepositoryModule)
    ├── ui/
    │   ├── auth/           # Login y Register
    │   ├── dashboard/      # Pantalla principal
    │   ├── workouts/       # Lista y detalle de rutinas
    │   ├── diets/          # Lista y detalle de dietas
    │   ├── classes/        # Clases del gimnasio
    │   ├── profile/        # Perfil de usuario
    │   └── theme/          # Colores, tipografía y tema (dark/teal)
    ├── MainActivity.kt     # NavHost y navegación principal
    ├── MainViewModel.kt    # Estado de inicio de sesión
    └── MetaForceApplication.kt
```

---

## 🚀 Primeros pasos

### Requisitos previos

- **Android Studio** Hedgehog (2023.1) o superior
- **JDK 17**
- **Android SDK** API 26+ (mínimo), API 36 (target)
- Backend de MetaForce ejecutándose localmente en el puerto `3000`

### Configuración del backend

La app apunta por defecto a `http://10.0.2.2:3000/api/` (loopback del emulador Android hacia `localhost` del equipo host). Para ejecutar sobre un dispositivo físico, cambia la `baseUrl` en `NetworkModule.kt`:

```kotlin
// di/NetworkModule.kt
.baseUrl("http://<TU_IP_LOCAL>:3000/api/")
```

### Clonar y ejecutar

```bash
git clone https://github.com/Mariogarluu/Meta_Force_kotlin.git
cd Meta_Force_kotlin
```

1. Abre el proyecto en **Android Studio**.
2. Deja que Gradle sincronice las dependencias.
3. Arranca el backend en tu equipo.
4. Ejecuta la app en un emulador o dispositivo físico (**Run › Run 'app'**).

---

## 📡 API REST

La app consume una API REST. Los endpoints principales son:

| Endpoint | Método | Descripción |
|---|---|---|
| `auth/login` | POST | Iniciar sesión |
| `auth/register` | POST | Registrar usuario |
| `users/me` | GET | Obtener perfil |
| `users/me` | PUT | Actualizar perfil |
| `users/me/profile-image` | POST | Subir foto de perfil |
| `workouts` | GET | Listar rutinas |
| `workouts/{id}` | GET | Detalle de rutina |
| `workouts` | POST | Crear rutina |
| `workouts/{id}` | DELETE | Eliminar rutina |
| `diets` | GET / POST | Listar / crear dietas |
| `diets/{id}` | GET | Detalle de dieta |
| `classes` | GET | Listar clases del gimnasio |

---

## 🎨 Tema

La app usa un tema oscuro personalizado basado en **Material 3**:

| Token | Color | Uso |
|---|---|---|
| `MF_BlueDeep` | `#0A192F` | Fondo principal |
| `MF_BlueLight` | `#112240` | Tarjetas / superficies |
| `MF_Teal` | `#64FFDA` | Acción primaria / resaltado |
| `MF_White` | `#E6F1FF` | Texto principal |
| `MF_Slate` | `#8892B0` | Texto secundario |
| `MF_Red` | `#EF4444` | Error |

---

## 🧪 Tests

El proyecto incluye tests unitarios e instrumentados básicos:

```bash
# Tests unitarios
./gradlew test

# Tests instrumentados (requiere emulador o dispositivo)
./gradlew connectedAndroidTest
```

---

## 📋 Requisitos del sistema

| Parámetro | Valor |
|---|---|
| `minSdk` | 26 (Android 8.0) |
| `targetSdk` | 36 |
| `compileSdk` | 36 |
| `kotlinJvmTarget` | 17 |

---

## 📄 Licencia

Este proyecto es de uso académico/personal. Consulta con el autor antes de reutilizar el código.
