package com.vasilecoste.babylog.ui.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.repository.DailyAggregate
import com.vasilecoste.babylog.ui.components.SimpleTopBar
import com.vasilecoste.babylog.ui.main.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val BottomAxisFormatter = CartesianValueFormatter { _, value, _ ->
    LocalDate.ofEpochDay(value.toLong()).format(DateTimeFormatter.ofPattern("MMM d"))
}

private enum class ChartFilter { CURRENT_MONTH, PREVIOUS_MONTH, ALL_TIME }

private val ChartFilter.labelRes: Int
    get() = when (this) {
        ChartFilter.CURRENT_MONTH -> R.string.chart_filter_current_month
        ChartFilter.PREVIOUS_MONTH -> R.string.chart_filter_previous_month
        ChartFilter.ALL_TIME -> R.string.chart_filter_all_time
    }

private fun List<DailyAggregate>.filteredBy(selected: ChartFilter): List<DailyAggregate> {
    val today = LocalDate.now()
    val month = when (selected) {
        ChartFilter.CURRENT_MONTH -> today
        ChartFilter.PREVIOUS_MONTH -> today.minusMonths(1)
        ChartFilter.ALL_TIME -> return this
    }
    return filter { it.date.year == month.year && it.date.month == month.month }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onMenuClick: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()
    var chartFilter by remember { mutableStateOf(ChartFilter.CURRENT_MONTH) }
    val data = remember(uiState.chartData, chartFilter) { uiState.chartData.filteredBy(chartFilter) }

    Scaffold(topBar = { SimpleTopBar(title = stringResource(R.string.statistics_title), onMenuClick = onMenuClick) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ChartFilterSelector(
                selected = chartFilter,
                onSelect = { chartFilter = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )

            if (data.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.chart_no_data), style = MaterialTheme.typography.titleMedium)
                }
                return@Scaffold
            }

            val weightPoints = data
                .dropWhile { (it.weightKg ?: 0.0) <= 0 }
                .dropLastWhile { (it.weightKg ?: 0.0) <= 0 }
                .mapNotNull { d -> d.weightKg?.let { d.date to it } }

            val heightPoints = data
                .dropWhile { (it.heightCm ?: 0.0) <= 0 }
                .dropLastWhile { (it.heightCm ?: 0.0) <= 0 }
                .mapNotNull { d -> d.heightCm?.let { d.date to it } }

            val foodPoints = data
                .dropWhile { it.totalFoodMl <= 0 }
                .dropLastWhile { it.totalFoodMl <= 0 }
                .map { it.date to it.totalFoodMl.toDouble() }

            val tummyTimePoints = data
                .dropWhile { it.tummyTimeSeconds <= 0 }
                .dropLastWhile { it.tummyTimeSeconds <= 0 }
                .map { it.date to it.tummyTimeSeconds.toDouble() }

            Column(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(
                    stringResource(R.string.chart_growth_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                if (weightPoints.isEmpty() && heightPoints.isEmpty()) {
                    Text(
                        stringResource(R.string.chart_no_data),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                } else {
                    GrowthChart(
                        weightPoints = weightPoints,
                        weightFormatter = CartesianValueFormatter.decimal(suffix = stringResource(R.string.chart_axis_suffix_kg)),
                        heightPoints = heightPoints.ifEmpty { null },
                        heightFormatter = CartesianValueFormatter.decimal(suffix = stringResource(R.string.chart_axis_suffix_cm)),
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    )
                    Legend(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        items = buildList {
                            if (weightPoints.isNotEmpty()) {
                                add(MaterialTheme.colorScheme.primary to stringResource(R.string.chart_legend_weight_kg))
                            }
                            if (heightPoints.isNotEmpty()) {
                                add(MaterialTheme.colorScheme.tertiary to stringResource(R.string.chart_legend_height_cm))
                            }
                        },
                    )
                }

                Text(
                    stringResource(R.string.chart_food_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                if (foodPoints.isEmpty()) {
                    Text(
                        stringResource(R.string.chart_no_food_data),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                } else {
                    DualLineChart(
                        primaryPoints = foodPoints,
                        primaryFormatter = CartesianValueFormatter.decimal(suffix = stringResource(R.string.chart_axis_suffix_ml)),
                        secondaryPoints = null,
                        secondaryFormatter = null,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    )
                    Legend(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        items = listOf(MaterialTheme.colorScheme.primary to stringResource(R.string.chart_legend_total_food_ml)),
                    )
                }

                Text(
                    stringResource(R.string.chart_tummy_time_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                if (tummyTimePoints.isEmpty()) {
                    Text(
                        stringResource(R.string.chart_no_tummy_time_data),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                } else {
                    DualLineChart(
                        primaryPoints = tummyTimePoints,
                        primaryFormatter = MinutesSecondsValueFormatter,
                        secondaryPoints = null,
                        secondaryFormatter = null,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    )
                    Legend(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        items = listOf(MaterialTheme.colorScheme.primary to stringResource(R.string.chart_legend_tummy_time)),
                    )
                }
            }
        }
    }
}

private val MinutesSecondsValueFormatter = CartesianValueFormatter { _, value, _ ->
    val totalSeconds = value.toLong()
    "%dm %02ds".format(totalSeconds / 60, totalSeconds % 60)
}

@Composable
private fun ChartFilterSelector(selected: ChartFilter, onSelect: (ChartFilter) -> Unit, modifier: Modifier = Modifier) {
    val options = ChartFilter.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = { Text(stringResource(option.labelRes)) },
            )
        }
    }
}

@Composable
private fun GrowthChart(
    weightPoints: List<Pair<LocalDate, Double>>,
    weightFormatter: CartesianValueFormatter,
    heightPoints: List<Pair<LocalDate, Double>>?,
    heightFormatter: CartesianValueFormatter?,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val weightColor = MaterialTheme.colorScheme.primary
    val heightColor = MaterialTheme.colorScheme.tertiary

    LaunchedEffect(weightPoints, heightPoints) {
        modelProducer.runTransaction {
            lineModel {
                series(
                    x = weightPoints.map { it.first.toEpochDay().toDouble() },
                    y = weightPoints.map { it.second },
                )
            }
            if (heightPoints != null) {
                columnModel {
                    series(
                        x = heightPoints.map { it.first.toEpochDay().toDouble() },
                        y = heightPoints.map { it.second },
                    )
                }
            }
        }
    }

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            listOf(
                LineCartesianLayer.rememberLine(
                    fill = LineCartesianLayer.LineFill.single(Fill(weightColor)),
                    pointProvider = LineCartesianLayer.PointProvider.single(
                        LineCartesianLayer.Point(ShapeComponent(Fill(weightColor), CircleShape)),
                    ),
                ),
            ),
        ),
        verticalAxisPosition = Axis.Position.Vertical.Start,
    )
    val columnLayer = if (heightPoints != null) {
        rememberColumnCartesianLayer(
            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                rememberLineComponent(fill = Fill(heightColor), thickness = 12.dp),
            ),
            verticalAxisPosition = Axis.Position.Vertical.End,
        )
    } else {
        null
    }

    val layers = listOfNotNull(lineLayer, columnLayer).toTypedArray()
    CartesianChartHost(
        chart = rememberCartesianChart(
            *layers,
            startAxis = VerticalAxis.rememberStart(valueFormatter = weightFormatter),
            endAxis = if (heightFormatter != null) VerticalAxis.rememberEnd(valueFormatter = heightFormatter) else null,
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = BottomAxisFormatter),
        ),
        modelProducer = modelProducer,
        modifier = modifier.height(200.dp),
    )
}

