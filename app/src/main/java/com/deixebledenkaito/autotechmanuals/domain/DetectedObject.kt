package com.deixebledenkaito.autotechmanuals.domain

data class DetectedObject(
    val label: String,
    val dimensions: Dimensions,
    val confidence: Double,
    val centerX: Double,
    val centerY: Double,
    val width: Double,
    val height: Double
)