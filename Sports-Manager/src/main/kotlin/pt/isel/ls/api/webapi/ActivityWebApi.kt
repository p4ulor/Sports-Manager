package pt.isel.ls.api.webapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.api.service.ActivityService
import pt.isel.ls.api.utils.*

class ActivityWebApi(private val activityService: ActivityService) : WebApi() {

    val activityRoutes = routes(
        "sports/{sid}/activities" bind Method.POST to ::createActivity, // Adds a new activity for a sport, requires sid of that sport, requires requestBody for the creation of the activity
        "sports/{sid}/activities" bind Method.GET to ::getActivitySport, // gets a list activities of a given sport, requires sid of that sport, supports paging
        "users/{uid}/activities" bind Method.GET to ::getActivityUser, // gets a list activities of a given user, requires uid of that user, supports paging
        "activities/{aid}" bind Method.GET to ::getActivity, // Get an activity, requires aid in path
        "activities/{aid}" bind Method.DELETE to ::deleteActivity, // Delete an activity, required aid in path
        "activities" bind Method.POST to ::deleteActivities,
        "activities" bind Method.GET to ::getActivities, // Get a list of activities, according to parameters in query (sid, orderBy, date?, rid?), supports paging
        "activities/{aid}" bind Method.PUT to ::updateActivity
    )

    private fun createActivity(request: Request) : Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val sid = getID_InURI(request, "sid")
            val requestBody = getRequestBody<ActivityCreateRequest>(request)
            activityService.addActivity(sid, requestBody, uuid)
        }
    }

    private fun getActivitySport(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            val sid = getID_InURI(request, "sid")
            activityService.getActivitySport(sid, paging)
        }
    }

    private fun getActivityUser(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            val uid = getID_InURI(request, "uid")
            activityService.getActivityUser(uid, paging)
        }
    }

    private fun getActivity(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val aid = getID_InURI(request, "aid")
            activityService.getActivity(aid)
        }
    }

    private fun deleteActivity(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val aid = getID_InURI(request, "aid")
            activityService.deleteActivity(aid)
        }
    }

    private fun deleteActivities(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val requestBody = getRequestBody<ActivititiesToDeleteRequest>(request)
            activityService.deleteActivities(requestBody)
        }
    }

    private fun getActivities(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            val sid = getIntParamInQuery(request, "sid")!!
            val orderBy = getStringParamInQuery(request, "orderBy")!!
            val date = getStringParamInQuery(request, "date", false)
            val rid = getIntParamInQuery(request, "rid, false", false)
            activityService.getActivities(GetAllActivitiesParams(sid, orderBy, date, rid), paging)
        }
    }

    private fun updateActivity(request: Request): Response {
        return routeMethodCall(request, Status.CREATED) {
            val uuid = getAuthorization_uuid(request)
            val aid = getID_InURI(request, "aid")
            val requestBody = getRequestBody<ActivityUpdateRequest>(request)
            activityService.updateActivity(aid, requestBody, uuid)
        }
    }
}
