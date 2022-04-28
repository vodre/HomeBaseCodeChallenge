package com.homebase.challenge.ui.add

import com.homebase.challenge.model.Idle
import com.homebase.challenge.model.UiStatus
import org.joda.time.DateTime
import java.util.*

internal data class AddShiftUiModel(
    val uiStatus: UiStatus,
    val employees: MutableList<String> = mutableListOf(),
    val roles: MutableList<String> = mutableListOf(),
    val colors: MutableList<String> = mutableListOf(),
    val startDate: Date = Calendar.getInstance().time,
    val endDate: Date = DateTime(Calendar.getInstance().time).plusHours(1).toDate(),
) {
    companion object {
        val default
            get() = AddShiftUiModel(
                uiStatus = Idle,
            )
    }
}
