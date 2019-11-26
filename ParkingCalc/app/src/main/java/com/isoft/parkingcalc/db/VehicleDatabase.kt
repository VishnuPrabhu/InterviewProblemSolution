package com.isoft.parkingcalc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(Vehicle::class), version = VehicleDatabase.VERSION)
@TypeConverters(Converters::class)
abstract class VehicleDatabase: RoomDatabase() {
    companion object { const val VERSION = 1 }

    abstract fun vehicleDao(): VehicleDao
}