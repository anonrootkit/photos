package com.fiore.photos.domain.model

import com.fiore.photos.UserProfile
import com.fiore.photos.utils.Constants

data class UserAuthBody(
    val uniqueId : String?,
    val code : String?
)

data class UserProfileBody(
    val uniqueId : String,
    val name : String,
    val followersCount : String,
    val followingCount : String,
    val isPublicAccount : Boolean
) {
    companion object{
        fun getBasicProfile(uniqueId: String) = UserProfileBody(
            uniqueId = uniqueId,
            name = Constants.PLACEHOLDER_NAME,
            followersCount = "0",
            followingCount = "0",
            isPublicAccount = true
        )
    }
}