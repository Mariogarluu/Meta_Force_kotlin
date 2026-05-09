package com.meta_force.meta_force.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Respuesta de la Edge Function `qr-sign`.
 *
 * @property token JWT firmado HS256 que representa el acceso del usuario.
 * @property userId Identificador del usuario propietario del QR.
 * @property planCode Código interno del plan de suscripción (p.ej. "standard").
 * @property endDate Fecha de fin de la suscripción en formato YYYY-MM-DD.
 * @property role Rol actual del usuario (CLIENT, SUPERADMIN, etc.).
 * @property exp Marca de tiempo UNIX (segundos) de expiración del JWT.
 * @property planName Nombre legible del plan.
 * @property durationLabel Etiqueta descriptiva de la duración (p.ej. "6 meses").
 * @property durationMonths Número de meses de la duración.
 */
@Serializable
data class SignedQr(
    val token: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("plan_code")
    val planCode: String,
    @SerialName("end_date")
    val endDate: String,
    val role: String,
    val exp: Long,
    @SerialName("plan_name")
    val planName: String? = null,
    @SerialName("duration_label")
    val durationLabel: String? = null,
    @SerialName("duration_months")
    val durationMonths: Int? = null
)

