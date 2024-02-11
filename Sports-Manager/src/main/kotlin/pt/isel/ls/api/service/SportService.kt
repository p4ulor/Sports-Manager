package pt.isel.ls.api.service


import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.data.SportData
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.utils.*

class SportService(private val userData: UserData, private val sportData: SportData) : SportServiceI {

    @Throws(AuthenticationException::class)
    override fun addSport(sportsCreateRequest: SportsCreateRequest, uuid: String): SportAddedResponse {
        val uid = userData.authenticateUser(uuid)
        return sportData.addSport(sportsCreateRequest,uid)
    }

    @Throws(NotFoundException::class)
    override fun getSport(sid: Int): SportResponse {
        return sportData.getSport(sid)
    }

    override fun getSports(params: SportGetParams, paging: Paging): List<SportsListResponse> {
        return sportData.getSports(params, paging)
    }

    @Throws(NotFoundException::class, ForbiddenException::class)
    override fun updateSport(sid: Int, request: SportsUpdateRequest, uuid: String) : SportResponse {
        val uid = userData.authenticateUser(uuid)
        val sport = sportData.getSport(sid) //can throw sport dont exist
        if(uid!=sport.uid) throw ForbiddenException("The user $uid doesn't have authorization to update this sport")
        return sportData.updateSport(sid, request)
    }
}
