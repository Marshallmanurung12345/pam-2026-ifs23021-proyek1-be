package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object LaundryOrderTable : UUIDTable("laundry_orders") {
    val userId = uuid("user_id")
    val serviceId = uuid("service_id")
    val customerName = varchar("customer_name", 100)
    val customerPhone = varchar("customer_phone", 20)
    val quantity = decimal("quantity", 10, 2)
    val totalPrice = decimal("total_price", 10, 2)
    val status = varchar("status", 30).default("pending")
    val notes = text("notes").nullable()
    val pickupDate = timestamp("pickup_date").nullable()
    val deliveryDate = timestamp("delivery_date").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
