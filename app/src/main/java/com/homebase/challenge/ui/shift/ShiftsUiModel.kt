package com.homebase.challenge.ui.shift

import com.homebase.challenge.model.Idle
import com.homebase.challenge.model.Shift
import com.homebase.challenge.model.UiStatus

internal data class UiModel(
    val uiStatus: UiStatus,
    val shifts: MutableList<Shift> = mutableListOf(),
) {
    companion object {
        val default
            get() = UiModel(
                uiStatus = Idle,
            )
    }
}
