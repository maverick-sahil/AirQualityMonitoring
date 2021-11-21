package com.example.airqualitymonitoring

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartDialogFragment(private val city: String, private val listAirQuality: List<AirQuality>) : DialogFragment() {

    companion object {
        const val TAG = "ChartDialogFragment"
    }

    private lateinit var textCity: TextView
    private lateinit var lineChart: LineChart
    private var timeCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textCity = view.findViewById(R.id.textCity)
        textCity.text = city
        lineChart = view.findViewById(R.id.lineChart)
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        renderData()
    }

    private fun renderData() {
        val llXAxis = LimitLine(10f, "")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val xAxis: XAxis = lineChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.axisMaximum = 100f
        xAxis.axisMinimum = 0f
        xAxis.setDrawLimitLinesBehindData(true)
        val ll1 = LimitLine(300f, "Severe")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        val ll2 = LimitLine(201f, "Poor")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        val leftAxis: YAxis = lineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(ll1)
        leftAxis.addLimitLine(ll2)
        leftAxis.axisMaximum = 400f
        leftAxis.axisMinimum = 0f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawLimitLinesBehindData(false)
        lineChart.axisRight.isEnabled = false
        lineChart.setVisibleXRangeMaximum(10f)
        setData()
    }

    fun setData() {
        val values: ArrayList<Entry> = ArrayList()
        listAirQuality.map {
            values.add(Entry(timeCounter++.toFloat(), it.aqi))
        }
        val set1: LineDataSet
        if (lineChart.data != null &&
            lineChart.data.dataSetCount > 0
        ) {
            set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "AQI")
            set1.setDrawIcons(false)
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.DKGRAY
            set1.setCircleColor(Color.DKGRAY)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            lineChart.data = data
        }
    }
}