package com.homebase.challenge.ui.shift

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.homebase.challenge.R
import com.homebase.challenge.model.Shift
import com.homebase.challenge.model.Success
import com.homebase.challenge.utils.RESPONSE_DATE_FORMAT
import com.homebase.challenge.utils.toDateTime
import com.homebase.challenge.utils.toJsonString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ShiftsViewModel(application: Application) : AndroidViewModel(application) {
    private val _viewStateFlow = MutableStateFlow(UiModel.default)
    internal val viewStateFlow get() = _viewStateFlow.asStateFlow()

    init {
        val jsonString =
            application
                .applicationContext
                .resources
                .openRawResource(R.raw.shifts)
                .toJsonString()

        viewModelScope.launch {
            val json = JSONObject(jsonString)
            val shiftsArray = json.getJSONArray("shifts")
            val gson = GsonBuilder().create()
            val shifts = gson.fromJson(shiftsArray.toString(), Array<Shift>::class.java).toMutableList()

            shifts.sortBy { it.start_date.toDateTime(RESPONSE_DATE_FORMAT) }

            _viewStateFlow.value = _viewStateFlow.value.copy(
                uiStatus = Success,
                shifts = shifts,
            )
        }
    }

    fun addShift(shift: Shift) {
        val updatedShifts = _viewStateFlow.value.shifts
        updatedShifts.add(shift)
        updatedShifts.sortBy { it.start_date.toDateTime(RESPONSE_DATE_FORMAT) }
        _viewStateFlow.value = _viewStateFlow.value.copy(
            uiStatus = Success,
            shifts = updatedShifts,
        )
    }
}
