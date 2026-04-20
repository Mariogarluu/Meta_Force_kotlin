# Contexto de Módulo: Persistencia Offline (Room)

## Tecnologías Principales
- **Room Persistence Library**: Abstracción sobre SQLite.
- **Coroutines/Flow**: Para la observación reactiva de la base de datos.

## Estructura de Datos
- **Entities**: Representación fiel de las tablas de Supabase para permitir la sincronización bidireccional.
- **DAOs**: Deben retornar `Flow<T>` para actualizaciones automáticas en la UI.

## Reglas de Implementación (SCRUM-106)
1. **Migrations**: Siempre definir una estrategia de migración clara. Usar `.fallbackToDestructiveMigration()` solo en entornos de desarrollo muy temprano.
2. **Type Converters**: Centralizar conversores para fechas (Long a Date) y objetos complejos (JSON string).
3. **Threading**: Nunca ejecutar consultas pesadas en el Main Thread. Room ya impone esto, pero usar `Dispatcher.IO` para mayor claridad.
