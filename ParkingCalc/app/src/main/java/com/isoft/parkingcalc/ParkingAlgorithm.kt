package com.isoft.parkingcalc

import android.view.Surface
import com.isoft.parkingcalc.models.Vehicle
import com.isoft.parkingcalc.models.VehicleType
import java.util.*
import java.util.concurrent.TimeUnit

class ParkingAlgorithm {

    companion object {
        const val DefaultSpaceRequiredForParking = 1.toDouble()
        const val SpaceRequiredForMotorCycleInSmallCarParkingSpot = 0.5
        const val SpaceRequiredForMotorCycleInMediumCarParking = 0.3
        const val SpaceRequiredForMotorCycleInLargeCarParkingSpot = 0.25



        fun instance() = ParkingAlgorithm()
    }

    val motorCycleParkingSpots = mutableListOf<Int>()
    init{
        motorCycleParkingSpots.addAll(1..10)
    }
    val smallCarParkingSpots = mutableListOf<Int>()
    init{
        smallCarParkingSpots.addAll(1..10)
    }
    val mediumCarParkingSpots = mutableListOf<Int>()
    init{
        mediumCarParkingSpots.addAll(1..10)
    }
    val largeCarParkingSpots = mutableListOf<Int>()
    init{
        largeCarParkingSpots.addAll(1..10)
    }

    @Throws(Surface.OutOfResourcesException::class, Exception::class)
    public fun parkMotorCycle(vehicle: Vehicle) {
        // database
        // select motorcycle
        // returns list of vehicle object
        // convert to dictionary and then to list of keys
        val occupiedMotorCycleSpots = listOf<Int>(3)

        val hasFreeSpots = (motorCycleParkingSpots.count() - occupiedMotorCycleSpots.count() >= 1);

        if (hasFreeSpots) {
            vehicle.allotedSpaceForParking = DefaultSpaceRequiredForParking
            val nextFreeSpot = motorCycleParkingSpots.sorted()
                .filter { spot -> !occupiedMotorCycleSpots.contains(spot) }
            // add to data base
        } else {
            parkSmallCar(vehicle)
        }
    }

