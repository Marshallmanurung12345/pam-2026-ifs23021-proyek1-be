package org.delcom.repositories

// Tambahkan baris ini setelah import SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.delcom.dao.LaundryOrderDAO
import org.delcom.dao.LaundryServiceDAO
import org.delcom.entities.LaundryOrder
import org.delcom.helpers.laundryOrderDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.LaundryOrderTable
import org.delcom.tables.LaundryServiceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class LaundryOrderRepository : ILaundryOrderRepository {

    private fun buildCondition(userId: String, search: String, status: String?): Op<Boolean> {
        val userUUID = UUID.fromString(userId)
        var condition: Op<Boolean> = LaundryOrderTable.userId eq userUUID

        if (status != null && status.isNotBlank()) {
            condition = condition and (LaundryOrderTable.status eq status)
        }

        if (search.isNotBlank()) {
            val keyword = "%${search.lowercase()}%"
            condition = condition and (LaundryOrderTable.customerName.lowerCase() like keyword)
        }

        return condition
    }

    override suspend fun getAll(
        userId: String,
        search: String,
        status: String?,
        page: Int,
        limit: Int
    ): List<LaundryOrder> = suspendTransaction {
        val condition = buildCondition(userId, search, status)
        val offset = ((page - 1) * limit).toLong()

        LaundryOrderDAO
            .find { condition }
            .orderBy(LaundryOrderTable.createdAt to SortOrder.DESC)
            // SESUDAH (ganti dengan ini):
            .limit(limit)
            .offset(offset)
            .map { orderDAO ->
                val serviceName = LaundryServiceDAO
                    .find { LaundryServiceTable.id eq orderDAO.serviceId }
                    .firstOrNull()?.name ?: ""
                laundryOrderDAOToModel(orderDAO, serviceName)
            }
    }

    override suspend fun countAll(userId: String, search: String, status: String?): Long = suspendTransaction {
        val condition = buildCondition(userId, search, status)
        LaundryOrderDAO.find { condition }.count()
    }

    override suspend fun getById(orderId: String): LaundryOrder? = suspendTransaction {
        val orderDAO = LaundryOrderDAO
            .find { LaundryOrderTable.id eq UUID.fromString(orderId) }
            .limit(1)
            .firstOrNull() ?: return@suspendTransaction null

        val serviceName = LaundryServiceDAO
            .find { LaundryServiceTable.id eq orderDAO.serviceId }
            .firstOrNull()?.name ?: ""

        laundryOrderDAOToModel(orderDAO, serviceName)
    }

    override suspend fun create(order: LaundryOrder): String = suspendTransaction {
        val dao = LaundryOrderDAO.new {
            userId = UUID.fromString(order.userId)
            serviceId = UUID.fromString(order.serviceId)
            customerName = order.customerName
            customerPhone = order.customerPhone
            quantity = order.quantity.toBigDecimal()
            totalPrice = order.totalPrice.toBigDecimal()
            status = order.status
            notes = order.notes
            pickupDate = order.pickupDate
            deliveryDate = order.deliveryDate
            createdAt = order.createdAt
            updatedAt = order.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun update(userId: String, orderId: String, newOrder: LaundryOrder): Boolean = suspendTransaction {
        val dao = LaundryOrderDAO
            .find {
                (LaundryOrderTable.id eq UUID.fromString(orderId)) and
                (LaundryOrderTable.userId eq UUID.fromString(userId))
            }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.serviceId = UUID.fromString(newOrder.serviceId)
            dao.customerName = newOrder.customerName
            dao.customerPhone = newOrder.customerPhone
            dao.quantity = newOrder.quantity.toBigDecimal()
            dao.totalPrice = newOrder.totalPrice.toBigDecimal()
            dao.status = newOrder.status
            dao.notes = newOrder.notes
            dao.pickupDate = newOrder.pickupDate
            dao.deliveryDate = newOrder.deliveryDate
            dao.updatedAt = newOrder.updatedAt
            true
        } else false
    }

    override suspend fun updateStatus(userId: String, orderId: String, status: String): Boolean = suspendTransaction {
        val dao = LaundryOrderDAO
            .find {
                (LaundryOrderTable.id eq UUID.fromString(orderId)) and
                (LaundryOrderTable.userId eq UUID.fromString(userId))
            }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.status = status
            dao.updatedAt = kotlinx.datetime.Clock.System.now()
            true
        } else false
    }

    override suspend fun delete(userId: String, orderId: String): Boolean = suspendTransaction {
        val rows = LaundryOrderTable.deleteWhere {
            (LaundryOrderTable.id eq UUID.fromString(orderId)) and
            (LaundryOrderTable.userId eq UUID.fromString(userId))
        }
        rows >= 1
    }
}
