package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.LaundryService

@Serializable
data class LaundryServiceRequest(
    var userId: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var unit: String = "",
    var estimatedDays: Int = 1,
    var image: String? = null,
    var isActive: Boolean = true,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "description" to description,
        "price" to price,
        "unit" to unit,
        "estimatedDays" to estimatedDays,
        "isActive" to isActive,
    )

    fun toEntity(): LaundryService = LaundryService(
        userId = userId,
        name = name,
        description = description,
        price = price,
        unit = unit,
        estimatedDays = estimatedDays,
        image = image,
        isActive = isActive,
        updatedAt = Clock.System.now()
    )
}