package com.isoft.parkingcalc.extenstions


class OutOfParkingLotException: Exception("No available space for parking the vehicle")

class VehicleNotAvailableException: Exception("Unable to find the vehicle with vehicle number")

class VehicleAlreadyParkedException: Exception("This Vehicle is already inside the Parking")