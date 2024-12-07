package com.example.tflite_integration_poc

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

class TFLiteInterpreter(context: Context, modelPath: String) {
    private val interpreter: Interpreter

    init {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val mappedByteBuffer = fileInputStream.channel.map(
            java.nio.channels.FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        interpreter = Interpreter(mappedByteBuffer)
    }

    fun predict(inputIds: Array<IntArray>, attentionMask: Array<IntArray>): Array<IntArray> {
        Log.d("TFLiteInterpreter", "Starting prediction...")

        val batchSize = 1
        val sequenceLength = 128  // Expected sequence length
        val numClasses = 56 // Adjust this to your model's output classes

        val inputBuffer = ByteBuffer.allocateDirect(batchSize * sequenceLength * 4)
            .order(ByteOrder.nativeOrder())

        for (i in inputIds[0]) {
            inputBuffer.putInt(i)  // Populate input_ids buffer
        }

        val attentionMaskBuffer = ByteBuffer.allocateDirect(batchSize * sequenceLength * 4)
            .order(ByteOrder.nativeOrder())

        for (i in attentionMask[0]) {
            attentionMaskBuffer.putInt(i)  // Populate attention_mask buffer
        }

        inputBuffer.rewind()
        attentionMaskBuffer.rewind()

        val outputBuffer = ByteBuffer.allocateDirect(batchSize * sequenceLength * numClasses * 4)
            .order(ByteOrder.nativeOrder())

        try {
            Log.d("TFLiteInterpreter", "Running inference...")
            interpreter.run(inputBuffer, outputBuffer)

            // Log the raw output (logits or raw scores)
            val rawOutput = Array(batchSize) { FloatArray(sequenceLength * numClasses) }
            outputBuffer.rewind()
            for (i in 0 until batchSize) {
                for (j in 0 until sequenceLength * numClasses) {
                    rawOutput[i][j] = outputBuffer.getFloat()  // Get raw logits
                }
            }

            // Log the raw output (for debugging purposes)
            Log.d("TFLiteInterpreter", "Raw Output: ${rawOutput.contentDeepToString()}")

            // Process logits to final predictions (e.g., applying argmax)
            val output = Array(batchSize) { IntArray(sequenceLength) }
            for (i in 0 until batchSize) {
                for (j in 0 until sequenceLength) {
                    // Get the slice of the logits for the current token
                    val logitsSlice = rawOutput[i].sliceArray(j * numClasses until (j + 1) * numClasses)

                    // Find the index of the max logit (argmax)
                    val maxIndex = logitsSlice.indices.maxByOrNull { logitsSlice[it] } ?: 0
                    output[i][j] = maxIndex
                }
            }

            Log.d("TFLiteInterpreter", "Inference complete. Processed Output: ${output.contentDeepToString()}")

            return output
        } catch (e: Exception) {
            Log.e("TFLiteInterpreter", "Error running inference: ${e.message}")
            e.printStackTrace()
            return Array(batchSize) { IntArray(sequenceLength) }
        }
    }

    fun close() {
        interpreter.close()
    }
}
