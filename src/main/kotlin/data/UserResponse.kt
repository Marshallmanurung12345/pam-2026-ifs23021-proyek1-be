package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    var id: String = "",
    var name: String = "",
    var username: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
)