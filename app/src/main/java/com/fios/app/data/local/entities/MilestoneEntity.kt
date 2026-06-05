package com.fios.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a financial goal in the "OS" metaphor.
 * Goals are viewed as "Project Milestones" or "Storage Allocation".
 */
@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectName: String,     // Goal name
    val targetCapacity: Double,  // Target amount
    val currentCapacity: Double, // Current amount
    val priority: Int,           // Priority level (1-5)
    val deadline: Long? = null,  // Target date timestamp
    val isCompleted: Boolean = false,
    val sector: String           // e.g., "EMERGENCY_BUFFER", "HARDWARE_UPGRADE"
)
