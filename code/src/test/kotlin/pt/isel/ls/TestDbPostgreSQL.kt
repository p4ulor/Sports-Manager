package pt.isel.ls

import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.api.data.mem.activities
import pt.isel.ls.api.data.mem.routes
import pt.isel.ls.api.data.mem.sports
import pt.isel.ls.api.data.mem.users
import pt.isel.ls.api.data.postgres.ActivityDataPostgres
import pt.isel.ls.api.data.postgres.RouteDataPostgres
import pt.isel.ls.api.data.postgres.SportDataPostgres
import pt.isel.ls.api.data.postgres.UserDataPostgres
import pt.isel.ls.api.service.*
import pt.isel.ls.api.utils.*
import java.sql.Connection
import kotlin.system.exitProcess
import kotlin.test.*

// NOTA: OS TESTES ESTÃO ESCRITOS SEGUNDO OS DADOS QUE INSERIMOS SEGUNDO OS SCRIPTS DE SQL DE INSERT TABLE
// Ordem: CRUD -> Create Read Update Delete. E depois exceções, em ordem CRUD
// MANTER TODOS OS TESTES SIMETRICOS ENTRE DATA MEM E POSTGRES!

class TestDbPostgreSQL {
    private var userData: UserDataPostgres
    private var activityService: ActivityService
    private var sportService: SportService
    private var routeService: RouteService
    private var userService: UserService
    private val dataSource = PGSimpleDataSource()

    init {
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        dataSource.setURL(jdbcDatabaseURL)
        if(dataSource.connection.isValid(0)){
            val activityData = ActivityDataPostgres(dataSource)
            val sportData = SportDataPostgres(dataSource)
            val routeData = RouteDataPostgres(dataSource)
            userData = UserDataPostgres(dataSource)

            activityService = ActivityService(userData, sportData, routeData, activityData)
            sportService = SportService(userData, sportData)
            routeService = RouteService(userData, routeData)
            userService = UserService(userData, activityData)
        } else {
            println("Connection to DB failed")
            exitProcess(-1)
        }
    }

    /**
     * *************** USERS *******************
     */
    // CREATE
    @Test
    fun addUser(){
        val user = UserCreateRequest("Chuck","chuckduck@gmail.com", "daniel123")
        val result = userService.addUser(user).uid
        val expected = userService.getUser(4).uid //visto q o getUser nao retorna uuid, entao comparamos so o uid
        assertEquals(expected, result).also { deleteFromWhere("user", "uid>3"); resetCount("user", 4) }
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
        val route = routeService.getRoute(4)
        val expected = RouteAddedResponse(route.rid)
        assertEquals(expected, result).also { deleteFromWhere("route", "rid>3"); resetCount("route", 4) }
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
        assertEquals(expected, result).also { updateRouteReset(expected, result) }
    }

