package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.User

@Serializable
data class AuthRequest(
    var name: String = "",
    var username: String = "",
    var password: String = "",
    var newPassword: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "username" to username,
        "password" to password,
        "newPassword" to newPassword
    )

    fun toEntity(): User = User(
        name = name,
        username = username,
        password = password,
    )
}