package com.example.projetopdm3bim.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projetopdm3bim.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.example.projetopdm3bim.data.DayStepCount
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(viewModel: StepViewModel) {
    val history by viewModel.weeklyHistory.collectAsState()
    
    val modelProducer = remember { CartesianChartModelProducer() }
    val labelFormatter = remember { DateTimeFormatter.ofPattern("dd/MM") }

    LaunchedEffect(history) {
        val historyList = history
        if (historyList.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries {
                    series(historyList.map { (it as DayStepCount).totalSteps.toFloat() })
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = stringResource(R.string.weekly_evolution),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        val historyList = history
        if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_data), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 8.dp,
                                shape = CorneredShape.rounded(allPercent = 40),
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, v, _ ->
                            val index = v.toInt()
                            if (index >= 0 && index < historyList.size) {
                                try {
                                    val dateStr = (historyList[index] as DayStepCount).day
                                    val date = LocalDate.parse(dateStr)
                                    date.format(labelFormatter)
                                } catch (e: Exception) {
                                    ""
                                }
                            } else ""
                        }
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = stringResource(R.string.daily_stats), style = MaterialTheme.typography.titleLarge)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            historyList.filter { it.totalSteps > 0 }.reversed().forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val date = try {
                        val dateStr = (item as DayStepCount).day
                        LocalDate.parse(dateStr).format(labelFormatter)
                    } catch (e: Exception) {
                        (item as DayStepCount).day
                    }
                    Text(text = date)
                    Text(
                        text = "${(item as DayStepCount).totalSteps} %s".format(stringResource(R.string.steps_unit)),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
