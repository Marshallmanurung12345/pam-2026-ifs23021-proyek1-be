package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LaundryService(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var name: String,
    var description: String,
    var price: Double,
    var unit: String,
    var estimatedDays: Int,
    var image: String? = null,
    var urlImage: String = "",
    var isActive: Boolean = true,
    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)