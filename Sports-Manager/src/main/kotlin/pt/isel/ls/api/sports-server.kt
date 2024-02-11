package pt.isel.ls.api

import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.data.RouteData
import pt.isel.ls.api.data.SportData
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.data.mem.ActivityDataMem
import pt.isel.ls.api.data.mem.RouteDataMem
import pt.isel.ls.api.data.mem.SportDataMem
import pt.isel.ls.api.data.mem.UserDataMem
import pt.isel.ls.api.data.postgres.ActivityDataPostgres
import pt.isel.ls.api.data.postgres.RouteDataPostgres
import pt.isel.ls.api.data.postgres.SportDataPostgres
import pt.isel.ls.api.data.postgres.UserDataPostgres
import pt.isel.ls.api.service.ActivityService
import pt.isel.ls.api.service.RouteService
import pt.isel.ls.api.service.SportService
import pt.isel.ls.api.service.UserService
import pt.isel.ls.api.webapi.ActivityWebApi
import pt.isel.ls.api.webapi.RouteWebApi
import pt.isel.ls.api.webapi.SportWebApi
import pt.isel.ls.api.webapi.UserWebApi

private val logger = LoggerFactory.getLogger("pt.isel.ls.api.sports-server")

fun main(args: Array<String>) {
    var activityData: ActivityData?
    var sportData: SportData?
    var routeData: RouteData?
    var userData: UserData?

    if(args.isNotEmpty() && args[0] == "postgres") {
        logger.info("Data source is postgres")
        val dataSource = PGSimpleDataSource()
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        dataSource.setURL(jdbcDatabaseURL)
        if(dataSource.connection.isValid(5000))
            logger.info("Connected to local postgresql DB")

        //TODO if the tables dont exist in the database, create them

        activityData = ActivityDataPostgres(dataSource)
        sportData = SportDataPostgres(dataSource)
        routeData = RouteDataPostgres(dataSource)
        userData = UserDataPostgres(dataSource)
    } else {
        logger.info("Data source is in memory")
        activityData = ActivityDataMem()
        sportData = SportDataMem()
        routeData = RouteDataMem()
        userData = UserDataMem()
    }

    val activityService = ActivityService(userData, sportData, routeData, activityData)
    val sportService = SportService(userData, sportData)
    val routeService = RouteService(userData, routeData)
    val userService = UserService(userData, activityData)

    val activityWebApi = ActivityWebApi(activityService)
    val sportWebApi = SportWebApi(sportService)
    val routeWebApi = RouteWebApi(routeService)
    val userWebApi = UserWebApi(userService)

    val app = routes(
        userWebApi.usersRoutes,
        routeWebApi.routesRoutes,
        sportWebApi.sportsRoutes,
        activityWebApi.activityRoutes,
        singlePageApp(ResourceLoader.Directory("static-content"))
    )

    val port = System.getenv("PORT")?.toIntOrNull() ?: 9000
    val jettyServer = app.asServer(Jetty(port)).start()
    logger.info("server started listening in port ${jettyServer.port()}. Input something to terminate")
    readln()
    jettyServer.stop()
    logger.info("leaving Main")
}
