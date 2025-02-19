package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.User

data class UserResponse(
    val id: String = "",
    val name: String = "",
    val profileImageUrl: String = ""
) {
    fun toDomain(): User {
        return User(
            id = this.id,
            name = this.name,
            profileImageUrl = this.profileImageUrl
        )
    }
}