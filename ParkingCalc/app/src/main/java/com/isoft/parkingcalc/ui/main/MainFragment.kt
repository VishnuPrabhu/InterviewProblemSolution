package com.isoft.parkingcalc.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.isoft.parkingcalc.R
import com.isoft.parkingcalc.showAlertDialog
import com.isoft.parkingcalc.ui.enterparking.EnterVehicleDetailsFragment
import com.isoft.parkingcalc.ui.exitparking.ExitAndFareCalculatorFragment
import com.isoft.parkingcalc.ui.occupied.VehicleParkingListFragment
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.Exception

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
        viewModel.vehicleDetails.observe(this, Observer {
            if (!TextUtils.isEmpty(et_enter_vehicle_no.text)) {
                showAlertDialog(it)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_enter_parking.setOnClickListener(this)
        btn_exit_parking.setOnClickListener(this)
        btn_view_parked_slots.setOnClickListener(this)
        btn_find_vehicle_parking.setOnClickListener(this)
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
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, VehicleParkingListFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.btn_find_vehicle_parking -> {
                try {
                    val vehicleNo = et_enter_vehicle_no.text.toString()
                    viewModel.getVehicleParkingNumber(vehicleNo.toInt())
                } catch (e: Exception) {
                    showAlertDialog(e.message!!)
                }
                et_enter_vehicle_no.setText("")
            }
            else -> {
                // Nothing to do
            }
        }
    }
}
