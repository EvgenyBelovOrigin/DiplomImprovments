package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.utils.AndroidUtils.calendarToUtcDate
import ru.netology.nework.utils.AndroidUtils.dateUtcToCalendar
import ru.netology.nework.utils.AndroidUtils.dateUtcToStringDate
import ru.netology.nework.viewmodel.NewJobViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class NewJobFragment : Fragment() {
    private val viewModel: NewJobViewModel by viewModels()
    private var calendarStart: Calendar = Calendar.getInstance()
    private var calendarFinish: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.start.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_start_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.show(childFragmentManager, "Date Picker")
            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val date = Date(it)
                calendarStart = dateUtcToCalendar(sdf.format(date))
                binding.start.text = dateUtcToStringDate(calendarToUtcDate(calendarStart))

            }
        }

        binding.finish.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_finish_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.show(childFragmentManager, "Date Picker")
            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val date = Date(it)
                calendarFinish = dateUtcToCalendar(sdf.format(date))
                binding.finish.text = dateUtcToStringDate(calendarToUtcDate(calendarFinish))

            }
        }

        binding.create.setOnClickListener {
            if (binding.name.text.toString().isEmpty()) {
                binding.name.error = getString(R.string.company_name_can_not_be_empty)
                return@setOnClickListener
            }
            if (binding.position.text.toString().isEmpty()) {
                binding.position.error = getString(R.string.position_can_not_be_empty)
                return@setOnClickListener
            }
            binding.progress.isVisible = true
            viewModel.addJob(
                Job(
                    0,
                    binding.name.text.toString(),
                    binding.position.text.toString(),
                    "2024-07-02T21:34:44.562Z",
                    "",
                    binding.link.text.toString(),
                    true,
                    0
                )
            )
        }
        viewModel.addedJob.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            findNavController().navigateUp()
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_loading)
                .setPositiveButton(R.string.ok) {
                        _, _,
                    ->
                    findNavController().navigateUp()
                }
                .show()
        }




        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_close_full_screen_view, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.close) {
                        findNavController().navigateUp()
                        return true
                    } else {
                        return false
                    }
                }
            },
            viewLifecycleOwner,
        )
        return binding.root
    }
}