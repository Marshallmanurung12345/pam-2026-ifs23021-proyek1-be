package org.delcom.entities

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RefreshToken(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var refreshToken: String,
    var authToken: String,
    var createdAt: String = "",
)