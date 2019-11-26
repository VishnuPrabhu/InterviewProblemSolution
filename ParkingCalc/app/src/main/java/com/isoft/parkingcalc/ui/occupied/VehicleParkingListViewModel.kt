package com.isoft.parkingcalc.ui.occupied

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isoft.parkingcalc.ParkingApplication
import com.isoft.parkingcalc.db.Vehicle

public class VehicleParkingListViewModel: ViewModel() {

    fun getAllParkedVehicles(): List<Section> {
        val parkedVehicles = ParkingApplication.db.vehicleDao().getAllParkedVehicles()

        val parkingList = parkedVehicles.groupBy { a -> a.parkingType }
        val types = parkingList.keys.toList()
        val vehicles = parkingList.values.toList()

        val list = mutableListOf<Section>()
        for (index in types.indices) {
            list.add(Section(types[index].name, vehicles[index].map { a -> "VehicleTYpe: ${a.vehicleType.name} Parked in ${a.assignedParkingSpaceNumber}" }.toMutableList()))
        }
        return list
    }
}