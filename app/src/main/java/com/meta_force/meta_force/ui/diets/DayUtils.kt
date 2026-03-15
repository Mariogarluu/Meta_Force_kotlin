package com.meta_force.meta_force.ui.diets

/**
 * Utilidades para trabajar con días de la semana en el contexto de dietas y entrenamientos.
 * Los días se representan como enteros: 0 = Lunes, 1 = Martes, ..., 6 = Domingo
 */
object DayUtils {

    /**
     * Obtiene el nombre completo de un día de la semana.
     * @param dayOfWeek Día de la semana (0-6)
     * @return Nombre del día
     */
    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            0 -> "Lunes"
            1 -> "Martes"
            2 -> "Miércoles"
            3 -> "Jueves"
            4 -> "Viernes"
            5 -> "Sábado"
            6 -> "Domingo"
            else -> "Día $dayOfWeek"
        }
    }

    /**
     * Obtiene el nombre abreviado de un día de la semana.
     * @param dayOfWeek Día de la semana (0-6)
     * @return Nombre abreviado del día
     */
    fun getDayNameShort(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            0 -> "Lun"
            1 -> "Mar"
            2 -> "Mié"
            3 -> "Jue"
            4 -> "Vie"
            5 -> "Sáb"
            6 -> "Dom"
            else -> dayOfWeek.toString()
        }
    }

    /**
     * Obtiene el siguiente día en secuencia, con bucle de domingo a lunes.
     * @param currentDay Día actual (0-6)
     * @return Siguiente día (0-6)
     */
    fun getNextDay(currentDay: Int): Int {
        return if (currentDay == 6) 0 else currentDay + 1
    }

    /**
     * Obtiene el día anterior en secuencia, con bucle de lunes a domingo.
     * @param currentDay Día actual (0-6)
     * @return Día anterior (0-6)
     */
    fun getPreviousDay(currentDay: Int): Int {
        return if (currentDay == 0) 6 else currentDay - 1
    }
}