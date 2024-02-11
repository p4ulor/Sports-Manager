package pt.isel.ls.api.service

import pt.isel.ls.api.data.RouteData
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.utils.*
import java.text.DecimalFormat

private const val DISTANCE_LOWER_LIMIT = 0
private const val DISTANCE_MAX_LIMIT = 1000000

class RouteService(private val userData: UserData, private val routeData: RouteData) : RouteServiceI {

    @Throws(AuthenticationException::class, BadRequestException::class)
    override fun addRoute(routeRequest: RoutesCreateRequest, uuid: String) : RouteAddedResponse {
        routeRequest.distance = isDistanceGood(routeRequest.distance)
        val uid = userData.authenticateUser(uuid)
        return routeData.addRoute(routeRequest,uid)
    }

    @Throws(NotFoundException::class)
    override fun getRoute(rid: Int): RouteResponse {
        return routeData.getRoute(rid)
    }

    override fun getRoutes(params: RoutesGetParams, paging: Paging): List<RoutesListResponse> {
        return routeData.getRoutes(params, paging)
    }

    @Throws(NotFoundException::class, ForbiddenException::class)
    override fun updateRoute(rid: Int, request: RouteUpdateRequest, uuid: String): RouteResponse { //test
        val uid = userData.authenticateUser(uuid)
        val route = routeData.getRoute(rid) //can throw route dont exist
        request.distance = request.distance?.let { isDistanceGood(it) }
        if (uid != route.uid) throw ForbiddenException("The user $uid doesn't have authorization to update this route")
        return routeData.updateRoute(rid, request)
    }

    private fun isDistanceGood(distance: Float) : Float {
        if (distance <= DISTANCE_LOWER_LIMIT)
            throw BadRequestException("distance needs to be more than $DISTANCE_LOWER_LIMIT")
        if (distance >= DISTANCE_MAX_LIMIT)
            throw BadRequestException("distance needs to be less than $DISTANCE_MAX_LIMIT")
        return DecimalFormat("######.##").format(distance).toFloat() //note, this rounds according to the digit after the furthest decimal on the right. 1000.235 -> 1000.24
    }
}
