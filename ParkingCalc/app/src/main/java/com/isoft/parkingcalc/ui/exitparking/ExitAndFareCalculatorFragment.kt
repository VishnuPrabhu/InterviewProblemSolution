package com.isoft.parkingcalc.ui.exitparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.isoft.parkingcalc.R
import com.isoft.parkingcalc.extenstions.showAlertDialog
import kotlinx.android.synthetic.main.fragment_exit_parking_fare_calculator.*

class ExitAndFareCalculatorFragment: Fragment() {

    companion object {
        fun  newInstance() = ExitAndFareCalculatorFragment()
    }

    private lateinit var viewModel: ExitAndFareCalculatorViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ExitAndFareCalculatorViewModel::class.java)
        viewModel.departingVehicle.observe(this, Observer {
            // show dialog and move to previous page
            print(it)
            showAlertDialog("Your parking fare is $it")
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exit_parking_fare_calculator, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_calculate_parking_fare.setOnClickListener {
            view ->
            try {
                val vehicleNo = et_vehicle_reg_no.text.toString().toInt()
                val fare = viewModel.calculateFareForParking(vehicleNo)
            } catch (e: Exception) {
                showAlertDialog(e.message!!)
            }
            et_vehicle_reg_no.setText("")
        }
    }

}