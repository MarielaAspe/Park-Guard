
package com.labactivity.safepark_iot

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SystemStatus(
    // The keys must exactly match the Firebase keys
    var buzzerIsOn: Boolean = false,
    var gateIsOpen: Boolean = false,
    var isArmed: Boolean = false,
    var street_light: Boolean = false
) {
    // You'll need a no-argument constructor for Firebase to automatically
    // deserialize the data, even if it's not explicitly used in Kotlin.
    constructor() : this(false, false, false, false)
}