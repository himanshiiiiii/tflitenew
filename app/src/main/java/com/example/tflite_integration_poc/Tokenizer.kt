package com.example.tflite_integration_poc

class Tokenizer {
    // Tokenize and pad/truncate input text to the expected shape [batch_size, sequence_length]
    fun tokenize(inputText: String): Array<IntArray> {
        val maxLength = 128  // Expected sequence length
        val batchSize = 16    // Expected batch size

        // Example tokenization - splitting by spaces and taking word lengths
        val tokens = inputText.split(" ").map { it.length }

        // Truncate or pad the tokenized sequence to match the max length
        val paddedTokens = tokens.take(maxLength).toMutableList()
        while (paddedTokens.size < maxLength) {
            paddedTokens.add(0)  // Padding with 0 (padding value)
        }

        // Create a batch of size 16 (repeat the same sequence for all batch entries)
        val batch = Array(batchSize) { paddedTokens.toIntArray() }

        return batch  // Return the batch of sequences
    }
}