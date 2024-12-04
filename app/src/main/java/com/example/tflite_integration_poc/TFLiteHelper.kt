package com.example.tflite_integration_poc

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteHelper(context: Context) {

    private var interpreter: Interpreter

    init {
        try {
            // Load the model from the assets
            interpreter = Interpreter(loadModelFile(context, "sroie2019v1.tflite"))
            println("Model loaded successfully")
        } catch (e: Exception) {
            println("Error loading model: ${e.message}")
            throw RuntimeException("Error loading the TensorFlow Lite model: ${e.message}")
        }
    }

    // Load the model file from assets folder
    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        try {
            val assetFileDescriptor = context.assets.openFd(modelName)
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: Exception) {
            println("Error loading model file: $modelName")
            throw RuntimeException("Error loading model file: $modelName", e)
        }
    }

    // Preprocess the input text for the model
    fun preprocessInput(text: String): ByteBuffer {
        // Tokenize the input text
        val tokens = text.split(" ")

        // Creating a ByteBuffer with the expected size (size may vary based on model)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 128) // 128 tokens, 4 bytes per token
        byteBuffer.order(ByteOrder.nativeOrder())

        // Add tokens to the ByteBuffer
        tokens.take(128).forEach { token ->
            byteBuffer.putInt(token.hashCode())
        }

        // Fill remaining space with 0s (if tokens are less than 128)
        for (i in tokens.size until 128) {
            byteBuffer.putInt(0)
        }

        return byteBuffer
    }

    // Run the inference on the input text
    fun runInference(inputText: String): String {
        // Preprocess input text
        val inputTensor = preprocessInput(inputText)

        // Create an output tensor for inference
        val outputTensor = Array(1) { IntArray(128) } // Adjust size based on model output

        // Run the model
        interpreter.run(inputTensor, outputTensor)

        // Return the result as a string
        val result = outputTensor[0].joinToString(", ") { it.toString() }
        println("Model output: $result")
        return result
    }

    fun close() {
        interpreter.close()
    }
}
