package com.homebase.challenge.ui.add

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.material.snackbar.Snackbar
import com.homebase.challenge.R
import com.homebase.challenge.databinding.AddShiftFragmentBinding
import com.homebase.challenge.model.*
import com.homebase.challenge.utils.toStringFormat
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.joda.time.DateTime
import timber.log.Timber

const val SHIFT_REQUEST_KEY = "shift_request_key"

class AddShiftFragment : Fragment(R.layout.add_shift_fragment) {

    private val binding by viewBinding(AddShiftFragmentBinding::bind)
    private val viewModel: AddShiftViewModel by viewModels()
    private lateinit var startDatePicker: SingleDateAndTimePickerDialog.Builder
    private lateinit var endDatePicker: SingleDateAndTimePickerDialog.Builder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        viewModel.viewStateFlow
            .flowWithLifecycle(lifecycle)
            .onEach { render(it) }
            .launchIn(lifecycleScope)
        viewModel.events
            .flowWithLifecycle(lifecycle)
            .onEach { onEvent(it) }
            .launchIn(lifecycleScope)
    }

    private fun onEvent(event: AddShiftViewModel.AddShiftEvent) {
        Timber.i(event.toString())
        when (event) {
            is AddShiftViewModel.AddShiftEvent.UpdateStartDate -> {
                endDatePicker.minDateRange(event.startDate)
                binding.startDate.text = event.startDateString
            }
            is AddShiftViewModel.AddShiftEvent.UpdateEndDate -> {
                startDatePicker.maxDateRange(event.endDate)
                binding.endDate.text = event.endDateString
            }
            is AddShiftViewModel.AddShiftEvent.SaveDate -> {
                setFragmentResult(
                    SHIFT_REQUEST_KEY,
                    bundleOf(
                        "name" to event.shift.name,
                        "start_date" to event.shift.start_date,
                        "end_date" to event.shift.end_date,
                        "color" to event.shift.color,
                        "role" to event.shift.role
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun setUI() {
        startDatePicker = getDatePickerBuilder()
        endDatePicker = getDatePickerBuilder()

        binding.startDateCalendar.setOnClickListener {
            startDatePicker.listener { viewModel.enterStartDate(it) }
            startDatePicker.display()
        }

        binding.endDateCalendar.setOnClickListener {
            endDatePicker.listener { viewModel.enterEndDate(it) }
            endDatePicker.display()
        }

        binding.saveButton.setOnClickListener {
            viewModel.save(
                name = binding.spinnerEmployees.selectedItem.toString(),
                color = binding.spinnerColors.selectedItem.toString(),
                role = binding.spinnerRoles.selectedItem.toString(),
            )
        }
    }

    private fun render(uiModel: AddShiftUiModel) {
        with(uiModel) {
            when (uiStatus) {
                Idle -> Unit
                Loading -> Unit
                Success -> {
                    val c = requireContext()
                    binding.spinnerEmployees.adapter = uiModel.employees.buildAdapter(c)
                    binding.spinnerRoles.adapter = uiModel.roles.buildAdapter(c)
                    binding.spinnerColors.adapter = uiModel.colors.buildAdapter(c)
                    binding.startDate.text = DateTime(uiModel.startDate).toStringFormat()
                    binding.endDate.text = DateTime(uiModel.endDate).toStringFormat()
                    startDatePicker.maxDateRange(uiModel.endDate)
                    endDatePicker.minDateRange(uiModel.startDate)
                }
                is Failure -> {
                    Snackbar.make(
                        requireView(),
                        "Error: ${(uiModel.uiStatus as? Failure)?.error}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun getDatePickerBuilder() = SingleDateAndTimePickerDialog.Builder(requireContext())
        .bottomSheet()
        .title(getString(R.string.select_date))
        .mainColor(ContextCompat.getColor(requireContext(), R.color.brown_500))
        .titleTextColor(ContextCompat.getColor(requireContext(), R.color.brown_500))
        .displayAmPm(true)
        .displayMinutes(false)
        .mustBeOnFuture()
}

private fun <E> MutableList<E>.buildAdapter(c: Context): SpinnerAdapter =
    ArrayAdapter(c, android.R.layout.simple_spinner_item, this)
