package com.example.airqualitymonitoring

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Date

class MainActivity : AppCompatActivity(), WebSocketCallback {

    companion object {
        const val TAG = "MainActivity"
        const val WEB_SOCKET_URL = "wss://city-ws.herokuapp.com/"
    }

    private lateinit var mClient: OkHttpClient
    private lateinit var recyclerAqi: RecyclerView
    private var listAirQualityUnique = mutableListOf<AirQuality>()
    private var listAirQualityAll = mutableListOf<AirQuality>()
    private var chartDialogFragment: ChartDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        mClient = OkHttpClient()
        startWebSocket()
    }

    private fun initViews() {
        recyclerAqi = findViewById(R.id.recyclerAQI)
        recyclerAqi.itemAnimator = null
        AQIAdapter().apply {
            recyclerAqi.adapter = this
            this.onItemClick = { airQuality ->
                val listFiltered = listAirQualityAll.filter { it.city == airQuality.city }
                if (listFiltered.isNotEmpty()) {
                    chartDialogFragment = ChartDialogFragment(airQuality.city, listFiltered)
                    chartDialogFragment?.show(supportFragmentManager, ChartDialogFragment.TAG)
                } else {
                    Toast.makeText(this@MainActivity, "Data not available!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startWebSocket() {
        val request = Request.Builder().url(WEB_SOCKET_URL).build()
        val listener = EchoWebSocketListener(this)
        mClient.newWebSocket(request, listener)
        mClient.dispatcher().executorService().shutdown()
    }

    override fun onMessage(message: String) {
        Log.d(TAG, "onMessage $message")
        val type = object : TypeToken<List<AirQuality>>() {}.type
        val listAirQuality: List<AirQuality> = Gson().fromJson(message, type)
        listAirQuality.map { airQuality ->
            val found = listAirQualityUnique.any { it.city == airQuality.city }
            if (found) {
                val indexMatch = listAirQualityUnique.indexOfFirst { it.city == airQuality.city }
                if (listAirQualityUnique[indexMatch].aqi != airQuality.aqi)
                    airQuality.lastUpdated = Date()
                else
                    airQuality.lastUpdated = listAirQualityUnique[indexMatch].lastUpdated
                listAirQualityUnique[indexMatch] = airQuality
            } else {
                airQuality.lastUpdated = Date()
                listAirQualityUnique.add(airQuality)
            }
            // For chart operations
            listAirQualityAll.add(airQuality)
        }
        runOnUiThread {
            (recyclerAqi.adapter as? AQIAdapter)?.submitList(listAirQualityUnique.toMutableList())
        }
    }

    override fun onClose(message: String) {
        Log.d(TAG, "onClose $message")
    }

    override fun onError(error: String) {
        Log.e(TAG, "onError $error")
    }
}