// In SystemStatus.kt (or wherever you define your data classes)
package com.labactivity.safepark_iot

// Matches the root level snapshot
data class SystemStatus(
    val command: CommandDetails? = CommandDetails(),
    val status: StatusDetails? = StatusDetails()
)

// Matches the 'command' node
data class CommandDetails(
    val arm: Boolean = false,
    val closeGate: Boolean = false,
    val openGate: Boolean = false,
    val streetlight: Int = 0 // 0=Auto, 1=ON, 2=OFF
)

// Matches the 'status' node
data class StatusDetails(
    val armed: Boolean = false,
    val buzzer: Boolean = false,
    val gateMoving: Boolean = false,
    val gateOpen: Boolean = false,
    val ir: Boolean = false,
    val streetlight: Boolean = false // Actual status of the street light
)