package com.example.projetopdm3bim.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projetopdm3bim.R

@Composable
fun ProfileScreen(viewModel: StepViewModel) {
    val h by viewModel.height.collectAsState()
    val w by viewModel.weight.collectAsState()
    val s by viewModel.stride.collectAsState()

    var heightText by remember(h) { mutableStateOf(h.toString()) }
    var weightText by remember(w) { mutableStateOf(w.toString()) }
    var strideText by remember(s) { mutableStateOf(s.toString()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.user_profile), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = heightText,
            onValueChange = { heightText = it },
            label = { Text(stringResource(R.string.height_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text(stringResource(R.string.weight_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = strideText,
            onValueChange = { strideText = it },
            label = { Text(stringResource(R.string.stride_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val height = heightText.toFloatOrNull() ?: 170f
                val weight = weightText.toFloatOrNull() ?: 70f
                val strideValue = strideText.toFloatOrNull() ?: 0.7f
                viewModel.saveProfile(height, weight, strideValue)
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_profile))
        }
    }
}
