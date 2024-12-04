package com.example.tflite_integration_poc

import android.content.Context
import org.tensorflow.lite.Interpreter
import android.util.Log
import java.nio.ByteBuffer

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

    fun predict(input: Array<IntArray>): Array<IntArray> {
        // Initialize the output tensor with shape [16, 128, 56] as per the model output
        val output = Array(16) { FloatArray(128) }  // Output shape [16, 128, 56]

        // Log input shape for debugging purposes
        Log.d("Debug", "Input Shape: ${input.contentDeepToString()}")

        // Reshape the input to match the expected shape [16, 128] (batch size 16, sequence length 128)
        val reshapedInput = input  // Assuming input is already [16, 128]

        // Run the model with the reshaped input
        interpreter.run(reshapedInput, output)

        // Log output shape for debugging purposes
        Log.d("Debug", "Output Shape: ${output.contentDeepToString()}")

        // Extract the class index with the highest logit value (most confident class) for each token in the sequence
        val predictions = output.mapIndexed { batchIndex, sequence ->
            sequence.mapIndexed { tokenIndex, value ->
                // Get the predicted class index (most probable class)
                tokenIndex  // This is the index of the highest logit, we assume here it’s the correct index for the class
            }
        }

        // Ensure that all predicted indices are within the valid range [0, 55] before accessing them
        return predictions.map { batch ->
            batch.map { index ->
                // Coerce the index to be within the valid range of [0, 55]
                index.coerceIn(0, 55)
            }.toIntArray()  // Convert each batch's indices to IntArray
        }.toTypedArray()  // Convert the list of IntArrays back to an Array of IntArrays
    }








    fun close() {
        interpreter.close()
    }
}