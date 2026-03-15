package com.meta_force.meta_force.ui.diets

/**
 * Utilidades para trabajar con días de la semana en el contexto de dietas y entrenamientos.
 * Los días se representan como enteros: 0 = Domingo, 1 = Lunes, ..., 6 = Sábado
 */
object DayUtils {

    /**
     * Obtiene el nombre completo de un día de la semana.
     * @param dayOfWeek Día de la semana (0-6)
     * @return Nombre del día
     */
    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            0 -> "Domingo"
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
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
            0 -> "Dom"
            1 -> "Lun"
            2 -> "Mar"
            3 -> "Mié"
            4 -> "Jue"
            5 -> "Vie"
            6 -> "Sáb"
            else -> dayOfWeek.toString()
        }
    }

    /**
     * Obtiene el siguiente día en secuencia, con bucle de sábado a domingo.
     * @param currentDay Día actual (0-6)
     * @return Siguiente día (0-6)
     */
    fun getNextDay(currentDay: Int): Int {
        return if (currentDay == 6) 0 else currentDay + 1
    }

    /**
     * Obtiene el día anterior en secuencia, con bucle de domingo a sábado.
     * @param currentDay Día actual (0-6)
     * @return Día anterior (0-6)
     */
    fun getPreviousDay(currentDay: Int): Int {
        return if (currentDay == 0) 6 else currentDay - 1
    }
}