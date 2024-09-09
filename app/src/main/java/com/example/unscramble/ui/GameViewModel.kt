package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWord: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()

        if (usedWord.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWord.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()

        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }

        return String(tempWord)
    }

    fun resetGame() {
        usedWord.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updateScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWord.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }

        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambleWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    init {
        resetGame()
    }
}

