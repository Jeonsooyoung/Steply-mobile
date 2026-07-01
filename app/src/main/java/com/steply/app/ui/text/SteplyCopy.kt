package com.steply.app.ui.text

object SteplyCopy {
    const val MedicalDisclaimer = "Steply is not a medical diagnosis tool."
    const val StopIfUncomfortable = "Stop if you feel pain, dizziness, or discomfort."
    const val UseSupport = "Use support or ask a caregiver to help if needed."
    const val MoveSlowly = "Move slowly and safely."
    const val GenericError = "Something went wrong. Please try again."
    const val ChooseProfileToBegin = "Please choose a profile to begin."
    const val ChooseProfileAgain = "Please choose a profile again."
    const val ChooseProfileToExport = "Please choose a profile to export."
    const val LocalDataNotice = "No signup needed. Profiles and movement records stay on this device."
    const val ProfilesAndRecordsStayLocal = "Profiles and movement records stay on this device."
    const val ReferenceOnlyDisclaimer = "Records are for reference only and are not a medical diagnosis."

    val MedicalDisclaimerWithStop = "$MedicalDisclaimer $StopIfUncomfortable"
    val SafetyReminder = "$StopIfUncomfortable\n$UseSupport"
    val SafetyReminderInline = "$StopIfUncomfortable $UseSupport"
}
