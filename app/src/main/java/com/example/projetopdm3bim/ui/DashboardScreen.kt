package com.example.projetopdm3bim.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetopdm3bim.R

@Composable
fun DashboardScreen(viewModel: StepViewModel) {
    val steps by viewModel.stepsToday.collectAsState()
    val spm by viewModel.spm.collectAsState()
    val dist by viewModel.distance.collectAsState()
    val cal by viewModel.calories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.steps_today),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$steps",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricCard(label = stringResource(R.string.distance), value = "%.2f %s".format(dist, stringResource(R.string.km_unit)))
            MetricCard(label = stringResource(R.string.calories), value = "%.0f %s".format(cal, stringResource(R.string.kcal_unit)))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.training_mode), fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${spm.toInt()} %s".format(stringResource(R.string.spm_unit)),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(R.string.spm_label), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String) {
    Card(
        modifier = Modifier.width(150.dp).padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, fontWeight = FontWeight.Bold)
        }
    }
}
