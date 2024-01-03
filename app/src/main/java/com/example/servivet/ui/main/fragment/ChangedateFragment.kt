package com.example.servivet.ui.main.fragment

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChangedateBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.CalenderContainerAdapter
import com.example.servivet.ui.main.view_model.ChangeDateViewModel
import java.util.Calendar

class ChangedateFragment:BaseFragment<FragmentChangedateBinding,ChangeDateViewModel>(R.layout.fragment_changedate) {
    override val binding: FragmentChangedateBinding by viewBinding(FragmentChangedateBinding::bind)
    override val mViewModel: ChangeDateViewModel by viewModels()

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
        binding.idCalenderContainerRecycle.adapter = CalenderContainerAdapter(requireContext(),weekDaysList,ArrayList())
       // mViewModel.ClickAction(binding,requireContext()).calenderset()
    }

    private fun setCalendarDays() {
        var currentYear = Calendar.getInstance()[Calendar.YEAR];
        var currentMonth = Calendar.getInstance()[Calendar.MONTH];
        var currentDate = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        var checkCurrentMonth = Calendar.getInstance()[Calendar.MONTH]

     //   val calendarDays = generateCalendarDays(currentYear, currentMonth)

    }

  /*  private fun generateCalendarDays(currentYear: Int, currentMonth: Int): Any {

    }
*/
    override fun setupObservers() {
    }

}