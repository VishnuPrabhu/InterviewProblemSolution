package com.isoft.parkingcalc

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isoft.parkingcalc.db.Vehicle
import com.isoft.parkingcalc.db.VehicleDao
import com.isoft.parkingcalc.db.VehicleDatabase
import com.isoft.parkingcalc.models.VehicleType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var vehicleDao: VehicleDao
    private lateinit var db: VehicleDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, VehicleDatabase::class.java).build()
        vehicleDao = db.vehicleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertVehicle() {
        val vehicle: Vehicle = TestUtil.addMotorCycleInMotorCycleParking().apply {
            vehicleNumber = 2364
            parkingType = VehicleType.MotorCycle
            vehicleType = VehicleType.MotorCycle
            enterDate = Date()
            exitDate = Date()
            assignedParkingSpaceNumber = 1
            allotedSpaceForParking = 1.0
        }
        vehicleDao.addVehicle(vehicle)
        val byNumber = vehicleDao.getParkedVehicleWithNumber(2364)
        assertThat(byNumber?.vehicleNumber, equalTo(vehicle.vehicleNumber))
    }

}