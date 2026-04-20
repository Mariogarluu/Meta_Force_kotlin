# Contexto de Módulo: Cámara y Media (Kotlin)

## Tecnologías Principales
- **CameraX**: Para la captura de fotos y previsualización.
- **MediaStore API**: Para guardar contenido multimedia en la galería del dispositivo.

## Reglas de Implementación
1. **Lifecycle Binding**: Siempre vincular el `ProcessCameraProvider` al ciclo de vida del Fragment o Activity.
2. **Permissions**: Verificar `CAMERA` y `WRITE_EXTERNAL_STORAGE` (o el correspondiente a la versión de API) antes de inicializar.
3. **Optimización**: Usar `Analysis` mode solo cuando sea estrictamente necesario para no penalizar el rendimiento.

## Pendientes (Jira: SCRUM-99)
- Implementar selector de filtros en tiempo real.
- Optimizar el guardado asíncrono para evitar bloqueos en el hilo UI.
