package com.isoft.parkingcalc

import android.app.Application
import androidx.room.Room
import com.isoft.parkingcalc.db.VehicleDatabase

class ParkingApplication: Application() {


    companion object {
        public lateinit var  db: VehicleDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(this,
                                  VehicleDatabase::class.java,
                                 "vehicle_database"
                                 ).allowMainThreadQueries().build()
    }
}