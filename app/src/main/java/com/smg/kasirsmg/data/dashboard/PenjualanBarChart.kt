package com.smg.kasirsmg.data.dashboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import java.time.LocalDate

@Composable
fun PenjualanBarChart(
    listData: List<Int>
) {
    val today = LocalDate.now()
    val dates = mutableListOf<LocalDate>()
    for (i in 0..6) {
        val date = today.minusDays(i.toLong())
        dates.add(date)
    }

    val steps = listData.size
    val pointsData = mutableListOf<Point>()

    listData.forEachIndexed { index, item ->
        pointsData.add(Point(index.toFloat(), item.toFloat()))
    }

    val penjualanTertinggi = listData.sortedByDescending { it }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(MaterialTheme.colorScheme.surfaceContainerLowest)
        .steps(pointsData.size - 1)
        .labelData {  i ->
            dates[i].toString()
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.primaryContainer)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps+1)
        .backgroundColor(MaterialTheme.colorScheme.surfaceContainerLowest)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = (penjualanTertinggi[0] + 1) / steps
            (i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.primaryContainer)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.primaryContainer
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    ShadowUnderLine(
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = Color.Transparent),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp),
        lineChartData = lineChartData
    )
}