package org.delcom.repositories

import org.delcom.entities.LaundryOrder

interface ILaundryOrderRepository {
    suspend fun getAll(userId: String, search: String, status: String?, page: Int, limit: Int): List<LaundryOrder>
    suspend fun countAll(userId: String, search: String, status: String?): Long
    suspend fun getById(orderId: String): LaundryOrder?
    suspend fun create(order: LaundryOrder): String
    suspend fun update(userId: String, orderId: String, newOrder: LaundryOrder): Boolean
    suspend fun updateStatus(userId: String, orderId: String, status: String): Boolean
    suspend fun delete(userId: String, orderId: String): Boolean
}
