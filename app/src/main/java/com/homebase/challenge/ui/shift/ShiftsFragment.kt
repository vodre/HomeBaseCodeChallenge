package com.homebase.challenge.ui.shift

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.homebase.challenge.R
import com.homebase.challenge.databinding.ShiftsFragmentBinding
import com.homebase.challenge.model.* // ktlint-disable no-wildcard-imports
import com.homebase.challenge.ui.add.SHIFT_REQUEST_KEY
import com.homebase.challenge.utils.SimpleDividerItemDecoration
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ShiftsFragment : Fragment(R.layout.shifts_fragment) {
    private val binding by viewBinding(ShiftsFragmentBinding::bind)
    private val viewModel: ShiftsViewModel by viewModels()
    private lateinit var recycler: Recycler<Shift>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecycler()
        viewModel.viewStateFlow
            .flowWithLifecycle(lifecycle)
            .onEach { render(it) }
            .launchIn(lifecycleScope)

        binding.addBtn.setOnClickListener {
            setFragmentResultListener(SHIFT_REQUEST_KEY) { _, bundle ->
                viewModel.addShift(
                    Shift(
                        name = bundle.getString("name", ""),
                        role = bundle.getString("role", ""),
                        color = bundle.getString("color", "gray"),
                        end_date = bundle.getString("end_date", ""),
                        start_date = bundle.getString("start_date", ""),
                    )
                )
            }
            findNavController().navigate(R.id.addShiftFragment)
        }
    }

    private fun render(uiModel: UiModel) {
        with(uiModel) {
            when (uiStatus) {
                Idle -> Timber.d("Idle")
                Loading -> Unit
                Success -> {
                    recycler.update {
                        data = uiModel.shifts.toDataSource()
                    }
                }
                is Failure ->
                    Snackbar.make(requireView(), "Error", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun configureRecycler() {
        recycler = Recycler.adopt(binding.recycler) {
            row<Shift, ShiftItemView> {
                create { context ->
                    view = ShiftItemView(context)
                    bind { shift ->
                        view.render(shift)
                    }
                }
            }
        }

        recycler.view.addItemDecoration(
            SimpleDividerItemDecoration(requireContext(), R.drawable.line_divider)
        )
    }
}
