package com.example.airqualitymonitoring

import java.util.Date

data class AirQuality(val city: String, val aqi: Float, var lastUpdated: Date)
