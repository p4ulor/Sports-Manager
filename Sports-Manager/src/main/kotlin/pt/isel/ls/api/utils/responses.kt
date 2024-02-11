package pt.isel.ls.api.utils
import kotlinx.serialization.Serializable

@Serializable
data class AppError(
    val code: Int,
    val name: String,
    val message: String
)


@Serializable
data class UserAddedResponse(
    val uid: Int,
    val uuid: String,
)

@Serializable
data class UserResponse(
    val uid: Int,
    val name: String,
    val email: String
)

@Serializable
data class UserListResponse(
    val uid: Int,
    val name: String,
)

@Serializable
data class UserLoginResponse(
    val uuid: String
)



@Serializable
data class SportAddedResponse(
    val sid: Int
)

@Serializable
data class SportResponse(
    val sid: Int,
    val name: String,
    val description: String?,
    val uid: Int
)

@Serializable
data class SportsListResponse(
    val sid: Int,
    val name: String,
)



@Serializable
data class RouteAddedResponse(
    val rid: Int
)

@Serializable
data class RouteResponse(
    val rid: Int,
    val startLocation: String,
    val endLocation: String,
    val distance: Float,
    val uid: Int
)

@Serializable
data class RoutesListResponse(
    val rid: Int,
    val startLocation: String,
    val endLocation: String
)



@Serializable
data class ActivityAddedResponse(
    val aid: Int
)

@Serializable
data class ActivityResponse(
    val aid: Int,
    val date: String,
    val duration: String,
    val uid: Int,
    val sid: Int,
    val rid: Int?
)

@Serializable
data class ActivitiesListResponse( //usada tb como se fosse DeletedActivitiesResponse
    val aid: Int,
    val date: String,
    val duration: String, //teve q ser adicionado para que podessemos usar o orderBy (pq organiza pela duration)
    val uid: Int,
    val sid: Int,
)

@Serializable
data class ActivitiesListUsers(
    val uid: Int,
)
