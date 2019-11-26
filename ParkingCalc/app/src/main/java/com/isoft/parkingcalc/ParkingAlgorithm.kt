package com.isoft.parkingcalc

import android.view.Surface
import com.isoft.parkingcalc.db.Vehicle
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

    init {
        motorCycleParkingSpots.addAll(1..10)
    }

    val smallCarParkingSpots = mutableListOf<Int>()

    init {
        smallCarParkingSpots.addAll(1..10)
    }

    val mediumCarParkingSpots = mutableListOf<Int>()

    init {
        mediumCarParkingSpots.addAll(1..10)
    }

    val largeCarParkingSpots = mutableListOf<Int>()

    init {
        largeCarParkingSpots.addAll(1..10)
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    public fun parkMotorCycle(vehicle: Vehicle) {
        val result = ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.MotorCycle)
        val occupiedMotorCycleSpots = result.map { a -> a.vehicleNumber }

        if (occupiedMotorCycleSpots.contains(vehicle.vehicleNumber)) {
            throw VehicleAlreadyParkedException()
        }

        val hasFreeSpots = (motorCycleParkingSpots.count() - occupiedMotorCycleSpots.count() >= 1);

        if (hasFreeSpots) {
            vehicle.allotedSpaceForParking = DefaultSpaceRequiredForParking
            val nextFreeSpot =
                motorCycleParkingSpots.filter { spot -> !occupiedMotorCycleSpots.contains(spot) }
                    .sorted().first()
            vehicle.assignedParkingSpaceNumber = nextFreeSpot
            ParkingApplication.db.vehicleDao().addVehicle(vehicle)
        } else {
            parkSmallCar(vehicle)
        }
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    public fun parkSmallCar(vehicle: Vehicle) {
        val occupiedSmallCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.SmallCar)
        val occupiedSmallCarVehicleNumbers = occupiedSmallCarSpots.map { a -> a.vehicleNumber }

        if (occupiedSmallCarVehicleNumbers.contains(vehicle.vehicleNumber)) {
            throw VehicleAlreadyParkedException()
        }

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
            ParkingApplication.db.vehicleDao().addVehicle(vehicle)
        } else {
            parkMediumCar(vehicle)
        }
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    public fun parkMediumCar(vehicle: Vehicle) {
        val occupiedMediumCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.SmallCar)
        val occupiedMediumCarVehicleNumbers = occupiedMediumCarSpots.map { a -> a.vehicleNumber }

        if (occupiedMediumCarVehicleNumbers.contains(vehicle.vehicleNumber)) {
            throw VehicleAlreadyParkedException()
        }

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
            ParkingApplication.db.vehicleDao().addVehicle(vehicle)
        } else {
            parkLargeCar(vehicle)
        }
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    public fun parkLargeCar(vehicle: Vehicle) {
        val occupiedLargeCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.SmallCar)
        val occupiedLargeCarVehicleNumbers = occupiedLargeCarSpots.map { a -> a.vehicleNumber }

        if (occupiedLargeCarVehicleNumbers.contains(vehicle.vehicleNumber)) {
            throw VehicleAlreadyParkedException()
        }

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
                ParkingApplication.db.vehicleDao().addVehicle(vehicle)
            } else {
                throw OutOfParkingLotException()
            }
        }
    }

    @Throws(VehicleNotAvailableException::class)
    public fun departVehicle(vehicleNumber: Int): Double {
        try {
            val departingVehicle =
                ParkingApplication.db.vehicleDao().getParkedVehicleWithNumber(vehicleNumber)

            if (departingVehicle != null) {

                val enteredDate = departingVehicle.enterDate
                val departingDate = Date()

                val timeDifferenceInMilliSeconds = departingDate.time - enteredDate.time
                val noOfParkingHours =
                    TimeUnit.HOURS.convert(timeDifferenceInMilliSeconds, TimeUnit.MILLISECONDS)

                val fare = departingVehicle.vehicleType.calculateParkingFare(noOfParkingHours)
                return fare
            }
            else {
                print(VehicleNotAvailableException().message)
                throw VehicleNotAvailableException()
            }
        } catch (e: Exception) {
            print(e.message)
            throw e
        }
    }


    private fun roundTo2Decimals(value: Double): Double {
        return String.format("%.2f", value).toDouble()
    }

}