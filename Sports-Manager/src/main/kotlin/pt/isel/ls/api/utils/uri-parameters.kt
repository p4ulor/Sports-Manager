package pt.isel.ls.api.utils

import kotlinx.serialization.Serializable

@Serializable
data class UsersGetsParams( //fase 2
    val sid: Int? = null,
    val rid: Int? = null
)

@Serializable
data class RoutesGetParams( //for "search" functionality fase 3
    val startLocation: String? = null,
    val endLocation: String? = null
)

@Serializable
data class SportGetParams( //for "search" functionality fase 3
    val name: String? = null
)

@Serializable
data class GetAllActivitiesParams( //fase 2
    val sid: Int,
    val orderBy: String,
    val date: String? = null,
    val rid: Int? = null
)
