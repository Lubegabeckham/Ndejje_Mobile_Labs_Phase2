package com.nedejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nedejje.momocalc.ui.theme.MoMoCalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoMoCalculatorAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MoMoCalcScreen()
                }
            }
        }
    }
}

@Composable
fun HoistedAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            isError = isError,
            label = { Text(stringResource(R.string.enter_amount)) },
            trailingIcon = {
                if (amount.isNotEmpty()) {
                    IconButton(onClick = { onAmountChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = stringResource(R.string.error_numbers_only),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun MoMoCalcScreen() {
    var amountInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val numericAmount = amountInput.toDoubleOrNull()
    val isError = amountInput.isNotEmpty() && numericAmount == null
    val fee = (numericAmount ?: 0.0) * 0.03
    val totalAmount = (numericAmount ?: 0.0) + fee
    
    val formattedFee = "UGX %,.0f".format(fee)
    val formattedTotal = "UGX %,.0f".format(totalAmount)

    val quickAmounts = listOf("5000", "10000", "20000", "50000", "100000")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Withdrawal Fee",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = formattedFee,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        item {
            HoistedAmountInput(
                amount = amountInput,
                onAmountChange = { amountInput = it },
                isError = isError
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Select",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 4.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickAmounts) { amount ->
                        AssistChip(
                            onClick = {
                                amountInput = amount
                                focusManager.clearFocus()
                            },
                            label = { Text("UGX %,.0f".format(amount.toDouble())) }
                        )
                    }
                }
            }
        }

        if (numericAmount != null && numericAmount > 0) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total to Pay:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoMoCalcPreview() {
    MoMoCalculatorAppTheme {
        MoMoCalcScreen()
    }
}
