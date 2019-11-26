package com.isoft.parkingcalc.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isoft.parkingcalc.ParkingApplication
import com.isoft.parkingcalc.VehicleNotAvailableException
import com.isoft.parkingcalc.db.Vehicle
import com.isoft.parkingcalc.models.VehicleType
import com.isoft.parkingcalc.ui.occupied.Section

class MainViewModel : ViewModel() {

    // Create a LiveData for VehicleNumber
    val vehicleDetails: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getVehicleParkingNumber(vehicleNumber: Int) {
        val vehicle = ParkingApplication.db.vehicleDao().getParkedVehicleWithNumber(vehicleNumber)

        if (vehicle != null) {
            vehicleDetails.value = "Your vehicle is Parked at ${vehicle?.parkingType} with parking space no: ${vehicle?.assignedParkingSpaceNumber}"
        }
        else {
            throw VehicleNotAvailableException()
        }
    }


}
