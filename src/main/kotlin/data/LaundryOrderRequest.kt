package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.LaundryOrder

@Serializable
data class LaundryOrderRequest(
    var userId: String = "",
    var serviceId: String = "",
    var customerName: String = "",
    var customerPhone: String = "",
    var quantity: Double = 0.0,
    var totalPrice: Double = 0.0,
    var status: String = "pending",
    var notes: String? = null,
    var pickupDate: String? = null,
    var deliveryDate: String? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "serviceId" to serviceId,
        "customerName" to customerName,
        "customerPhone" to customerPhone,
        "quantity" to quantity,
        "totalPrice" to totalPrice,
        "status" to status,
    )

    fun toEntity(): LaundryOrder = LaundryOrder(
        userId = userId,
        serviceId = serviceId,
        customerName = customerName,
        customerPhone = customerPhone,
        quantity = quantity,
        totalPrice = totalPrice,
        status = status,
        notes = notes,
        pickupDate = pickupDate,
        deliveryDate = deliveryDate,
    )
}