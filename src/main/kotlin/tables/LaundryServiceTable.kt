package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object LaundryServiceTable : UUIDTable("laundry_services") {
    val userId = uuid("user_id")
    val name = varchar("name", 100)
    val description = text("description")
    val price = decimal("price", 10, 2)
    val unit = varchar("unit", 50)
    val estimatedDays = integer("estimated_days")
    val image = text("image").nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
