package com.homebase.challenge.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.homebase.challenge.R
import com.homebase.challenge.model.Idle
import com.homebase.challenge.model.Shift
import com.homebase.challenge.model.Success
import com.homebase.challenge.utils.RESPONSE_DATE_FORMAT
import com.homebase.challenge.utils.toFormattedString
import com.homebase.challenge.utils.toJsonString
import com.homebase.challenge.utils.toStringFormat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashSet

class AddShiftViewModel(application: Application) : AndroidViewModel(application) {
    private val _viewStateFlow = MutableStateFlow(AddShiftUiModel.default)
    internal val viewStateFlow get() = _viewStateFlow.asStateFlow()
    private var _events = MutableSharedFlow<AddShiftEvent>()
    val events = _events.asSharedFlow()

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
            val shifts =
                gson.fromJson(shiftsArray.toString(), Array<Shift>::class.java).toMutableList()
            val employees = HashSet<String>()
            val colors = HashSet<String>()
            val roles = HashSet<String>()

            shifts.forEach {
                employees.add(it.name)
                roles.add(it.role)
                colors.add(it.color.replaceFirstChar(Char::titlecase))
            }

            _viewStateFlow.value = _viewStateFlow.value.copy(
                uiStatus = Success,
                employees = employees.toMutableList(),
                roles = roles.toMutableList(),
                colors = colors.toMutableList(),
            )
        }
    }

    fun enterStartDate(date: Date) {
        viewModelScope.launch {
            _events.emit(AddShiftEvent.UpdateStartDate(DateTime(date).toStringFormat(), date))
        }
        _viewStateFlow.value = _viewStateFlow.value.copy(
            uiStatus = Idle,
            startDate = date
        )
    }

    fun enterEndDate(date: Date) {
        viewModelScope.launch {
            _events.emit(AddShiftEvent.UpdateEndDate(DateTime(date).toStringFormat(), date))
        }
        _viewStateFlow.value = _viewStateFlow.value.copy(
            uiStatus = Idle,
            endDate = date
        )
    }

    fun save(
        name: String,
        role: String,
        color: String,
    ) {
        val format = RESPONSE_DATE_FORMAT
        val startDate = _viewStateFlow.value.startDate.toFormattedString(format)
        val endDate = _viewStateFlow.value.endDate.toFormattedString(format)
        val shift = Shift(
            name = name,
            role = role,
            color = color,
            start_date = startDate,
            end_date = endDate,
        )
        viewModelScope.launch {
            _events.emit(AddShiftEvent.SaveDate(shift))
        }
    }

    sealed interface AddShiftEvent {
        data class UpdateStartDate(val startDateString: String, val startDate: Date) : AddShiftEvent
        data class UpdateEndDate(val endDateString: String, val endDate: Date) : AddShiftEvent
        data class SaveDate(val shift: Shift) : AddShiftEvent
    }
}
