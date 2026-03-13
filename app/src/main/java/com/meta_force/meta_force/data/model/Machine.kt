package com.meta_force.meta_force.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the broad category of a gym machine.
 */
enum class MachineType {
    @SerializedName("cardio") CARDIO,
    @SerializedName("fuerza") FUERZA,
    @SerializedName("peso libre") PESO_LIBRE,
    @SerializedName("funcional") FUNCIONAL,
    @SerializedName("otro") OTRO
}

/**
 * Represents the current operational status of a specific machine instance.
 *
 * @property displayName Human-readable name for the status.
 */
enum class MachineStatus(val displayName: String) {
    @SerializedName("operativa") OPERATIVA("Operativa"),
    @SerializedName("en mantenimiento") MANTENIMIENTO("En mantenimiento"),
    @SerializedName("fuera de servicio") FUERA_SERVICIO("Fuera de servicio")
}

/**
 * Represents a specific instance of a machine located at a fitness center.
 *
 * @property id Unique identifier for the machine instance.
 * @property machineTypeId ID of the [MachineTypeModel] this instance belongs to.
 * @property instanceNumber A unique number or label for the machine within the center.
 * @property centerId ID of the center where the machine is located.
 * @property status Current [MachineStatus] of the machine.
 * @property machineType Optional detailed information about the machine type.
 * @property center Optional detailed information about the center.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last update timestamp.
 */
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

/**
 * Represents a category or model of machine (e.g., "Treadmill X1").
 *
 * @property id Unique identifier for the machine type.
 * @property name Name of the machine type.
 * @property type The broad [MachineType] it falls under.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last update timestamp.
 * @property instances Optional list of physical [MachineCenterInstance]s of this type.
 */
data class MachineTypeModel(
    val id: String,
    val name: String,
    val type: MachineType,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    @SerializedName("machines", alternate = ["instances"])
    val instances: List<MachineCenterInstance>? = null
)