@Composable
private fun DualLineChart(
    primaryPoints: List<Pair<LocalDate, Double>>,
    primaryFormatter: CartesianValueFormatter,
    secondaryPoints: List<Pair<LocalDate, Double>>?,
    secondaryFormatter: CartesianValueFormatter?,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    LaunchedEffect(primaryPoints, secondaryPoints) {
        modelProducer.runTransaction {
            lineModel {
                series(
                    x = primaryPoints.map { it.first.toEpochDay().toDouble() },
                    y = primaryPoints.map { it.second },
                )
            }
            if (secondaryPoints != null) {
                lineModel {
                    series(
                        x = secondaryPoints.map { it.first.toEpochDay().toDouble() },
                        y = secondaryPoints.map { it.second },
                    )
                }
            }
        }
    }

    val primaryLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            listOf(
                LineCartesianLayer.rememberLine(
                    fill = LineCartesianLayer.LineFill.single(Fill(primaryColor)),
                    pointProvider = LineCartesianLayer.PointProvider.single(
                        LineCartesianLayer.Point(ShapeComponent(Fill(primaryColor), CircleShape)),
                    ),
                ),
            ),
        ),
        verticalAxisPosition = Axis.Position.Vertical.Start,
    )
    val secondaryLayer = if (secondaryPoints != null) {
        rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                listOf(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(secondaryColor)),
                        pointProvider = LineCartesianLayer.PointProvider.single(
                            LineCartesianLayer.Point(ShapeComponent(Fill(secondaryColor), CircleShape)),
                        ),
                    ),
                ),
            ),
            verticalAxisPosition = Axis.Position.Vertical.End,
        )
    } else {
        null
    }

    val layers = listOfNotNull(primaryLayer, secondaryLayer).toTypedArray()
    CartesianChartHost(
        chart = rememberCartesianChart(
            *layers,
            startAxis = VerticalAxis.rememberStart(valueFormatter = primaryFormatter),
            endAxis = if (secondaryFormatter != null) VerticalAxis.rememberEnd(valueFormatter = secondaryFormatter) else null,
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = BottomAxisFormatter),
        ),
        modelProducer = modelProducer,
        modifier = modifier.height(200.dp),
    )
}

@Composable
private fun Legend(items: List<Pair<Color, String>>, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (color, label) -> LegendItem(color = color, label = label) }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
