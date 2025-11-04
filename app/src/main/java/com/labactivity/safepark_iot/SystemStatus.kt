package com.labactivity.safepark_iot
data class SystemStatus(
    val armed: Boolean = false,
    val motion: Boolean = false,
    val gate: Boolean = false,
    val online: Boolean = false,
    val uptime: Int = 0
)