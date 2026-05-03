# 📖 Documentación del Proyecto: Meta-Force Android (Kotlin)

Aquí documentamos el desarrollo de la App nativa para que el entrenamiento no pare nunca.

## 📱 Core de la App
*   **`MainActivity.kt`**: Aquí hemos configurado la base de la interfaz móvil. Hemos implementado el ciclo de vida de Android para que la App sea rápida y eficiente al abrirse.
*   **`build.gradle.kts`**: Aquí hemos gestionado todas las dependencias modernas de Kotlin y Compose. Hemos asegurado que la App use las últimas versiones de seguridad para proteger los datos del usuario.

## 🧠 Lógica y Modelos
*   **`data/models/`**: Aquí hemos replicado los modelos de datos de la web. Hemos usado clases de datos de Kotlin para que la sincronización con el Backend sea perfecta y no haya errores de formato.
*   **`ui/theme/`**: Aquí hemos adaptado el diseño visual al móvil. Hemos usado Material Design 3 con retoques personalizados para mantener la estética premium que tenemos en la web.

## 🔗 Conectividad
*   **`network/ApiService.kt`**: Aquí hemos programado todas las llamadas a la API del backend. Hemos usado Retrofit para que las peticiones sean rápidas y seguras, manejando los estados de carga con elegancia.
