package com.isoft.parkingcalc.db

import androidx.room.*
import com.isoft.parkingcalc.models.VehicleType

@Dao
interface VehicleDao {

    @Query("SELECT * FROM Vehicle where parking_type = :parkingType")
    fun getParkedVehicles(parkingType: VehicleType): List<Vehicle>

    @Query("SELECT * FROM Vehicle WHERE vehicleNumber = :number")
    fun getParkedVehicleWithNumber(number: Int): Vehicle?

    @Query("SELECT * FROM Vehicle")
    fun getAllParkedVehicles(): List<Vehicle>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addVehicle(vehicle: Vehicle)

    @Delete
    fun deleteVehicle(vehicle: Vehicle)

}