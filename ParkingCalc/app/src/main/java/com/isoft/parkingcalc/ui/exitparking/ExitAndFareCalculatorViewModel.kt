package com.isoft.parkingcalc.ui.exitparking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isoft.parkingcalc.ParkingAlgorithm
import com.isoft.parkingcalc.VehicleNotAvailableException
import com.isoft.parkingcalc.models.Vehicle

class ExitAndFareCalculatorViewModel: ViewModel() {

    // Create a LiveData with a String
    val departingVehicle: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    @Throws(VehicleNotAvailableException::class)
    public fun calculateFareForParking(vehicleNumber: Int): Double {
        val parkingAlgorithm = ParkingAlgorithm.instance()
        val fare = parkingAlgorithm.departVehicle(vehicleNumber)
        departingVehicle.value = fare
        // Remove From Database ***
        return fare
    }
}