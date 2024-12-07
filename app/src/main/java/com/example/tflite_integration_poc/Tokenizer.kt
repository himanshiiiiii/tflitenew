package com.example.tflite_integration_poc

import android.content.Context
import android.util.Log
import java.io.InputStreamReader
import java.nio.charset.Charset

class Tokenizer(context: Context, vocabFile: String) {

    private val vocab: Map<String, Int>
    private val invVocab: Map<Int, String>
    private val unkToken = "[UNK]"
    private val padToken = "[PAD]"
    private val clsToken = "[CLS]"
    private val sepToken = "[SEP]"
    private val maxSequenceLength = 128

    init {
        // Load vocab file (usually from the assets folder)
        val vocabStream = context.assets.open(vocabFile)
        val vocabList = InputStreamReader(vocabStream, Charset.forName("UTF-8")).readLines().map { it.trim() }

        // Map each token to an integer index and also create the inverse vocabulary
        vocab = vocabList.mapIndexed { index, token -> token to index }.toMap()
        invVocab = vocab.entries.associateBy({ it.value }, { it.key })
    }

    // Tokenizes the input text into input IDs and creates an attention mask
    fun tokenize(inputText: String): Pair<Array<IntArray>, Array<IntArray>> {
        val tokens = mutableListOf<String>()
        val attentionMask = mutableListOf<Int>()

        // Add the [CLS] token at the beginning and [SEP] token at the end
        tokens.add(clsToken)
        attentionMask.add(1)

        // Tokenize input text and add each token
        val tokenized = inputText.split(" ")  // Simple split by spaces
        for (word in tokenized) {
            val token = vocab.keys.find { it.equals(word, ignoreCase = true) } ?: unkToken
            tokens.add(token)
            attentionMask.add(1)
        }

        // Add the [SEP] token at the end
        tokens.add(sepToken)
        attentionMask.add(1)

        // Padding
        while (tokens.size < maxSequenceLength) {
            tokens.add(padToken)
            attentionMask.add(0)  // Padding tokens should be ignored by the model
        }

        // Convert tokens to IDs
        val inputIds = tokens.map { vocab[it] ?: vocab[unkToken]!! }.toIntArray()
        val attentionMaskArray = attentionMask.toIntArray()

        // Create a batch of size 1
        val batch = Array(1) { inputIds }
        val attentionMaskBatch = Array(1) { attentionMaskArray }

        // Log the tokenized and padded sequences
        Log.d("Tokenizer", "Tokenized Input IDs: ${batch.contentDeepToString()}")
        Log.d("Tokenizer", "Tokenized Attention Mask: ${attentionMaskBatch.contentDeepToString()}")

        return Pair(batch, attentionMaskBatch)
    }

    // Convert token IDs to tokens
    fun convertIdToToken(tokenId: Int): String {
        return invVocab[tokenId] ?: "[UNK]"  // Use the inverse vocabulary to convert ID to token
    }
}
