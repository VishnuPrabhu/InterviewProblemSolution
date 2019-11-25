package com.isoft.parkingcalc.models

import com.isoft.parkingcalc.ParkingAlgorithm
import java.util.*

data class Vehicle (var vehicleNumber: Int = 0,
                    var vehicleType: VehicleType = VehicleType.MotorCycle,
                    var enterDate: Date = Date(),
                    var exitDate: Date = Date(),
                    var assignedParkingSpaceNumber: Int = 0,
                    var allotedSpaceForParking: Double = ParkingAlgorithm.DefaultSpaceRequiredForParking)