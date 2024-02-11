package pt.isel.ls.api.service


import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.data.RouteData
import pt.isel.ls.api.data.SportData
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.utils.*
import pt.isel.ls.api.webapi.UPPERBOUND_LIMIT
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

private const val DURATION_LOWER_LIMIT = "00:00:01.0"
const val ASC = "ASC"
const val DESC = "DESC"

class ActivityService(private val userData: UserData, private val sportData: SportData, private val routeData: RouteData, private val activityData: ActivityData) : ActivityServiceI {

    @Throws(AuthenticationException::class, NotFoundException::class, BadRequestException::class)
    override fun addActivity(sid: Int, activityCreateRequest: ActivityCreateRequest, uuid: String): ActivityAddedResponse {
        val uid = userData.authenticateUser(uuid)
        isDateGood(activityCreateRequest.date)
        isDurationGood(activityCreateRequest.duration)
        activityCreateRequest.rid?.let { doesRouteExist(it) }
        doesSportExist(sid)
        return activityData.addActivity(sid, activityCreateRequest, uid)
    }

    @Throws(NotFoundException::class)
    override fun getActivitySport(sid: Int, paging: Paging): List<ActivitiesListResponse> {
        return activityData.getActivitySport(sid, paging)
    }

    @Throws(NotFoundException::class)
    override fun getActivity(aid: Int): ActivityResponse {
        return activityData.getActivity(aid)
    }

    @Throws(NotFoundException::class)
    override fun deleteActivity(aid: Int): ActivityResponse {
        return activityData.deleteActivity(aid)
    }

    @Throws(NotFoundException::class)
    override fun deleteActivities(aids: ActivititiesToDeleteRequest): List<ActivitiesListResponse> {
        return activityData.deleteActivities(aids.getAids())
    }

    @Throws(NotFoundException::class)
    override fun getActivityUser(uid: Int, paging: Paging) : List<ActivitiesListResponse> {
        return activityData.getActivityUser(uid, paging)
    }

    @Throws(NotFoundException::class, BadRequestException::class)
    override fun getActivities(params: GetAllActivitiesParams, paging: Paging): List<ActivitiesListResponse> {
        doesSportExist(params.sid)
        if (params.orderBy!=ASC && params.orderBy!=DESC) {
            throw BadRequestException("orderBy needs to be ASC or DESC (it's case sensitive, use capital letters)")
        }
        if(params.date!=null) isDateGood(params.date)
        if (params.rid!=null) doesRouteExist(params.rid)
        return activityData.getActivities(params, paging)
    }

    @Throws(NotFoundException::class, ForbiddenException::class)
    override fun updateActivity(aid: Int, update: ActivityUpdateRequest, uuid: String) : ActivityResponse {
        val uid = userData.authenticateUser(uuid)
        update.date?.let { isDateGood(it) }
        update.duration?.let { isDurationGood(it) }
        //update.rid?.let { doesRouteExist(it) }
        val activity = activityData.getActivity(aid) //can throw activity dont exist
        if(uid!=activity.uid) throw ForbiddenException("The user $uid doesn't have authorization to update this activity")
        return activityData.updateActivity(aid, update)
    }

    private fun doesRouteExist(rid: Int) { //nestes 3 proximos metodos secalhar só
        try { routeData.getRoute(rid) }
        catch (e: NotFoundException){ throw NotFoundException("Can't add Activity with a Route that doesn't exist") }
    }

    private fun doesSportExist(sid: Int) {
        try { sportData.getSport(sid) }
        catch (e: NotFoundException){ throw NotFoundException("Can't add Activity with a Sport that doesn't exist") }
    }

    private fun isDurationGood(duration: String) {
        try { LocalTime.parse(duration) }
        catch (e: DateTimeParseException) {
            throw BadRequestException("Error with formation of duration on Activity. " +
                    "Format is like: ${LocalTime.NOON} and like ${LocalTime.MAX}")
        }

        if(LocalTime.parse(duration) < LocalTime.parse(DURATION_LOWER_LIMIT)){
            throw BadRequestException("duration needs to be equal or more than $DURATION_LOWER_LIMIT. " +
                    "The activity's duration is $duration")
        }
    }

    private fun isDateGood(date: String){
        try { LocalDate.parse(date) }
        catch(e: DateTimeParseException){ throw BadRequestException("Error with formation of date on Activity") }
    }
}
