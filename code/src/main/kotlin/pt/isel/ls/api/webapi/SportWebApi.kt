package pt.isel.ls.api.webapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.api.service.SportService
import pt.isel.ls.api.utils.*

class SportWebApi(private val sportService: SportService) : WebApi() {

    val sportsRoutes = routes(
        "sports" bind Method.POST to ::createSport, //requires requestBody
        "sports" bind Method.GET to ::getSports, //supports paging
        "sports/{sid}" bind Method.GET to ::getSport, //requires sid in path
        "sports/{sid}" bind Method.PUT to :: updateSport
    )

    private fun createSport(request: Request): Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val requestBody = getRequestBody<SportsCreateRequest>(request)
            sportService.addSport(requestBody, uuid)
        }
    }

    private fun getSports(request: Request): Response { // GET /users?limit=5&skip=1
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            val name = getStringParamInQuery(request, "name", false)
            sportService.getSports(SportGetParams(name),  paging)
        }
    }

    private fun getSport(request: Request): Response {
        return routeMethodCall(request, Status.OK) {
            val sid = getID_InURI(request, "sid")
            sportService.getSport(sid)
        }
    }

    private fun updateSport(request: Request): Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val sid = getID_InURI(request, "sid")
            val requestBody = getRequestBody<SportsUpdateRequest>(request)
            sportService.updateSport(sid, requestBody, uuid)
        }
    }
}
