package org.delcom.entities

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LaundryOrder(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var serviceId: String,
    var serviceName: String = "",
    var customerName: String,
    var customerPhone: String,
    var quantity: Double,
    var totalPrice: Double,
    var status: String = "pending",
    var notes: String? = null,
    var pickupDate: String? = null,
    var deliveryDate: String? = null,
    var createdAt: String = "",
    var updatedAt: String = "",
)