    @Throws(Surface.OutOfResourcesException::class, Exception::class)
    public fun parkSmallCar(vehicle: Vehicle) {
        // database
        // select small car
        // returns list of vehicle object
        // convert to dictionary and then to list of keys
        val occupiedSmallCarSpots = listOf<Vehicle>()

        val hasFreeSpots: Boolean

        if (vehicle.vehicleType == VehicleType.MotorCycle) {
            hasFreeSpots =
                (smallCarParkingSpots.count() - occupiedSmallCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= SpaceRequiredForMotorCycleInSmallCarParkingSpot;
        } else {
            hasFreeSpots =
                (smallCarParkingSpots.sum() - occupiedSmallCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= DefaultSpaceRequiredForParking;
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Small Car Spaces which has a bike sparked in it. allocated space range is 0.5
            if (vehicle.vehicleType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInSmallCarParkingSpot

                // All occupied spots will have allotedSpaceForParking > 0
                val nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking =
                    occupiedSmallCarSpots.filter { spot -> roundTo2Decimals(spot.allotedSpaceForParking) < DefaultSpaceRequiredForParking }
                // you have a space with bike parking -> use that space
                if (nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking.count() > 0) {
                    val nextFreeSpot =
                        nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking.sortedBy { a -> a.assignedParkingSpaceNumber }
                            .first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot.assignedParkingSpaceNumber
                }
                // you dont have any space available, park in a instance space ans assign 0.5 for space occupied
                else {
                    val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                        !occupiedSmallCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }
            }
            // add Vehicle to data base
        } else {
            parkMediumCar(vehicle)
        }
    }

    @Throws(Surface.OutOfResourcesException::class, Exception::class)
    public fun parkMediumCar(vehicle: Vehicle) {
        // database
        // select medium car
        // returns list of vehicle object
        // convert to dictionary and then to list of keys
        val occupiedMediumCarSpots = listOf<Vehicle>()

        val hasFreeSpots: Boolean

        if (vehicle.vehicleType == VehicleType.MotorCycle) {
            hasFreeSpots = (mediumCarParkingSpots.count() - occupiedMediumCarSpots.map { a ->
                roundTo2Decimals(a.allotedSpaceForParking)
            }.sum()) >= SpaceRequiredForMotorCycleInMediumCarParking;
        } else {
            hasFreeSpots = (mediumCarParkingSpots.count() - occupiedMediumCarSpots.map { a ->
                roundTo2Decimals(a.allotedSpaceForParking)
            }.sum()) >= DefaultSpaceRequiredForParking;
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Medium Car Spaces which has a bike sparked in it. allocated space range is 0.3, 0.6
            if (vehicle.vehicleType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInMediumCarParking

                // All occupied spots will have allotedSpaceForParking > 0
                val nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking =
                    occupiedMediumCarSpots.filter { spot -> roundTo2Decimals(spot.allotedSpaceForParking) < DefaultSpaceRequiredForParking }
                // you have a space with bike parking -> use that space
                if (nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking.count() > 0) {
                    val nextFreeSpot =
                        nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking.sortedBy { a -> a.assignedParkingSpaceNumber }
                            .first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot.assignedParkingSpaceNumber
                }
                // you dont have any space available, park in a instance space ans assign 0.5 for space occupied
                else {
                    val nextFreeSpot = mediumCarParkingSpots.filter { spot ->
                        !occupiedMediumCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }
            }
            // add Vehicle to data base
        } else {
            parkLargeCar(vehicle)
        }
    }

    @Throws(Surface.OutOfResourcesException::class, Exception::class)
    public fun parkLargeCar(vehicle: Vehicle) {
        // database
        // select Large car
        // returns list of vehicle object
        // convert to dictionary and then to list of keys
        val occupiedLargeCarSpots = listOf<Vehicle>()

        val hasFreeSpots: Boolean

        if (vehicle.vehicleType == VehicleType.MotorCycle) {
            hasFreeSpots =
                (largeCarParkingSpots.count() - occupiedLargeCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= SpaceRequiredForMotorCycleInMediumCarParking;
        } else {
            hasFreeSpots =
                (mediumCarParkingSpots.count() - occupiedLargeCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= DefaultSpaceRequiredForParking;
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Medium Car Spaces which has a bike sparked in it. allocated space range is 0.3, 0.6
            if (vehicle.vehicleType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInLargeCarParkingSpot

                // All occupied spots will have allotedSpaceForParking > 0
                val nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking =
                    occupiedLargeCarSpots.filter { spot -> roundTo2Decimals(spot.allotedSpaceForParking) < DefaultSpaceRequiredForParking }
                // you have a space with bike parking -> use that space
                if (nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking.count() > 0) {
                    val nextFreeSpot =
                        nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking.sortedBy { a -> a.assignedParkingSpaceNumber }
                            .first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot.assignedParkingSpaceNumber
                }
                // you dont have any space available, park in a instance space ans assign 0.5 for space occupied
                else {
                    val nextFreeSpot = mediumCarParkingSpots.filter { spot ->
                        !occupiedLargeCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }
                // add Vehicle to data base
            } else {
                throw OutOfParkingLotException()
            }
        }
    }

    @Throws(VehicleNotAvailableException::class)
    public fun departVehicle(vehicleNumber: Int): Double {
        // database
        // select * from db where vehicle n0 == vno
        // you get a vehicle
        // remove from Database

        if (true) {

            lateinit var departingVehicle: Vehicle

            val enteredDate = departingVehicle.enterDate
            val departingDate = Date()

            val timeDifferenceInMilliSeconds = departingDate.time - enteredDate.time
            val noOfParkingHours =
                TimeUnit.HOURS.convert(timeDifferenceInMilliSeconds, TimeUnit.MILLISECONDS)

            val fare = departingVehicle.vehicleType.calculateParkingFare(noOfParkingHours)
            return fare
        } else {
            throw VehicleNotAvailableException()
        }
    }

    private fun roundTo2Decimals(value: Double): Double {
        return String.format("%.2f", value).toDouble()
    }

}