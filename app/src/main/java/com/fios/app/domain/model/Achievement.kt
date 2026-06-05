package com.fios.app.domain.model

data class Achievement(
    val id: Long = 0,
    val title: String,
    val description: String,
    val unlocked: Boolean = false,
    val iconCode: String
)
