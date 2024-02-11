package pt.isel.ls.api.data.mem

import pt.isel.ls.api.data.RouteData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.text.DecimalFormat

class RouteDataMem : RouteData {

    override fun addRoute(routeRequest: RoutesCreateRequest, uid: Int) : RouteAddedResponse {
        val route = Route(
            routeId++,routeRequest.startLocation,
            routeRequest.endLocation,routeRequest.distance,uid)
        routes.add(route)
        return RouteAddedResponse(route.rid)
    }

    @Throws(NotFoundException::class)
    override fun getRoute(rid: Int) : RouteResponse {
        val route = routes.find { it.rid == rid } ?:
        throw NotFoundException("route not found")
        return RouteResponse(route.rid,route.startLocation,
            route.endLocation, route.distance, route.uid)
    }

    override fun getRoutes(params: RoutesGetParams, paging: Paging) : List<RoutesListResponse> { //test
        val list = mutableListOf<RoutesListResponse>()
        val limitIndex = paging.skip+paging.limit-1

        routes.forEachIndexed { _, it ->
            val s = if(params.startLocation!=null) it.startLocation.startsWith(params.startLocation, true) else true
            val e = if(params.endLocation!=null) it.endLocation.startsWith(params.endLocation, true) else true
            if(s && e) list.add(RoutesListResponse(it.rid, it.startLocation, it.endLocation))
        }

        val iterator = list.iterator()
        var index = 0
        iterator.forEach {
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return list
    }

    override fun updateRoute(rid: Int, request: RouteUpdateRequest) : RouteResponse { //test
        routes.forEach {
            if(it.rid==rid){
                if(request.endLocation!=null) it.endLocation = request.endLocation
                val d = request.distance //pq de "smart cast is impossible because distance is var"
                if(d!=null) it.distance = d
                return RouteResponse(it.rid, it.startLocation, it.endLocation, it.distance, it.uid)
            }
        }
        return getRoute(rid)
    }
}