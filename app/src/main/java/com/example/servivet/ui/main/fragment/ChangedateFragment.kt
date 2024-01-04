package com.example.servivet.ui.main.fragment

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChangedateBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.CalenderContainerAdapter
import com.example.servivet.ui.main.view_model.ChangeDateViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChangedateFragment:BaseFragment<FragmentChangedateBinding,ChangeDateViewModel>(R.layout.fragment_changedate) {
    override val binding: FragmentChangedateBinding by viewBinding(FragmentChangedateBinding::bind)
    override val mViewModel: ChangeDateViewModel by viewModels()
    private lateinit var datesList:ArrayList<String>

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(binding,requireContext())

        }
        setcal()

    }
    fun setcal(){
       val weekDaysList = listOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        )
        setCalendarDays()
        binding.idCalenderContainerRecycle.adapter = CalenderContainerAdapter(requireContext(),weekDaysList,datesList,ArrayList())
       // mViewModel.ClickAction(binding,requireContext()).calenderset()
    }

    private fun setCalendarDays() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        datesList = generateDatesForMonth(year, month)

    }

    private fun generateDatesForMonth(year: Int, month: Int): ArrayList<String> {
        val datesList = ArrayList<String>()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        while (calendar.get(Calendar.MONTH) == month) {
            datesList.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return datesList
    }    override fun setupObservers() {
    }

}