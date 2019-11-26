package com.isoft.parkingcalc.di

import android.app.Application
import android.content.Context
import com.isoft.parkingcalc.MainActivity
import com.isoft.parkingcalc.ParkingApplication
import com.isoft.parkingcalc.extenstions.ApplicationContext
import com.isoft.parkingcalc.extenstions.DatabaseInfo
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class])
interface ApplicationComponent {
    fun inject(parkingApplication: ParkingApplication)
    fun inject(mainActivity: MainActivity)

    @ApplicationContext
    val context: Context?

    val application: Application?

    @DatabaseInfo
    val databaseName: String?
}