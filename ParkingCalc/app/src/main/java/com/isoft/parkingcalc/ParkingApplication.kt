package com.isoft.parkingcalc

import android.app.Application
import androidx.room.Room
import com.isoft.parkingcalc.db.VehicleDatabase
import com.isoft.parkingcalc.di.ApplicationModule
import com.isoft.parkingcalc.di.DaggerApplicationComponent
import com.isoft.parkingcalc.di.DatabaseModule


class ParkingApplication: Application() {

    companion object {
        public lateinit var  db: VehicleDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // Having some issues while integrating dagger. so for now I am using legacy approach of creating singleton instance of db
//         val applicationComponent = DaggerApplicationComponent
//            .builder()
//            .applicationModule(ApplicationModule(this))
//            .databaseModule(DatabaseModule(this))
//            .build()
//        applicationComponent.inject(this)

        db = Room.databaseBuilder(this,
                                  VehicleDatabase::class.java,
                                 "vehicle_database"
                                 ).allowMainThreadQueries().build()
    }
}