package com.isoft.parkingcalc.db

import androidx.room.TypeConverter
import com.isoft.parkingcalc.models.VehicleType
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromVehicleTypeToString(vehicleType: VehicleType): String? {
        return vehicleType.name
    }

    @TypeConverter
    fun fromStringToVehicleType(vehicleType: String): VehicleType {
        return VehicleType.valueOf(vehicleType)
    }
}