    @Test
    fun updateRouteWithDistanceWithManyDecimals(){ // phase 3
        val result = routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000.2356789f), uuid1)
        val expected = RouteResponse(1,"Chelas","S. Sebastião", 1000.24f, 1)
        assertEquals(expected, result).also { updateRouteReset(expected, result) }
    }

    private fun updateRouteReset(expected: RouteResponse, result: RouteResponse) {
        println("Expected: $expected \n Result: $result")
        routeService.updateRoute(1, RouteUpdateRequest("Alameda", 100f), uuid1)
        activityService.getActivities(GetAllActivitiesParams(1, "ASC", null, 1), Paging()).forEach {
            println(it)
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
            routeService.updateRoute(1, RouteUpdateRequest("S. Sebastião", 1000000f), uuid1)
        }.message.also { println(it)}
    }

    /**
     * *************** SPORT *******************
     */
    @Test
    fun addSport(){
        val sportsCreateRequest = SportsCreateRequest("Handball","Player plays with the hands and score in the goal")
        val result: SportAddedResponse = sportService.addSport(sportsCreateRequest, uuid1)
        val sport = sportService.getSport(4)
        val expected = SportAddedResponse(sport.sid)
        assertEquals(expected, result).also { deleteFromWhere("sport", "sid>3"); resetCount("sport", 4) }
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
            sportService.updateSport(1, SportsUpdateRequest("Is a sport you play with your feet"), uuid1)
                .also { println(it) }
            activityService.getActivitySport(1, Paging()).forEach {
                println(it)
            }
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
        resetCount("activity", 7) //tive q meter isto aqui senao nao dava, deve ser pq o processo de alterar a sequencia demora ou assim, nao sei, ja procurei imenso... Dá "duplicate key value violates unique constraint "activity_pkey" Detail: Key (aid)=(6) already exists."
        val activityCreateRequest = ActivityCreateRequest("2010-03-05","08:30:00.000",2)
        val result: ActivityAddedResponse = activityService.addActivity(VALID_SID, activityCreateRequest, uuid3)
        val activity = activityService.getActivity(7)
        val expected = ActivityAddedResponse(activity.aid)
        assertEquals(expected, result).also { deleteFromWhere("activity", "aid>6"); resetCount("activity", 7) }
    }

    // GETS
    @Test
    fun getActivitySport(){ // when paging.skip is greater than the length
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
        assertEquals(expected, result).also { updateReset(expected, result) }
    }

    @Test
    fun updateActivityDate(){ // phase 3
        val result = activityService.updateActivity(2, ActivityUpdateRequest("2022-05-19", null), uuid1)
        val expected = ActivityResponse(2, "2022-05-19", "23:10:11.200", 1, 2, 3)
        assertEquals(expected, result).also { updateReset(expected, result) }
    }

    @Test
    fun updateActivityDuration(){ // phase 3
        val result = activityService.updateActivity(2, ActivityUpdateRequest(null, "10:10:11.200"), uuid1)
        val expected = ActivityResponse(2, "2010-05-04", "10:10:11.200", 1, 2, 3)
        assertEquals(expected, result).also { updateReset(expected, result) }
    }

    private fun updateReset(expected: ActivityResponse, result: ActivityResponse){
        println("Expected: $expected \n Result: $result")
        activityService.updateActivity(2, ActivityUpdateRequest("2010-05-04", "23:10:11.200"), uuid1)
    }

    //DELETES
    @Test
    fun deleteActivity(){
        val a = activities.get(VALID_DELETE_AID-1)
        val expected = ActivityResponse(a.aid, a.date.toString(), a.duration.toString(), a.uid, a.sid, a.rid)
        val activityResponse = activityService.deleteActivity(VALID_DELETE_AID)
        assertEquals(expected, activityResponse).also {
            resetCount("activity", 6)
            insertInto("activity", "6, '2010-05-08', '23:10:15.200', 1, 2, 2")
        }
    }

    @Test
    fun deleteActivities(){
        val acts = arrayOf(ActivititiesToDeleteRequest.Aids(5),  ActivititiesToDeleteRequest.Aids(6))
        activityService.deleteActivities(ActivititiesToDeleteRequest(acts))
        try {
            assertFailsWith<NotFoundException> { activityService.getActivity(5) }.message.also { println(it) }
            assertFailsWith<NotFoundException> { activityService.getActivity(6) }.message.also { println(it) }
        } finally {
            resetCount("activity", 5)
            insertInto("activity", "5, '2010-05-07', '23:10:14.200', 2, 3, 1")
            insertInto("activity", "6, '2010-05-08', '23:10:15.200', 1, 2, 2")
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

    private fun deleteFromWhere(table: String, condition: String){
        dataSource.connection.use {
            it.prepareStatement("DELETE FROM \"${table}\" VALUES WHERE (${condition})").executeUpdate()
        }
    }

    private fun insertInto(table: String, values: String) {
        dataSource.connection.use {
            it.prepareStatement("INSERT INTO \"${table}\" VALUES (${values})").executeUpdate()
            //printSeqCount(table, it)
        }
    }

    private fun resetCount(table: String, counterN: Int){
        dataSource.connection.use {
            val seq = getSequence(table)
            it.prepareStatement("ALTER SEQUENCE $seq RESTART WITH $counterN").executeUpdate()
            printSeqCount(table, it)
        }
    }

    private fun printSeqCount(table: String, connection: Connection){
        val seq = getSequence(table)
        val result = connection.prepareStatement("SELECT last_value FROM $seq").executeQuery()
        result.next()
        val n = result.getInt("last_value")
        println("N of $table = $n")
    }

    private fun getSequence(table: String) : String {
        return when(table.uppercase()){
            "USER" -> "USER_uid_seq"
            "ROUTE" -> "ROUTE_rid_seq"
            "SPORT" -> "SPORT_sid_seq"
            else -> "ACTIVITY_aid_seq"
        }
    }
}
