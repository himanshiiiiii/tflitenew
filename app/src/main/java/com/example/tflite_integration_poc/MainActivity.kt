package com.example.tflite_integration_poc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private lateinit var tfliteInterpreter: TFLiteInterpreter
    private lateinit var tokenizer: Tokenizer
    private val id2label = mapOf(
        0 to "O",
        1 to "B-INVOICE_NUMBER",
        2 to "I-INVOICE_NUMBER",
        3 to "B-INVOICE_DATE",
        4 to "I-INVOICE_DATE",
        5 to "B-DUE_DATE",
        6 to "I-DUE_DATE",
        7 to "B-CUSTOMER_PO",
        8 to "I-CUSTOMER_PO",
        9 to "B-VENDOR_NAME",
        10 to "I-VENDOR_NAME",
        11 to "B-VENDOR_ADDRESS",
        12 to "I-VENDOR_ADDRESS",
        13 to "B-VENDOR_PHONE",
        14 to "I-VENDOR_PHONE",
        15 to "B-VENDOR_EMAIL",
        16 to "I-VENDOR_EMAIL",
        17 to "B-VENDOR_WEBSITE",
        18 to "I-VENDOR_WEBSITE",
        19 to "B-CUSTOMER_NAME",
        20 to "I-CUSTOMER_NAME",
        21 to "B-CUSTOMER_ADDRESS",
        22 to "I-CUSTOMER_ADDRESS",
        23 to "B-CUSTOMER_PHONE",
        24 to "I-CUSTOMER_PHONE",
        25 to "B-CUSTOMER_EMAIL",
        26 to "I-CUSTOMER_EMAIL",
        27 to "B-ITEM_DESCRIPTION",
        28 to "I-ITEM_DESCRIPTION",
        29 to "B-QUANTITY",
        30 to "B-UNIT_PRICE",
        31 to "B-TOTAL_PRICE",
        32 to "B-SUBTOTAL",
        33 to "I-SUBTOTAL",
        34 to "B-TAX_AMOUNT",
        35 to "I-TAX_AMOUNT",
        36 to "B-TOTAL_AMOUNT_DUE",
        37 to "I-TOTAL_AMOUNT_DUE",
        38 to "B-PAYMENT_TERMS",
        39 to "I-PAYMENT_TERMS",
        40 to "B-PAYMENT_METHOD",
        41 to "I-PAYMENT_METHOD",
        42 to "B-BANK_DETAILS",
        43 to "I-BANK_DETAILS",
        44 to "B-NOTES",
        45 to "I-NOTES",
        46 to "B-GST",
        47 to "B-GSTIN",
        48 to "I-GSTIN",
        49 to "B-CGST",
        50 to "B-SGST",
        51 to "B-IGST",
        52 to "B-AMOUNT",
        53 to "I-AMOUNT",
        54 to "B-SUB-TOTAL",
        55 to "I-SUB-TOTAL"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the interpreter and tokenizer
        tfliteInterpreter = TFLiteInterpreter(this, "sroie2019v1new.tflite")
        tokenizer = Tokenizer(this, "vocab.txt") // The vocab file should be in assets folder

        setContent {
            var inputText by remember { mutableStateOf("") }
            var predictions by remember { mutableStateOf("") }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Enter Input Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Tokenize input text
                        val (inputIds, attentionMask) = tokenizer.tokenize(inputText)

                        // Log input details
                        Log.d("MainActivity", "Tokenized Input IDs: ${inputIds.contentDeepToString()}")
                        Log.d("MainActivity", "Tokenized Attention Mask: ${attentionMask.contentDeepToString()}")

                        // Run the model with the tokenized input
                        val modelOutput = tfliteInterpreter.predict(inputIds, attentionMask)

                        // Map output to labels
                        predictions = modelOutput[0]
                            .zip(inputIds[0]) // Pair output indices with input tokens
                            .joinToString("\n") { (labelId, tokenId) ->
                                val token = tokenizer.convertIdToToken(tokenId)
                                val label = id2label[labelId] ?: "UNKNOWN"
                                "$token\t$label"
                            }

                        // Log the output
                        Log.d("MainActivity", "Predictions: $predictions")

                    }) {
                        Text("Run Model")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Predictions:\n$predictions")
                }
            }
        }
    }

    override fun onDestroy() {
        tfliteInterpreter.close()
        super.onDestroy()
    }
}
