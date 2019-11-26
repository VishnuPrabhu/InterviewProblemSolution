package com.isoft.parkingcalc

import android.view.Surface
import com.isoft.parkingcalc.db.Vehicle
import com.isoft.parkingcalc.models.VehicleType
import java.util.*
import java.util.concurrent.TimeUnit

class ParkingAlgorithm {

    companion object {
        const val DefaultSpaceRequiredForParking = 1.toDouble()
        const val DefaultSpaceRequiredForMotorCycleInMediumParking = 0.9
        const val SpaceRequiredForMotorCycleInSmallCarParkingSpot = 0.5
        const val SpaceRequiredForMotorCycleInMediumCarParking = 0.3333
        const val SpaceRequiredForMotorCycleInLargeCarParkingSpot = 0.25


        fun instance() = ParkingAlgorithm()
    }

    private val motorCycleParkingSpots = mutableListOf<Int>()

    init {
        motorCycleParkingSpots.addAll(1..2)
    }

    private val smallCarParkingSpots = mutableListOf<Int>()

    init {
        smallCarParkingSpots.addAll(1..2)
    }

    private val mediumCarParkingSpots = mutableListOf<Int>()

    init {
        mediumCarParkingSpots.addAll(1..2)
    }

    private val largeCarParkingSpots = mutableListOf<Int>()

    init {
        largeCarParkingSpots.addAll(1..2)
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    fun parkMotorCycle(vehicle: Vehicle): String {

        val error = verifyIfVehicleAlreadyParkedInside(vehicle.vehicleNumber)
        if (error) {
            throw VehicleAlreadyParkedException()
        }

        val result = ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.MotorCycle)
        val occupiedMotorCycleSpots = result.map { a -> a.assignedParkingSpaceNumber }

        val hasFreeSpots = (motorCycleParkingSpots.count() - occupiedMotorCycleSpots.count() >= 1)

        if (hasFreeSpots) {
            vehicle.allotedSpaceForParking = DefaultSpaceRequiredForParking
            val nextFreeSpot =
                motorCycleParkingSpots.filter { spot -> !occupiedMotorCycleSpots.contains(spot) }
                    .sorted().first()
            vehicle.assignedParkingSpaceNumber = nextFreeSpot

            // ** before adding to database change the vehicle type to Large Car, as it is in carparking
            // 1 motorcycle = 0.25 large car
            vehicle.parkingType = VehicleType.MotorCycle

            ParkingApplication.db.vehicleDao().addVehicle(vehicle)

            return "Park your vehicle at ${VehicleType.MotorCycle.name} parking space no: ${vehicle.assignedParkingSpaceNumber}"
        } else {
            return parkSmallCar(vehicle)
        }
    }

