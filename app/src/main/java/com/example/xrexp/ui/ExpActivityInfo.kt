package com.example.xrexp.ui

data class ExpActivityInfo(
    val activityClass: Class<*>,
    val description: String,
    val isFullSpace: Boolean = false // by default application starts in Home space, check AndroidManifest property
)