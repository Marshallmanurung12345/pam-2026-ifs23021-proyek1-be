package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
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

    @Contextual
    var pickupDate: Instant? = null,
    @Contextual
    var deliveryDate: Instant? = null,
    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)
