package com.example.xrexp.audio.positional

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class PositionalAudioControlViewModel : ViewModel() {
    private val _uiState = mutableStateOf(PositionalAudioControlUiState())
    val uiState: State<PositionalAudioControlUiState> = _uiState

    fun onAngleChanged(value: Float) {
        _uiState.value = _uiState.value.copy(angle = value)
    }

    fun onDistanceChanged(value: Float) {
        _uiState.value = _uiState.value.copy(distance = value)
    }

    fun onLoopChanged(checked: Boolean) {
        _uiState.value = _uiState.value.copy(loop = checked)
    }

    fun onPlayClicked() {
        _uiState.value = _uiState.value.copy(
            isPlaying = true,
            slidersEnabled = true,
            showDialog = true
        )
    }

    fun onStopClicked() {
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            slidersEnabled = false,
            angle = 0f,
            distance = 0f,
            showDialog = false
        )
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(showDialog = false)
    }
}