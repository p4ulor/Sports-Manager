package pt.isel.ls

import org.junit.Test
import kotlin.test.*
import pt.isel.ls.api.data.mem.*
import pt.isel.ls.api.service.*
import pt.isel.ls.api.utils.*
import java.time.LocalTime
import kotlin.system.exitProcess

// NOTA: OS TESTES ESTÃO ESCRITOS SEGUNDO OS DADOS QUE ESTÃO DEFINIDOS EM DummyData.kt
// Ordem: CRUD -> Create Read Update Delete. E depois exceções, em ordem CRUD
// MANTER TODOS OS TESTES SIMETRICOS ENTRE DATA MEM E POSTGRES!

const val INVALID_ID = 9999
const val VALID_UID = 1
const val VALID_RID = 1
const val VALID_SID = 1
const val VALID_AID = 1
const val VALID_DELETE_AID = 6
val uuid1 = users[0].uuid //uuid from user 1
val uuid2 = users[1].uuid //uuid from user 2
val uuid3 = users[2].uuid //uuid from user 3

class TestServicesDataMem {

    private val activityData = ActivityDataMem()
    private val sportData = SportDataMem()
    private val routeData = RouteDataMem()
    private val userData = UserDataMem()
    private val activityService = ActivityService(userData, sportData, routeData, activityData)
    private val sportService = SportService(userData, sportData)
    private val routeService = RouteService(userData, routeData)
    private val userService = UserService(userData, activityData)

    init { if(activities.size>=7) { println("Make sure to remove extra data for SPA tests"); throw Exception() } } //System.exit() nao corre bem com os testes gradle

    /**
     * *************** USERS *******************
     */
    // CREATE
    @Test
    fun addUser(){
        val user = UserCreateRequest("Chuck","chuckduck@gmail.com", "daniel123")
        val result: UserAddedResponse = userService.addUser(user)
        val userObtained = users.get(users.size-1)
        val expected = UserAddedResponse(userObtained.uid, userObtained.uuid)
        assertEquals(expected, result).also { users.removeLast() }
    }

    // READ
    @Test
    fun getUser(){
        val result: UserResponse = userService.getUser(VALID_UID)
        val user = users.get(VALID_UID-1)
        val expected = UserResponse(user.uid, user.name, user.email)
        assertEquals(expected, result)
    }

