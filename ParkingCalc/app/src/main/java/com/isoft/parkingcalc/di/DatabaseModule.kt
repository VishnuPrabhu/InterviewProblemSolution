package com.isoft.parkingcalc.di

import android.content.Context
import androidx.room.Room
import com.isoft.parkingcalc.db.VehicleDao
import com.isoft.parkingcalc.db.VehicleDatabase
import com.isoft.parkingcalc.extenstions.ApplicationContext
import com.isoft.parkingcalc.extenstions.DatabaseInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule(val mContext: Context) {
    @DatabaseInfo
    private val mDBName = "vehicle_database.db"

    @Singleton
    @Provides
    fun provideDatabase(): VehicleDatabase {
        return Room.databaseBuilder(
            mContext,
            VehicleDatabase::class.java,
            mDBName
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideDatabaseName(): String {
        return mDBName
    }

    @Singleton
    @Provides
    fun provideVehicleDao(db: VehicleDatabase): VehicleDao {
        return db.vehicleDao()
    }
}