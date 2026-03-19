package org.delcom.dao

import org.delcom.tables.LaundryOrderTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class LaundryOrderDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, LaundryOrderDAO>(LaundryOrderTable)

    var userId by LaundryOrderTable.userId
    var serviceId by LaundryOrderTable.serviceId
    var customerName by LaundryOrderTable.customerName
    var customerPhone by LaundryOrderTable.customerPhone
    var quantity by LaundryOrderTable.quantity
    var totalPrice by LaundryOrderTable.totalPrice
    var status by LaundryOrderTable.status
    var notes by LaundryOrderTable.notes
    var pickupDate by LaundryOrderTable.pickupDate
    var deliveryDate by LaundryOrderTable.deliveryDate
    var createdAt by LaundryOrderTable.createdAt
    var updatedAt by LaundryOrderTable.updatedAt
}
