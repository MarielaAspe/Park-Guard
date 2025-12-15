package com.labactivity.safepark_iot

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SystemStatus(
    val command: CommandDetails? = CommandDetails(),
    val status: StatusDetails? = StatusDetails()
)

@IgnoreExtraProperties
data class CommandDetails(
    val arm: Boolean = false,
    val alarm: Boolean = false,
    val closeGate: Boolean = false,
    val openGate: Boolean = false,
    val streetlight: Int = 0
)

@IgnoreExtraProperties
data class StatusDetails(
    val armed: Boolean = false,
    val buzzer: Boolean = false,
    val gateMoving: Boolean = false,
    val gateOpen: Boolean = false,
    val ir: Boolean = false,
    val streetlight: Boolean = false
)