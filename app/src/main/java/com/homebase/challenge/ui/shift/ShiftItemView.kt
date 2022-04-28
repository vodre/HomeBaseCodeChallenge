package com.homebase.challenge.ui.shift

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.homebase.challenge.R
import com.homebase.challenge.databinding.ViewEmployeeItemBinding
import com.homebase.challenge.model.Shift
import com.homebase.challenge.utils.RESPONSE_DATE_FORMAT
import com.homebase.challenge.utils.layoutInflater
import com.homebase.challenge.utils.toColorResource
import com.homebase.challenge.utils.toDateTime

class ShiftItemView(
    context: Context,
    attributeSet: AttributeSet? = null,
) : ConstraintLayout(
    ContextThemeWrapper(context, R.style.Theme_CodeChallenge),
    attributeSet,
) {
    private val binding = ViewEmployeeItemBinding.inflate(layoutInflater, this, true)

    fun render(shift: Shift) {
        val startDate = shift.start_date.toDateTime(RESPONSE_DATE_FORMAT)
        val endDate = shift.end_date.toDateTime(RESPONSE_DATE_FORMAT)
        val day = startDate.dayOfWeek().asShortText
        val month = startDate.monthOfYear().asShortText
        val startHour = startDate.hourOfDay().get()
        val endHour = endDate.hourOfDay().get()
        val dayTime = if (endHour < 12) "AM" else "PM"
        val label = "${shift.name} (${shift.role}) $day ${startDate.dayOfMonth}/$month"
        val color = shift.color.toColorResource()
        val hourLabel = "$startHour - $endHour $dayTime"

        binding.label.text = label
        binding.time.text = hourLabel
        binding.indicator.setColorFilter(ContextCompat.getColor(context, color))
    }
}
