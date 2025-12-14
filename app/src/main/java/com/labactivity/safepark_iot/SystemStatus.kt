package com.labactivity.safepark_iot

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class SystemStatus(
    var isArmed: Boolean = false,
    var gateIsOpen: Boolean = false,
    var buzzerIsOn: Boolean = false,

    @get:PropertyName("street_light")
    @set:PropertyName("street_light")
    var streetLight: Boolean = false
)