    @Test
    fun getUsers(){
        val result = userService.getUsers(UsersGetsParams(), Paging(1,2))
        val expected = mutableListOf(UserListResponse(3, "Daniel"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getUsersGivenRIDandSID(){
        val result = userService.getUsers(UsersGetsParams(3, 1), Paging(3,0))
        val expected = mutableListOf(UserListResponse(2, "Luis"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun loginUser(){
        val result = userService.loginUser(UserLoginRequest("filipesporting@hotmail.com", "filipe123"))
        val expected = UserLoginResponse(uuid1)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    //EXCEPTIONS
    @Test
    fun addUserWithEmailThatIsTaken(){
        val user = UserCreateRequest(users.get(0).name, users.get(0).email, "a")
        assertFailsWith<BadRequestException> {
            userService.addUser(user)
        }
    }

    @Test
    fun getUserNotFoundException(){
        assertFailsWith<NotFoundException> {
            userService.getUser(INVALID_ID)
        }
    }

    @Test
    fun userAuthenticationException(){
        assertFailsWith<AuthenticationException> {
            userData.authenticateUser("$INVALID_ID")
        }.message.also { println(it) }
    }

    @Test
    fun userLoginWithBadEmail(){ //no @
        assertFailsWith<BadRequestException> {
            userService.loginUser(UserLoginRequest("sportinghotmail.com", "filipe123"))
        }
    }

    @Test
    fun userLoginWithBadEmail2(){ //no .com
        assertFailsWith<BadRequestException> {
            userService.loginUser(UserLoginRequest("sporting@hotmailcom", "filipe123"))
        }
    }

    @Test
    fun userLoginNotFoundException(){
        assertFailsWith<NotFoundException> {
            userService.loginUser(UserLoginRequest("sporting@hotmail.com", "filipe123"))
        }
    }

    @Test
    fun userLoginAuthenticationException(){
        assertFailsWith<AuthenticationException> {
            userService.loginUser(UserLoginRequest("filipesporting@hotmail.com", "wrongpassword"))
        }
    }

    /**
     * *************** ROUTE *******************
     */
    @Test
    fun addRoute() {
        val routeRequest = RoutesCreateRequest("Aveiro","Porto",1000f)
        val result: RouteAddedResponse = routeService.addRoute(routeRequest, uuid2)
        val route = routes.get(routes.size-1)
        val expected = RouteAddedResponse(route.rid)
        assertEquals(expected, result).also { routes.removeLast() }
    }

    @Test
    fun getRoute(){
        val result: RouteResponse = routeService.getRoute(VALID_RID)
        val route = routes.get(VALID_RID-1)
        val expected = RouteResponse(route.rid, route.startLocation, route.endLocation, route.distance, route.uid)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getRoutes(){
        val result = routeService.getRoutes(RoutesGetParams(), Paging(1,2))
        val expected = mutableListOf(RoutesListResponse(3,"Olaias","Aeroporto"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getRoutesGivenStartLocation(){ // phase 3
        val result = routeService.getRoutes(RoutesGetParams("Olaias"), Paging(1,0))
        val expected = mutableListOf(RoutesListResponse(3,"Olaias","Aeroporto"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getRoutesGivenEndLocation(){ // phase 3
        val result = routeService.getRoutes(RoutesGetParams(endLocation = "Aeroporto"), Paging(1,0))
        val expected = mutableListOf(RoutesListResponse(3,"Olaias","Aeroporto"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getRoutesGivenStartAndEndLocation(){ // phase 3
        val result = routeService.getRoutes(RoutesGetParams("Olaias", "Aeroporto"), Paging(1,0))
        val expected = mutableListOf(RoutesListResponse(3,"Olaias","Aeroporto"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    //UPDATES
    @Test
    fun updateRoute(){ // phase 3
        val result = routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000.23f), uuid1)
        val expected = RouteResponse(1,"Chelas","S. Sebastião", 1000.23f, 1)
        assertEquals(expected, result).also {
            println("Expected: $expected \n Result: $result")
            routes[0] = Route(1,"Chelas","Alameda",100f, 1)
        }
    }

    @Test
    fun updateRouteWithDistanceWithManyDecimals(){ // phase 3
        val result = routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000.2356789f), uuid1)
        val expected = RouteResponse(1,"Chelas","S. Sebastião", 1000.24f, 1)
        assertEquals(expected, result).also {
            println("Expected: $expected \n Result: $result")
            routes[0] = Route(1,"Chelas","Alameda",100f, 1)
        }
    }

    //EXCEPTIONS
    @Test
    fun getRouteNotFoundException(){
        assertFailsWith<NotFoundException> {
            routeService.getRoute(INVALID_ID)
        }
    }

    @Test
    fun updateRouteWithNonAuthorizedUser(){ // phase 3
        assertFailsWith<ForbiddenException> {
            routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000f), uuid2)
        }.message.also { println(it)}
    }

    @Test
    fun updateRouteWithExcessiveDistance(){ // phase 3
        assertFailsWith<BadRequestException> {
            routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000000.23f), uuid1)
        }.message.also { println(it)}
    }

    /**
     * *************** SPORT *******************
     */
    @Test
    fun addSport(){
        val sportsCreateRequest = SportsCreateRequest("Handball","Player plays with the hands and score in the goal")
        val result: SportAddedResponse = sportService.addSport(sportsCreateRequest, uuid1)
        val sport = sports.get(sports.size-1)
        val expected = SportAddedResponse(sport.sid)
        assertEquals(expected, result).also { sports.removeLast() }
    }

    @Test
    fun getSport(){
        val result: SportResponse = sportService.getSport(VALID_SID)
        val sport = sports.get(VALID_SID-1)
        val expected = SportResponse(sport.sid, sport.name, sport.description, sport.uid)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getSports(){
        val result = sportService.getSports(SportGetParams(null), Paging(2,1))
        var expected = mutableListOf(SportsListResponse(2,"Basketball"), SportsListResponse(3,"Pool"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getSportsGivenName(){ // phase 3
        val result = sportService.getSports(SportGetParams("Basketball"), Paging(1,0))
        var expected = mutableListOf(SportsListResponse(2,"Basketball"))
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    //UPDATES
    @Test
    fun updateSport(){ // phase 3
        val result = sportService.updateSport(1, SportsUpdateRequest("Sport where CR7 is the best"), uuid1)
        val expected = SportResponse(1, "Soccer", "Sport where CR7 is the best", 1)
        assertEquals(expected, result).also {
            println("Expected: $expected \n Result: $result")
            sports[0] = Sport(1,"Soccer","Is a sport you play with your feet",2)
        }
    }

    //EXCEPTIONS
    @Test
    fun getSportNotFoundException(){
        assertFailsWith<NotFoundException> {
            sportService.getSport(INVALID_ID)
        }
    }

    @Test
    fun updateSportWithNonAuthorizedUser(){ // phase 3
        assertFailsWith<ForbiddenException> {
            sportService.updateSport(1, SportsUpdateRequest("Sport where CR7 is the best"), uuid2)
        }.message.also { println(it) }
    }

    /**
     * *************** ACTIVITY *******************
     */
    //CREATE
    @Test
    fun addActivity(){
        val activityCreateRequest = ActivityCreateRequest("2010-03-05","08:30:00.000",2)

        val result: ActivityAddedResponse = activityService.addActivity(VALID_SID, activityCreateRequest, uuid3)
        val activity = activities.get(activities.size-1)
        val expected = ActivityAddedResponse(activity.aid)
        assertEquals(expected, result).also { activities.removeLast() }
    }

    // GETS
    @Test
    fun getActivitySport(){
        val result = activityService.getActivitySport(1, Paging(1,0))
        val listExpected = mutableListOf(ActivitiesListResponse(1, "2010-05-03", "13:10:10.200",3, 1))
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivitySportWithProperPaging() {
        val result = activityService.getActivitySport(3, Paging(2,1))
        val listExpected = mutableListOf(
            ActivitiesListResponse(4, "2010-05-06", "23:10:13.200", 2, 3),
            ActivitiesListResponse(5, "2010-05-07", "23:10:14.200", 2, 3)
        )
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivity(){
        val result : ActivityResponse = activityService.getActivity(VALID_AID)
        val act = activities.get(VALID_AID-1)
        val expected = ActivityResponse(act.aid, act.date.toString(), act.duration.toString(), act.uid, act.sid, act.rid)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result") }
    }

    @Test
    fun getActivityUser(){
        val result = activityService.getActivityUser(3, Paging(1,0))
        val listExpected = mutableListOf(ActivitiesListResponse(1, "2010-05-03", "13:10:10.200", 3, 1))
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivityUser2(){
        val result = activityService.getActivityUser(3, Paging(1,1)) //só há uma activity com uid==1, por isso com o skip==1 retorna empty
        var listExpected = mutableListOf<ActivitiesListResponse>()
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivityUser3(){
        val result: List<ActivitiesListResponse> = activityService.getActivityUser(2, Paging(2,1))
        var listExpected = mutableListOf(
            ActivitiesListResponse(4, "2010-05-06", "23:10:13.200", 2, 3),
            ActivitiesListResponse(5, "2010-05-07", "23:10:14.200", 2, 3)
        )
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivityUser4(){
        val result = activityService.getActivityUser(2, Paging(2,2))
        var listExpected = mutableListOf(ActivitiesListResponse(5, "2010-05-07", "23:10:14.200", 2, 3))
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivities() {
        val result = activityService.getActivities(GetAllActivitiesParams(3, "DESC","2010-05-06" ,1), Paging(1,0))
        val listExpected = mutableListOf(ActivitiesListResponse(4, "2010-05-06", "23:10:13.200", 2, 3))
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivitiesBigPaging() {
        val result = activityService.getActivities(GetAllActivitiesParams(3, "DESC"), Paging(20,0))
        val listExpected = mutableListOf<ActivitiesListResponse>()
        activities.forEachIndexed { index, it ->
            if(index in 2..4) listExpected.add(ActivitiesListResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid))
        }
        assertEquals(listExpected.reversed(), result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivities0Limit() {
        val result = activityService.getActivities(GetAllActivitiesParams(3, "DESC"), Paging(0,20))
        val listExpected = mutableListOf<ActivitiesListResponse>()
        assertEquals(listExpected, result).also { println("Expected: $listExpected \n Result: $result") }
    }

    @Test
    fun getActivitiesNoDateAndrid() {
        val result = activityService.getActivities(GetAllActivitiesParams(3, "DESC"), Paging(3,0))
        val listExpected = mutableListOf<ActivitiesListResponse>()
        activities.forEachIndexed { index, it ->
            if(index in 2..4) listExpected.add(ActivitiesListResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid))
        }
        assertEquals(listExpected.reversed(), result).also { println("Expected: $listExpected \n Result: $result") }
    }

    //UPDATES
    @Test
    fun updateActivityDateAndDuration(){ // phase 3
        val result = activityService.updateActivity(2, ActivityUpdateRequest("2022-05-19", "20:10:11.200"), uuid1)
        val expected = ActivityResponse(2, "2022-05-19", "20:10:11.200", 1, 2, 3)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result")
            activities[1] = Activity(2, "2010-05-04", LocalTime.parse("23:10:11.200"), 1, 2, 3)
        }
    }

    @Test
    fun updateActivityDate(){ // phase 3
        val result = activityService.updateActivity(2, ActivityUpdateRequest("2022-05-19", null), uuid1)
        val expected = ActivityResponse(2, "2022-05-19", "23:10:11.200", 1, 2, 3)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result")
            activities[1] = Activity(2, "2010-05-04", LocalTime.parse("23:10:11.200"), 1, 2, 3)
        }
    }

    @Test
    fun updateActivityDuration(){ // phase 3
        val result = activityService.updateActivity(2, ActivityUpdateRequest(null, "10:10:11.200"), uuid1)
        val expected = ActivityResponse(2, "2010-05-04", "10:10:11.200", 1, 2, 3)
        assertEquals(expected, result).also { println("Expected: $expected \n Result: $result")
            activities[1] = Activity(2, "2010-05-04", LocalTime.parse("23:10:11.200"), 1, 2, 3)
        }
    }

    //DELETES
    @Test
    fun deleteActivity(){
        val a = activities.get(VALID_DELETE_AID-1)
        val expected = ActivityResponse(a.aid, a.date.toString(), a.duration.toString(), a.uid, a.sid, a.rid)
        val activityResponse = activityService.deleteActivity(VALID_DELETE_AID)
        assertEquals(expected, activityResponse).also { activities.add(Activity(6, "2010-05-08", "23:10:15.200", 1, 2, 2)) }
    }

    @Test
    fun deleteActivities(){
        val acts = arrayOf(ActivititiesToDeleteRequest.Aids(5),  ActivititiesToDeleteRequest.Aids(6))
        activityService.deleteActivities(ActivititiesToDeleteRequest(acts))
        try {
            assertFailsWith<NotFoundException> { activityService.getActivity(5) }.message.also { println(it) }
            assertFailsWith<NotFoundException> { activityService.getActivity(6) }.message.also { println(it) }
        } finally {
            activities.add(Activity(5, "2010-05-07", "23:10:14.200", 2, 3, 1))
            activities.add(Activity(6, "2010-05-08", "23:10:15.200", 1, 2, 2))
        }
    }

    //EXCEPTIONS
    @Test
    fun createActivityWithBadDate(){
        assertFailsWith<BadRequestException> {
            val activityCreateRequest = ActivityCreateRequest("2010/03/05","00:00:00.100",2)
            activityService.addActivity(VALID_SID, activityCreateRequest, uuid3)
        }.message.also { println(it) }
    }

    @Test
    fun createActivityWithBadDurationValue(){ //for being bellow 1 second
        assertFailsWith<BadRequestException> {
            val activityCreateRequest = ActivityCreateRequest("2010-03-05","00:00:00.100",2)
            activityService.addActivity(VALID_SID, activityCreateRequest, uuid3)
        }.message.also { println(it) }
    }

    @Test
    fun createActivityWithBadDurationFormat(){
        assertFailsWith<BadRequestException> {
            val activityCreateRequest = ActivityCreateRequest("2010-03-05","00_00:10.100",2)
            activityService.addActivity(VALID_SID, activityCreateRequest, uuid3)
        }.message.also { println(it) }
    }

    @Test
    fun getActivityNotFoundException(){
        assertFailsWith<NotFoundException> {
            activityService.getActivity(INVALID_ID)
        }
    }

    @Test
    fun getActivitiesBadRequestorderBy() {
        assertFailsWith<BadRequestException> {
            activityService.getActivities(GetAllActivitiesParams(3, "des"), Paging(1,2))
        }.message.also { println(it) }
    }

    @Test
    fun updateActivityWithNonAuthorizedUser(){ // phase 3
        assertFailsWith<ForbiddenException> {
            activityService.updateActivity(2, ActivityUpdateRequest("2010-05-04", "23:10:11.200"), uuid2)
        }.message.also { println(it)}
    }

    @Test
    fun deleteActivityNotFoundException(){
        assertFailsWith<NotFoundException> {
            activityService.deleteActivity(INVALID_ID)
        }.message.also { println(it) }
    }
}
