package com.meta_force.meta_force.data.model

/**
 * Modelo de dominio para representar una suscripción del cliente junto con
 * la información principal de facturación.
 *
 * @property id Identificador de la suscripción.
 * @property planName Nombre legible del plan (p. ej. "Standard").
 * @property planCode Código interno del plan (p. ej. "standard").
 * @property durationLabel Etiqueta de duración (p. ej. "6 meses").
 * @property startDate Fecha de inicio (YYYY-MM-DD).
 * @property endDate Fecha de fin (YYYY-MM-DD).
 * @property status Estado de la suscripción (active/expired/cancelled).
 * @property total Importe total facturado.
 * @property invoice Factura asociada principal.
 */
data class Subscription(
    val id: String,
    val planName: String,
    val planCode: String,
    val durationLabel: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val total: Double,
    val invoice: Invoice
)

