package pt.isel.ls.api.webapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.api.service.RouteService
import pt.isel.ls.api.utils.*

class RouteWebApi(private val routeService: RouteService) : WebApi() {

    val routesRoutes = routes(
        "routes" bind Method.POST to ::createRoute, //requires requestBody
        "routes" bind Method.GET to ::getRoutes, //supports paging
        "routes/{rid}" bind Method.GET to ::getRoute, //requires rid in path
        "routes/{rid}" bind Method.PUT to ::updateRoute //requires rid in path
    )

    private fun createRoute(request: Request): Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val requestBody = getRequestBody<RoutesCreateRequest>(request)
            routeService.addRoute(requestBody, uuid)
        }
    }

    private fun getRoutes(request: Request): Response {
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            val startLocation = getStringParamInQuery(request, "startLocation", false)
            val endLocation = getStringParamInQuery(request, "endLocation", false)
            routeService.getRoutes(RoutesGetParams(startLocation, endLocation), paging)
        }
    }

    private fun getRoute(request: Request): Response {
        return routeMethodCall(request, Status.OK) {
            val rid = getID_InURI(request, "rid")
            routeService.getRoute(rid)
        }
    }

    private fun updateRoute(request: Request): Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val rid = getID_InURI(request, "rid")
            val requestBody = getRequestBody<RouteUpdateRequest>(request)
            routeService.updateRoute(rid, requestBody, uuid)
        }
    }
}
