package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusRequest(
    var status: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "status" to status,
    )
}