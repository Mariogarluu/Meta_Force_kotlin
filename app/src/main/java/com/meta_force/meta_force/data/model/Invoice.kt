package com.meta_force.meta_force.data.model

/**
 * Modelo de dominio para una factura asociada a una suscripción.
 *
 * @property id Identificador de la factura.
 * @property number Número de factura legible (puede ser nulo si aún no se asignó).
 * @property issueDate Fecha de emisión (YYYY-MM-DD).
 * @property total Importe total de la factura.
 */
data class Invoice(
    val id: String,
    val number: String?,
    val issueDate: String,
    val total: Double
)

