package com.isoft.parkingcalc


class OutOfParkingLotException: Exception("No available space for parking the vehicle")

class VehicleNotAvailableException: Exception("Unable to find the vehicle with vehicle number")