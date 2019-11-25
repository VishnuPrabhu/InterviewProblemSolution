package com.isoft.parkingcalc.models

import java.util.concurrent.TimeUnit

enum class VehicleType {

//    companion object {
//        fun newMotorCycle() {
//            VehicleType(50, 25)
//        }
//        fun newSmallCar() {
//            VehicleType(100, 45)
//        }
//        fun newMediumCar() {
//            VehicleType(150, 75)
//        }
//        fun newLargeCar() {
//            VehicleType(200, 100)
//        }
//    }


    MotorCycle(50, 25), SmallCar(100, 45),
    MediumCar(150, 75), LargeCar(200, 100);

    val parkingFareFor1stHour: Int
    val parkingFareAfter1stHour: Int

    constructor(parkingFareFor1stHour: Int, parkingFareAfter1stHour: Int) {
        this.parkingFareFor1stHour = parkingFareAfter1stHour
        this.parkingFareAfter1stHour = parkingFareAfter1stHour
    }

    private val NoOfHoursEligibleForDiscount = TimeUnit.HOURS.convert(5, TimeUnit.DAYS)
    private val DiscountPercentage = 0.25

    fun calculateParkingFare(noOfHours: Long): Double {

        if (noOfHours > NoOfHoursEligibleForDiscount)
        {
            val fareFor1stHour = 1 * parkingFareFor1stHour
            val fareForRemainingHourNotApplicableForDiscount = (NoOfHoursEligibleForDiscount - 1) * parkingFareAfter1stHour
            val fareWithoutDiscount = fareFor1stHour + fareForRemainingHourNotApplicableForDiscount
            val fareWithDiscount = DiscountPercentage * (noOfHours - NoOfHoursEligibleForDiscount) * parkingFareAfter1stHour

            val totalFareWithDiscount = fareWithoutDiscount + fareWithDiscount
            return  totalFareWithDiscount
        }
        else
        {
            val fareFor1stHour = 1 * parkingFareFor1stHour
            val fareForRemainingHour = (noOfHours - 1) * parkingFareAfter1stHour
            val totalFare = fareFor1stHour + fareForRemainingHour
            return totalFare.toDouble()
        }
    }


}