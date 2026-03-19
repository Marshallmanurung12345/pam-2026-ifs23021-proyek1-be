package org.delcom.repositories

import org.delcom.entities.LaundryService

interface ILaundryServiceRepository {
    suspend fun getAll(userId: String, search: String, isActive: Boolean?): List<LaundryService>
    suspend fun getById(serviceId: String): LaundryService?
    suspend fun create(service: LaundryService): String
    suspend fun update(userId: String, serviceId: String, newService: LaundryService): Boolean
    suspend fun delete(userId: String, serviceId: String): Boolean
}
