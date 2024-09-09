package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayWord = getUnscrambledWord(currentGameUiState.currentScrambleWord)

        viewModel.updateUserGuess(correctPlayWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value

        assertFalse(currentGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet(){
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value

        assertEquals(0, currentGameUiState.score)

        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded(){
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameUiState.currentScrambleWord)

        assertNotEquals(unscrambledWord, gameUiState.currentScrambleWord)

        assertTrue(gameUiState.score==0)
        assertTrue(gameUiState.currentWordCount == 1)

        assertFalse(gameUiState.isGuessedWordWrong)
        assertFalse(gameUiState.isGameOver)

    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0;

        var currentGameUiState = viewModel.uiState.value

        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambleWord)

        repeat(MAX_NO_OF_WORDS){
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()

            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambleWord)

            assertEquals(expectedScore, currentGameUiState.score)
        }

        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedWordCountIncreased(){
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambleWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        assertEquals(lastWordCount+1, currentGameUiState.currentWordCount)
    }
}