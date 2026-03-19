package org.delcom.dao

import org.delcom.tables.LaundryServiceTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class LaundryServiceDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, LaundryServiceDAO>(LaundryServiceTable)

    var userId by LaundryServiceTable.userId
    var name by LaundryServiceTable.name
    var description by LaundryServiceTable.description
    var price by LaundryServiceTable.price
    var unit by LaundryServiceTable.unit
    var estimatedDays by LaundryServiceTable.estimatedDays
    var image by LaundryServiceTable.image
    var isActive by LaundryServiceTable.isActive
    var createdAt by LaundryServiceTable.createdAt
    var updatedAt by LaundryServiceTable.updatedAt
}