    private fun verifyIfVehicleAlreadyParkedInside(vehicleNumber: Int): Boolean {
        return ParkingApplication.db.vehicleDao().getParkedVehicleWithNumber(vehicleNumber) != null
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    fun parkSmallCar(vehicle: Vehicle): String {

        val error = verifyIfVehicleAlreadyParkedInside(vehicle.vehicleNumber)
        if (error) {
            throw VehicleAlreadyParkedException()
        }

        val occupiedSmallCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.SmallCar)

        val hasFreeSpots: Boolean

        if (vehicle.parkingType == VehicleType.MotorCycle) {
            hasFreeSpots =
                (smallCarParkingSpots.count() - occupiedSmallCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= SpaceRequiredForMotorCycleInSmallCarParkingSpot
        } else {
            hasFreeSpots =
                (smallCarParkingSpots.count() - occupiedSmallCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= DefaultSpaceRequiredForParking
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Small Car Spaces which has a bike sparked in it. allocated space range is 0.5
            if (vehicle.parkingType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInSmallCarParkingSpot

                var nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking = 0
                val occupiedSpaces = occupiedSmallCarSpots.map { a ->  Parking(a.assignedParkingSpaceNumber, a.allotedSpaceForParking) }.sortedBy { a -> a.number }.groupBy { a -> a.number }
                for(index in occupiedSpaces.keys.indices) {
                    val parkingNumber = occupiedSpaces.keys.toList()[index]
                    val spaceOccupied = occupiedSpaces.values.toList()[index].sumByDouble { b -> b.space }

                    if (spaceOccupied < DefaultSpaceRequiredForParking) {
                        nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking = parkingNumber
                        break
                    }
                }

                if (nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking != 0)
                {
                    vehicle.assignedParkingSpaceNumber = nextPartialOccupiedSpaceOfMotorCycleInSmallCarParking
                }
                else {
                    val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                        !occupiedSmallCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }
            // park your small car in a Small Car available parking space.
            } else {
                val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                    !occupiedSmallCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                        spot
                    )
                }.sorted().first()
                vehicle.assignedParkingSpaceNumber = nextFreeSpot
            }

            // ** before adding to database change the vehicle type to Snakk Car, as it is in carparking
            // 1 motorcycle = 0.5 small car
            vehicle.parkingType = VehicleType.SmallCar

            ParkingApplication.db.vehicleDao().addVehicle(vehicle)

            return "Park your vehicle at ${VehicleType.SmallCar.name} parking space no: ${vehicle.assignedParkingSpaceNumber}"
        } else {
            return parkMediumCar(vehicle)
        }
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    fun parkMediumCar(vehicle: Vehicle): String {
        val error = verifyIfVehicleAlreadyParkedInside(vehicle.vehicleNumber)
        if (error) {
            throw VehicleAlreadyParkedException()
        }

        val occupiedMediumCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.MediumCar)

        val hasFreeSpots: Boolean

        if (vehicle.parkingType == VehicleType.MotorCycle) {
            hasFreeSpots = (mediumCarParkingSpots.count() - occupiedMediumCarSpots.map { a ->
                roundTo2Decimals(a.allotedSpaceForParking)
            }.sum()) >= SpaceRequiredForMotorCycleInMediumCarParking
        } else {
            hasFreeSpots = (mediumCarParkingSpots.count() - occupiedMediumCarSpots.map { a ->
                roundTo2Decimals(a.allotedSpaceForParking)
            }.sum()) >= DefaultSpaceRequiredForParking
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Medium Car Spaces which has a bike sparked in it. allocated space range is 0.3, 0.6
            if (vehicle.parkingType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInMediumCarParking

                var nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking = 0
                val occupiedSpaces = occupiedMediumCarSpots.map { a ->  Parking(a.assignedParkingSpaceNumber, a.allotedSpaceForParking) }.sortedBy { a -> a.number }.groupBy { a -> a.number }
                for(index in occupiedSpaces.keys.indices) {
                    val parkingNumber = occupiedSpaces.keys.toList()[index]
                    val spaceOccupied = occupiedSpaces.values.toList()[index].sumByDouble { b -> b.space }

                    if (spaceOccupied < DefaultSpaceRequiredForMotorCycleInMediumParking) {
                        nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking = parkingNumber
                        break
                    }
                }

                if (nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking != 0)
                {
                    vehicle.assignedParkingSpaceNumber = nextPartialOccupiedSpaceOfMotorCycleInMediumCarParking
                }
                else {
                    val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                        !occupiedMediumCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }
            // Park your medium car in a Medium Car available parking space
            } else {
                val nextFreeSpot = mediumCarParkingSpots.filter { spot ->
                    !occupiedMediumCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                        spot
                    )
                }.sorted().first()
                vehicle.assignedParkingSpaceNumber = nextFreeSpot
            }

            // ** before adding to database change the vehicle type to Medium Car, as it is in carparking
            // 1 motorcycle = 0.3 Medium car
            vehicle.parkingType = VehicleType.MediumCar

            ParkingApplication.db.vehicleDao().addVehicle(vehicle)

            return "Park your vehicle at ${VehicleType.MediumCar.name} parking space no: ${vehicle.assignedParkingSpaceNumber}"
        } else {
            return parkLargeCar(vehicle)
        }
    }

    @Throws(
        Surface.OutOfResourcesException::class,
        VehicleAlreadyParkedException::class,
        Exception::class
    )
    fun parkLargeCar(vehicle: Vehicle): String {
        val error = verifyIfVehicleAlreadyParkedInside(vehicle.vehicleNumber)
        if (error) {
            throw VehicleAlreadyParkedException()
        }

        val occupiedLargeCarSpots =
            ParkingApplication.db.vehicleDao().getParkedVehicles(VehicleType.LargeCar)

        val hasFreeSpots: Boolean

        if (vehicle.parkingType == VehicleType.MotorCycle) {
            hasFreeSpots =
                (largeCarParkingSpots.count() - occupiedLargeCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= SpaceRequiredForMotorCycleInLargeCarParkingSpot
        } else {
            hasFreeSpots =
                (mediumCarParkingSpots.count() - occupiedLargeCarSpots.map { a -> roundTo2Decimals(a.allotedSpaceForParking) }.sum()) >= DefaultSpaceRequiredForParking
        }

        if (hasFreeSpots) {
            // Check if you have any remaining available space in Medium Car Spaces which has a bike sparked in it. allocated space range is 0.3, 0.6
            if (vehicle.parkingType == VehicleType.MotorCycle) {
                vehicle.allotedSpaceForParking = SpaceRequiredForMotorCycleInLargeCarParkingSpot

                var nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking = 0
                val occupiedSpaces = occupiedLargeCarSpots.map { a ->  Parking(a.assignedParkingSpaceNumber, a.allotedSpaceForParking) }.sortedBy { a -> a.number }.groupBy { a -> a.number }
                for(index in occupiedSpaces.keys.indices) {
                    val parkingNumber = occupiedSpaces.keys.toList()[index]
                    val spaceOccupied = occupiedSpaces.values.toList()[index].sumByDouble { b -> b.space }

                    if (spaceOccupied < DefaultSpaceRequiredForParking) {
                        nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking = parkingNumber
                        break
                    }
                }

                if (nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking != 0)
                {
                    vehicle.assignedParkingSpaceNumber = nextPartialOccupiedSpaceOfMotorCycleInLargeCarParking
                }
                else {
                    val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                        !occupiedLargeCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                            spot
                        )
                    }.sorted().first()
                    vehicle.assignedParkingSpaceNumber = nextFreeSpot
                }

            } else {
                val nextFreeSpot = smallCarParkingSpots.filter { spot ->
                    !occupiedLargeCarSpots.map { a -> a.assignedParkingSpaceNumber }.contains(
                        spot
                    )
                }.sorted().first()
                vehicle.assignedParkingSpaceNumber = nextFreeSpot
            }
            // ** before adding to database change the vehicle type to Large Car, as it is in carparking
            // 1 motorcycle = 0.25 large car
            vehicle.parkingType = VehicleType.LargeCar

            ParkingApplication.db.vehicleDao().addVehicle(vehicle)

            return "Park your vehicle at ${VehicleType.LargeCar.name} parking space no: ${vehicle.assignedParkingSpaceNumber}"
        } else {
            throw OutOfParkingLotException()
        }
    }

    @Throws(VehicleNotAvailableException::class)
    fun departVehicle(vehicleNumber: Int): Double {
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

                // Remove From Database ***
                ParkingApplication.db.vehicleDao().deleteVehicle(departingVehicle)
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

    data class Parking(val number: Int, val space: Double)

}