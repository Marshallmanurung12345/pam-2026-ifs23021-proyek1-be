package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.LaundryServiceRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ILaundryServiceRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

class LaundryServiceService(
    private val userRepo: IUserRepository,
    private val serviceRepo: ILaundryServiceRepository,
) {
    // GET /laundry-services?search=&isActive=
    suspend fun getAll(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val search = call.request.queryParameters["search"] ?: ""
        val isActiveParam = call.request.queryParameters["isActive"]
        val isActive: Boolean? = when (isActiveParam) {
            "true" -> true
            "false" -> false
            else -> null
        }

        val services = serviceRepo.getAll(user.id, search, isActive)

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil daftar layanan laundry",
                mapOf("laundryServices" to services)
            )
        )
    }

    // GET /laundry-services/{id}
    suspend fun getById(call: ApplicationCall) {
        val serviceId = call.parameters["id"]
            ?: throw AppException(400, "ID layanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val service = serviceRepo.getById(serviceId)
        if (service == null || service.userId != user.id) {
            throw AppException(404, "Data layanan tidak ditemukan!")
        }

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil data layanan",
                mapOf("laundryService" to service)
            )
        )
    }

    // POST /laundry-services
    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<LaundryServiceRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama layanan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.minValue("price", 0.01, "Harga harus lebih dari 0")
        validator.required("unit", "Satuan tidak boleh kosong")
        validator.validate()

        val serviceId = serviceRepo.create(request.toEntity())

        call.respond(
            DataResponse(
                "success",
                "Berhasil menambahkan layanan laundry",
                mapOf("serviceId" to serviceId)
            )
        )
    }

    // PUT /laundry-services/{id}
    suspend fun put(call: ApplicationCall) {
        val serviceId = call.parameters["id"]
            ?: throw AppException(400, "ID layanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<LaundryServiceRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama layanan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.minValue("price", 0.01, "Harga harus lebih dari 0")
        validator.required("unit", "Satuan tidak boleh kosong")
        validator.validate()

        val oldService = serviceRepo.getById(serviceId)
        if (oldService == null || oldService.userId != user.id) {
            throw AppException(404, "Data layanan tidak ditemukan!")
        }
        // Pertahankan gambar lama jika tidak di-update
        request.image = oldService.image

        val isUpdated = serviceRepo.update(user.id, serviceId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data layanan!")

        call.respond(DataResponse("success", "Berhasil mengubah data layanan", null))
    }

    // PUT /laundry-services/{id}/image
    suspend fun putImage(call: ApplicationCall) {
        val serviceId = call.parameters["id"]
            ?: throw AppException(400, "ID layanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        var newImage: String? = null
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/laundry-services/$fileName"
                    val file = File(filePath)
                    file.parentFile.mkdirs()
                    part.provider().copyAndClose(file.writeChannel())
                    newImage = filePath
                }
                else -> {}
            }
            part.dispose()
        }

        if (newImage == null) throw AppException(400, "Gambar layanan tidak tersedia!")
        if (!File(newImage!!).exists()) throw AppException(400, "Gambar gagal diunggah!")

        val oldService = serviceRepo.getById(serviceId)
        if (oldService == null || oldService.userId != user.id) {
            throw AppException(404, "Data layanan tidak ditemukan!")
        }

        val entity = oldService.copy(image = newImage)
        val isUpdated = serviceRepo.update(user.id, serviceId, entity)
        if (!isUpdated) throw AppException(400, "Gagal memperbarui gambar layanan!")

        // Hapus gambar lama
        if (oldService.image != null) {
            val oldFile = File(oldService.image!!)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse("success", "Berhasil mengubah gambar layanan", null))
    }

    // DELETE /laundry-services/{id}
    suspend fun delete(call: ApplicationCall) {
        val serviceId = call.parameters["id"]
            ?: throw AppException(400, "ID layanan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val oldService = serviceRepo.getById(serviceId)
        if (oldService == null || oldService.userId != user.id) {
            throw AppException(404, "Data layanan tidak ditemukan!")
        }

        val isDeleted = serviceRepo.delete(user.id, serviceId)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data layanan!")

        if (oldService.image != null) {
            val oldFile = File(oldService.image!!)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse("success", "Berhasil menghapus data layanan", null))
    }

    // GET /images/laundry-services/{id}
    suspend fun getImage(call: ApplicationCall) {
        val serviceId = call.parameters["id"]
            ?: throw AppException(400, "ID layanan tidak valid!")

        val service = serviceRepo.getById(serviceId)
            ?: throw AppException(404, "Data layanan tidak ditemukan!")

        if (service.image == null) throw AppException(404, "Layanan belum memiliki gambar")

        val file = File(service.image!!)
        if (!file.exists()) throw AppException(404, "Gambar layanan tidak tersedia")

        call.respondFile(file)
    }
}
