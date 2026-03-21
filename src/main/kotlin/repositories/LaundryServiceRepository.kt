package org.delcom.repositories

import org.delcom.dao.LaundryServiceDAO
import org.delcom.entities.LaundryService
import org.delcom.helpers.laundryServiceDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.LaundryServiceTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.*

class LaundryServiceRepository(private val baseUrl: String) : ILaundryServiceRepository {

    override suspend fun getAll(userId: String, search: String, isActive: Boolean?): List<LaundryService> = suspendTransaction {
        val userUUID = UUID.fromString(userId)
        val baseCondition = if (isActive != null) {
            (LaundryServiceTable.userId eq userUUID) and (LaundryServiceTable.isActive eq isActive)
        } else {
            LaundryServiceTable.userId eq userUUID
        }

        if (search.isBlank()) {
            LaundryServiceDAO.find { baseCondition }
                .orderBy(LaundryServiceTable.createdAt to SortOrder.DESC)
                .map { laundryServiceDAOToModel(it, baseUrl) }
        } else {
            val keyword = "%${search.lowercase()}%"
            LaundryServiceDAO.find { baseCondition and (LaundryServiceTable.name.lowerCase() like keyword) }
                .orderBy(LaundryServiceTable.name to SortOrder.ASC)
                .map { laundryServiceDAOToModel(it, baseUrl) }
        }
    }

    override suspend fun getById(serviceId: String): LaundryService? = suspendTransaction {
        LaundryServiceDAO.find { LaundryServiceTable.id eq UUID.fromString(serviceId) }
            .limit(1).map { laundryServiceDAOToModel(it, baseUrl) }.firstOrNull()
    }

    override suspend fun create(service: LaundryService): String = suspendTransaction {
        val now = kotlinx.datetime.Clock.System.now()
        val dao = LaundryServiceDAO.new {
            userId = UUID.fromString(service.userId)
            name = service.name
            description = service.description
            price = service.price.toBigDecimal()
            unit = service.unit
            estimatedDays = service.estimatedDays
            image = service.image
            isActive = service.isActive
            createdAt = now
            updatedAt = now
        }
        dao.id.value.toString()
    }

    override suspend fun update(userId: String, serviceId: String, newService: LaundryService): Boolean = suspendTransaction {
        val dao = LaundryServiceDAO.find {
            (LaundryServiceTable.id eq UUID.fromString(serviceId)) and
                    (LaundryServiceTable.userId eq UUID.fromString(userId))
        }.limit(1).firstOrNull()

        if (dao != null) {
            dao.name = newService.name
            dao.description = newService.description
            dao.price = newService.price.toBigDecimal()
            dao.unit = newService.unit
            dao.estimatedDays = newService.estimatedDays
            dao.image = newService.image
            dao.isActive = newService.isActive
            dao.updatedAt = kotlinx.datetime.Clock.System.now()
            true
        } else false
    }

    override suspend fun delete(userId: String, serviceId: String): Boolean = suspendTransaction {
        val rows = LaundryServiceTable.deleteWhere {
            (LaundryServiceTable.id eq UUID.fromString(serviceId)) and
                    (LaundryServiceTable.userId eq UUID.fromString(userId))
        }
        rows >= 1
    }
}