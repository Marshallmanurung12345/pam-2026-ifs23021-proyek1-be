package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.LaundryOrderDAO
import org.delcom.dao.LaundryServiceDAO
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.UserDAO
import org.delcom.entities.LaundryOrder
import org.delcom.entities.LaundryService
import org.delcom.entities.RefreshToken
import org.delcom.entities.User
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDAOToModel(dao: UserDAO, baseUrl: String) = User(
    id = dao.id.value.toString(),
    name = dao.name,
    username = dao.username,
    password = dao.password,
    photo = dao.photo,
    urlPhoto = buildImageUrl(baseUrl, dao.photo ?: "/uploads/defaults/user.png"),
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    id = dao.id.value.toString(),
    userId = dao.userId.toString(),
    refreshToken = dao.refreshToken,
    authToken = dao.authToken,
    createdAt = dao.createdAt,
)

fun laundryServiceDAOToModel(dao: LaundryServiceDAO, baseUrl: String) = LaundryService(
    id = dao.id.value.toString(),
    userId = dao.userId.toString(),
    name = dao.name,
    description = dao.description,
    price = dao.price.toDouble(),
    unit = dao.unit,
    estimatedDays = dao.estimatedDays,
    image = dao.image,
    urlImage = buildImageUrl(baseUrl, dao.image ?: "/uploads/defaults/service.png"),
    isActive = dao.isActive,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

fun laundryOrderDAOToModel(dao: LaundryOrderDAO, serviceName: String = "") = LaundryOrder(
    id = dao.id.value.toString(),
    userId = dao.userId.toString(),
    serviceId = dao.serviceId.toString(),
    serviceName = serviceName,
    customerName = dao.customerName,
    customerPhone = dao.customerPhone,
    quantity = dao.quantity.toDouble(),
    totalPrice = dao.totalPrice.toDouble(),
    status = dao.status,
    notes = dao.notes,
    pickupDate = dao.pickupDate,
    deliveryDate = dao.deliveryDate,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

fun buildImageUrl(baseUrl: String, pathGambar: String): String {
    val relativePath = pathGambar.removePrefix("uploads/")
    return "$baseUrl/static/$relativePath"
}
