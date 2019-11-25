package com.isoft.parkingcalc.ui.enterparking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isoft.parkingcalc.models.Vehicle
import com.isoft.parkingcalc.models.VehicleType
import com.isoft.parkingcalc.ParkingAlgorithm
import java.util.*

class EnterVehicleDetailsViewModel : ViewModel() {


    // Create a LiveData with a String
    val newVehicleForParking: MutableLiveData<Vehicle> by lazy {
        MutableLiveData<Vehicle>()
    }


    // IMP NOTE : We are not check for duplicate vehicle number here, as this is not in the requirements.
    // So please dont add duplicate vehicle numbers
    public fun addVehicle(vehicleNo: Int, vehicleType: Int)
    {
        try {
            val parkingAlgorithm = ParkingAlgorithm.instance()
            val vehicle = Vehicle()
            vehicle.vehicleNumber = vehicleNo
            vehicle.enterDate = Date()
            // vehicle.exitDate = // should be added during the exit parking
            vehicle.assignedParkingSpaceNumber = 0
            vehicle.allotedSpaceForParking = ParkingAlgorithm.DefaultSpaceRequiredForParking


            when(vehicleType) {
                VehicleType.MotorCycle.ordinal -> {
                    vehicle.vehicleType = VehicleType.MotorCycle
                    parkingAlgorithm.parkMotorCycle(vehicle)
                }
                VehicleType.SmallCar.ordinal -> {
                    vehicle.vehicleType = VehicleType.SmallCar
                    parkingAlgorithm.parkSmallCar(vehicle)
                }
                VehicleType.MediumCar.ordinal -> {
                    vehicle.vehicleType = VehicleType.MediumCar
                    parkingAlgorithm.parkMediumCar(vehicle)
                }
                VehicleType.LargeCar.ordinal -> {
                    vehicle.vehicleType = VehicleType.LargeCar
                    parkingAlgorithm.parkLargeCar(vehicle)
                }
                else -> vehicle.vehicleType = VehicleType.MotorCycle
            }

            newVehicleForParking.value!!.vehicleNumber = vehicleNo
        }
        catch (e: Exception) {
            throw e
        }
    }


}