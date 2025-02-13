package com.example.xrexp.ui

data class ExpActivityInfo(
    val activityClass: Class<*>,
    val name: String,
    val description: String,
    val isHomeSpace: Boolean = true
)