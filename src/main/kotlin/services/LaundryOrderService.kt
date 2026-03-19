package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.LaundryOrderRequest
import org.delcom.data.OrderStatusRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ILaundryOrderRepository
import org.delcom.repositories.ILaundryServiceRepository
import org.delcom.repositories.IUserRepository

class LaundryOrderService(
    private val userRepo: IUserRepository,
    private val serviceRepo: ILaundryServiceRepository,
    private val orderRepo: ILaundryOrderRepository,
) {
    companion object {
        val VALID_STATUSES = listOf("pending", "processing", "done", "delivered", "cancelled")
        const val DEFAULT_LIMIT = 10
    }

    // GET /laundry-orders?search=&status=&page=1&limit=10
    suspend fun getAll(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val search = call.request.queryParameters["search"] ?: ""
        val status = call.request.queryParameters["status"]?.takeIf { it.isNotBlank() }
        val page = call.request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 50) ?: DEFAULT_LIMIT

        val orders = orderRepo.getAll(user.id, search, status, page, limit)
        val total = orderRepo.countAll(user.id, search, status)
        val totalPages = ((total + limit - 1) / limit).toInt()

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil daftar pesanan laundry",
                mapOf(
                    "laundryOrders" to orders,
                    "pagination" to mapOf(
                        "page" to page,
                        "limit" to limit,
                        "total" to total,
                        "totalPages" to totalPages,
                        "hasNext" to (page < totalPages)
                    )
                )
            )
        )
    }

    // GET /laundry-orders/{id}
    suspend fun getById(call: ApplicationCall) {
        val orderId = call.parameters["id"]
            ?: throw AppException(400, "ID pesanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val order = orderRepo.getById(orderId)
        if (order == null || order.userId != user.id) {
            throw AppException(404, "Data pesanan tidak ditemukan!")
        }

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil data pesanan",
                mapOf("laundryOrder" to order)
            )
        )
    }

    // POST /laundry-orders
    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<LaundryOrderRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("serviceId", "Layanan laundry tidak boleh kosong")
        validator.required("customerName", "Nama pelanggan tidak boleh kosong")
        validator.required("customerPhone", "Nomor telepon tidak boleh kosong")
        validator.minValue("quantity", 0.01, "Jumlah/berat harus lebih dari 0")
        validator.minValue("totalPrice", 0.01, "Total harga harus lebih dari 0")
        validator.validate()

        // Validasi service milik user
        val service = serviceRepo.getById(request.serviceId)
        if (service == null || service.userId != user.id) {
            throw AppException(404, "Layanan laundry tidak ditemukan!")
        }
        if (!service.isActive) {
            throw AppException(400, "Layanan laundry tidak aktif!")
        }

        val orderId = orderRepo.create(request.toEntity())

        call.respond(
            DataResponse(
                "success",
                "Berhasil menambahkan pesanan laundry",
                mapOf("orderId" to orderId)
            )
        )
    }

    // PUT /laundry-orders/{id}
    suspend fun put(call: ApplicationCall) {
        val orderId = call.parameters["id"]
            ?: throw AppException(400, "ID pesanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<LaundryOrderRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("serviceId", "Layanan laundry tidak boleh kosong")
        validator.required("customerName", "Nama pelanggan tidak boleh kosong")
        validator.required("customerPhone", "Nomor telepon tidak boleh kosong")
        validator.minValue("quantity", 0.01, "Jumlah/berat harus lebih dari 0")
        validator.minValue("totalPrice", 0.01, "Total harga harus lebih dari 0")
        validator.validate()

        val oldOrder = orderRepo.getById(orderId)
        if (oldOrder == null || oldOrder.userId != user.id) {
            throw AppException(404, "Data pesanan tidak ditemukan!")
        }

        // Hanya pesanan berstatus "pending" yang dapat diubah
        if (oldOrder.status !in listOf("pending")) {
            throw AppException(400, "Pesanan dengan status '${oldOrder.status}' tidak dapat diubah!")
        }

        val service = serviceRepo.getById(request.serviceId)
        if (service == null || service.userId != user.id) {
            throw AppException(404, "Layanan laundry tidak ditemukan!")
        }

        val isUpdated = orderRepo.update(user.id, orderId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data pesanan!")

        call.respond(DataResponse("success", "Berhasil mengubah data pesanan", null))
    }

    // PUT /laundry-orders/{id}/status
    suspend fun putStatus(call: ApplicationCall) {
        val orderId = call.parameters["id"]
            ?: throw AppException(400, "ID pesanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<OrderStatusRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("status", "Status tidak boleh kosong")
        validator.isIn("status", VALID_STATUSES, "Status tidak valid. Pilihan: ${VALID_STATUSES.joinToString(", ")}")
        validator.validate()

        val oldOrder = orderRepo.getById(orderId)
        if (oldOrder == null || oldOrder.userId != user.id) {
            throw AppException(404, "Data pesanan tidak ditemukan!")
        }

        val isUpdated = orderRepo.updateStatus(user.id, orderId, request.status)
        if (!isUpdated) throw AppException(400, "Gagal memperbarui status pesanan!")

        call.respond(DataResponse("success", "Berhasil mengubah status pesanan", null))
    }

    // DELETE /laundry-orders/{id}
    suspend fun delete(call: ApplicationCall) {
        val orderId = call.parameters["id"]
            ?: throw AppException(400, "ID pesanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val oldOrder = orderRepo.getById(orderId)
        if (oldOrder == null || oldOrder.userId != user.id) {
            throw AppException(404, "Data pesanan tidak ditemukan!")
        }

        val isDeleted = orderRepo.delete(user.id, orderId)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data pesanan!")

        call.respond(DataResponse("success", "Berhasil menghapus data pesanan", null))
    }
}
