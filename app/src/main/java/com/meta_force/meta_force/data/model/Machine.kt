package com.meta_force.meta_force.data.model

import com.google.gson.annotations.SerializedName

enum class MachineType {
    @SerializedName("cardio") CARDIO,
    @SerializedName("fuerza") FUERZA,
    @SerializedName("peso libre") PESO_LIBRE,
    @SerializedName("funcional") FUNCIONAL,
    @SerializedName("otro") OTRO
}

enum class MachineStatus(val displayName: String) {
    @SerializedName("operativa") OPERATIVA("Operativa"),
    @SerializedName("en mantenimiento") MANTENIMIENTO("En mantenimiento"),
    @SerializedName("fuera de servicio") FUERA_SERVICIO("Fuera de servicio")
}

data class MachineCenterInstance(
    val id: String,
    val machineTypeId: String,
    val instanceNumber: Int,
    val centerId: String,
    val status: MachineStatus,
    val machineType: MachineTypeModel? = null,
    val center: Center? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class MachineTypeModel(
    val id: String,
    val name: String,
    val type: MachineType,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    @SerializedName("machines", alternate = ["instances"])
    val instances: List<MachineCenterInstance>? = null
)
