package com.fios.app.domain.model

data class Milestone(
    val id: Long = 0,
    val projectName: String,
    val targetCapacity: Double,
    val currentCapacity: Double,
    val priority: Int,
    val deadline: Long? = null,
    val isCompleted: Boolean = false,
    val sector: String
)
