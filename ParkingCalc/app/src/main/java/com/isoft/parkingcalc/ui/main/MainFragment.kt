package com.isoft.parkingcalc.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.isoft.parkingcalc.R
import com.isoft.parkingcalc.ui.enterparking.EnterVehicleDetailsFragment
import com.isoft.parkingcalc.ui.exitparking.ExitAndFareCalculatorFragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_enter_parking.setOnClickListener(this)
        btn_exit_parking.setOnClickListener(this)
        btn_parking_statistics.setOnClickListener(this)
        btn_parking_statistics.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_enter_parking -> {
                // Open Entering screen
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, EnterVehicleDetailsFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.btn_exit_parking -> {
                // Open Exiting screen
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, ExitAndFareCalculatorFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.btn_view_parked_slots -> {
                // Open Parking Slots List screen
            }
            R.id.btn_parking_statistics -> {
                // Open Statistics Chart screen
            }
            else -> {
                // Nothing to do
            }
        }
    }
}
