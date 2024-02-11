package pt.isel.ls.api.service

import pt.isel.ls.api.utils.*
// SERVICES IS THE MODULE THAT CHECKS IF THE FORMATS OF DATES, DURATIONS, ETC ARE GOOD, AND IF KEYS AND FOREIGN KEYS EXIST. Which is why it has acess to other entities, other data sources
// While that "data" executes the values

// Ordered by CRUD - Create, Read, Update and Delete
// Parameter orders: path, request - for PUT and POST. path, params, paging for GETS

interface UserServiceI {
    @Throws(BadRequestException::class)
    fun addUser(userRequest: UserCreateRequest) : UserAddedResponse

    @Throws(NotFoundException::class)
    fun getUser(uid: Int) : UserResponse

    fun getUsers(params: UsersGetsParams, paging: Paging) : List<UserListResponse>

    @Throws(NotFoundException::class, AuthenticationException::class, BadRequestException::class)
    fun loginUser(loginRequest: UserLoginRequest) : UserLoginResponse
}

interface SportServiceI {
    @Throws(AuthenticationException::class)
    fun addSport(sportsCreateRequest: SportsCreateRequest, uuid: String): SportAddedResponse

    @Throws(NotFoundException::class)
    fun getSport(sid: Int) : SportResponse

    fun getSports(params: SportGetParams, paging: Paging) : List<SportsListResponse> // works as it's "searchRoute" revisit

    @Throws(NotFoundException::class, AuthenticationException::class)
    fun updateSport(sid: Int, request: SportsUpdateRequest, uuid: String) : SportResponse
}

interface RouteServiceI {
    @Throws(AuthenticationException::class, BadRequestException::class)
    fun addRoute(routeRequest: RoutesCreateRequest, uuid: String) : RouteAddedResponse

    @Throws(NotFoundException::class)
    fun getRoute(rid: Int): RouteResponse

    fun getRoutes(params: RoutesGetParams, paging: Paging): List<RoutesListResponse>

    @Throws(NotFoundException::class, AuthenticationException::class)
    fun updateRoute(rid: Int, request: RouteUpdateRequest, uuid: String) : RouteResponse
}

interface ActivityServiceI {
    @Throws(AuthenticationException::class, NotFoundException::class, BadRequestException::class)
    fun addActivity(sid: Int, activityCreateRequest: ActivityCreateRequest, uuid: String): ActivityAddedResponse

    @Throws(NotFoundException::class)
    fun getActivity(aid: Int): ActivityResponse

    @Throws(NotFoundException::class)
    fun getActivitySport(sid: Int, paging: Paging) : List<ActivitiesListResponse>

    @Throws(NotFoundException::class)
    fun getActivityUser(uid: Int, paging: Paging) : List<ActivitiesListResponse>

    @Throws(NotFoundException::class, BadRequestException::class)
    fun getActivities(params: GetAllActivitiesParams, paging: Paging): List<ActivitiesListResponse>

    @Throws(NotFoundException::class, AuthenticationException::class)
    fun updateActivity(aid: Int, update: ActivityUpdateRequest, uuid: String) : ActivityResponse

    @Throws(NotFoundException::class)
    fun deleteActivity(aid: Int): ActivityResponse

    @Throws(NotFoundException::class)
    fun deleteActivities(aids: ActivititiesToDeleteRequest): List<ActivitiesListResponse>
}