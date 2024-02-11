package pt.isel.ls.api.data

import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*

// Ordered by CRUD - Create, Read, Update, Delete, ...
// Parameter orders: path-param, request (for PUT's and POST's), paging (for GETS), ...

interface UserData {
    @Throws(BadRequestException::class)
    fun addUser(userRequest: UserCreateRequest) : UserAddedResponse

    @Throws(NotFoundException::class)
    fun getUser(uid: Int) : UserResponse

    fun getUsers(paging: Paging) : List<UserListResponse>

    @Throws(AuthenticationException::class)
    fun authenticateUser(uuid: String): Int

    @Throws(NotFoundException::class, AuthenticationException::class)
    fun loginUser(loginRequest: UserLoginRequest) : UserLoginResponse
}

interface SportData {
    @Throws(BadRequestException::class)
    fun addSport(sportRequest: SportsCreateRequest, uid: Int) : SportAddedResponse

    @Throws(NotFoundException::class)
    fun getSport(sid: Int) : SportResponse

    fun getSports(params: SportGetParams, paging: Paging) : List<SportsListResponse> // works as it's "searchRoute" revisit

    @Throws(NotFoundException::class)
    fun updateSport(sid: Int, request: SportsUpdateRequest) : SportResponse // phase 3
}

interface RouteData {
    fun addRoute(routeRequest: RoutesCreateRequest, uid: Int) : RouteAddedResponse

    @Throws(NotFoundException::class)
    fun getRoute(rid: Int) : RouteResponse

    fun getRoutes(params: RoutesGetParams, paging: Paging) : List<RoutesListResponse> // works as it's "searchRoute" revisit

    @Throws(NotFoundException::class)
    fun updateRoute(rid: Int, request: RouteUpdateRequest) : RouteResponse // phase 3
}

interface ActivityData {
    @Throws(AuthenticationException::class, NotFoundException::class, BadRequestException::class)
    fun addActivity(sid: Int, activityRequest: ActivityCreateRequest, uid: Int) : ActivityAddedResponse

    @Throws(NotFoundException::class)
    fun getActivity(aid: Int) : ActivityResponse

    @Throws(NotFoundException::class)
    fun getActivitySport(sid: Int, paging: Paging) : List<ActivitiesListResponse>

    @Throws(NotFoundException::class)
    fun getActivityUser(uid: Int, paging: Paging) : List<ActivitiesListResponse>

    @Throws(BadRequestException::class)
    fun getActivities(params: GetAllActivitiesParams, paging: Paging) : List<ActivitiesListResponse>

    @Throws(NotFoundException::class)
    fun getActivitiesWithSportAndRoute/*orderedByDuration*/(sid: Int, rid: Int, paging: Paging) : List<ActivitiesListUsers> //utility function para se poder fazer getUsersFromActivity com simplicidade

    @Throws(NotFoundException::class, BadRequestException::class)
    fun updateActivity(aid: Int, request: ActivityUpdateRequest) : ActivityResponse // phase 3

    @Throws(NotFoundException::class)
    fun deleteActivity(aid: Int) : ActivityResponse

    @Throws(NotFoundException::class)
    fun deleteActivities(aids: MutableList<Int>): List<ActivitiesListResponse>
}
