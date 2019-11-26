package com.isoft.parkingcalc.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.isoft.parkingcalc.algorithms.ParkingAlgorithm
import com.isoft.parkingcalc.models.VehicleType
import java.util.*

@Entity
data class Vehicle(
    @PrimaryKey var vehicleNumber: Int = 0,
    @ColumnInfo(name = "parking_type") var parkingType: VehicleType = VehicleType.MotorCycle,
    @ColumnInfo(name = "vehicle_type") var vehicleType: VehicleType = VehicleType.MotorCycle,
    @ColumnInfo(name = "enter_date") var enterDate: Date = Date(),
    @Ignore var exitDate: Date = Date(),
    @ColumnInfo(name = "assigned_number") var assignedParkingSpaceNumber: Int = 0,
    @ColumnInfo(name = "alloted_space") var allotedSpaceForParking: Double = ParkingAlgorithm.DefaultSpaceRequiredForParking
)