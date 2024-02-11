package pt.isel.ls.api.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.api.data.RouteData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.sql.ResultSet
import java.sql.Statement
import java.text.DecimalFormat

class RouteDataPostgres(private val dataSource: PGSimpleDataSource) : RouteData {

    override fun addRoute(routeRequest: RoutesCreateRequest, uid: Int): RouteAddedResponse {
        val getGeneratedKeys: Int
        dataSource.connection.use {
            val query = it.prepareStatement(
                "INSERT INTO ROUTE VALUES" +
                    "(DEFAULT,'${routeRequest.startLocation}','${routeRequest.endLocation}',${routeRequest.distance},${uid})",
                Statement.RETURN_GENERATED_KEYS
            )
            query.executeUpdate()
            val rs = query.getGeneratedKeys()
            getGeneratedKeys = if (rs.next()) rs.getInt(1) else -1
        }
        return RouteAddedResponse(getGeneratedKeys)
    }

    @Throws(NotFoundException::class)
    override fun getRoute(rid: Int): RouteResponse {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(" SELECT * FROM ROUTE WHERE rid=${rid}")
            result = query.executeQuery()
        }
        if (result.next()) {
            val getrid = result.getInt("rid")
            val getstartLocation = result.getString("startLocation")
            val getendLocation = result.getString("endLocation")
            val getdistance = result.getFloat("distance")
            val getuid = result.getInt("uid")
            return RouteResponse(getrid, getstartLocation, getendLocation, getdistance, getuid)
        }
        throw NotFoundException("route not found")
    }

    override fun getRoutes(params: RoutesGetParams, paging: Paging) : List<RoutesListResponse>{
        val result: ResultSet
        val list = mutableListOf<RoutesListResponse>()
        var startLocationLike = if(params.startLocation!=null) "startLocation LIKE '${params.startLocation}'" else null
        var endLocationLike = if(params.endLocation!=null) "endLocation LIKE '${params.endLocation}'" else null
        var condition = ""
        if(endLocationLike!=null) {
            condition = "WHERE $endLocationLike"
            if(startLocationLike!=null) condition = "$condition AND $startLocationLike"
        }
        else if(startLocationLike!=null) condition = "WHERE $startLocationLike"
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT rid,startLocation,endLocation " +
                        "FROM ROUTE " +
                        "$condition " +
                        "ORDER BY rid " +
                        "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) {
            val rid = result.getInt("rid")
            val startLocation = result.getString("startLocation")
            val endLocation = result.getString("endLocation")
            list.add(RoutesListResponse(rid, startLocation, endLocation))
        }
        return list
    }

    override fun updateRoute(rid: Int, request: RouteUpdateRequest) : RouteResponse { //test
        if(request.endLocation!=null || request.distance!=null) {
            //disableConstraints(dataSource)
            var endLocationSet = if(request.endLocation!=null) "endLocation = '${request.endLocation}'" else ""
            var distanceSet = if(request.distance!=null) "distance = ${request.distance}" else ""
            if(distanceSet.isNotEmpty() && endLocationSet.isNotEmpty()) endLocationSet = "$endLocationSet, "
            dataSource.connection.use {
                it.prepareStatement("UPDATE ROUTE SET $endLocationSet $distanceSet WHERE rid=$rid").executeUpdate()
            }
        }
        return getRoute(rid)
    }
}