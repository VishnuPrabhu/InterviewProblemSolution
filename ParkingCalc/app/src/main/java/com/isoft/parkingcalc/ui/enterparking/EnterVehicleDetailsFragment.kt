package com.isoft.parkingcalc.ui.enterparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.isoft.parkingcalc.R
import com.isoft.parkingcalc.showAlertDialog
import kotlinx.android.synthetic.main.fragment_enter_vehicle_details.*

class EnterVehicleDetailsFragment: Fragment() {

    companion object {
        fun newInstance() = EnterVehicleDetailsFragment()
    }

    lateinit var viewModel: EnterVehicleDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_vehicle_details, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EnterVehicleDetailsViewModel::class.java)
        // observer which updates the UI.
        viewModel.newVehicleForParking.observe(this, Observer {
            // Show dialog and go to previous page
            print(it)
            fragmentManager?.popBackStack()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = ArrayAdapter(this.activity, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.vehicle_types))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_vehicle_type.adapter = adapter

        btn_get_available_parking_slot.setOnClickListener {
            try {
                viewModel.addVehicle(et_vehicle_reg_no.text.toString().toInt(), spinner_vehicle_type.selectedItemPosition)
            } catch (e: Exception) {
                showAlertDialog(e.message!!)
            }
        }
    }
}