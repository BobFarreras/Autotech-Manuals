package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.User

data class UserResponse(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val description: String = "",
    val stars: Int = 0
) {
    fun toDomain(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            profileImageUrl = this.profileImageUrl,
            description = this.description,
            stars = this.stars
        )
    }
}