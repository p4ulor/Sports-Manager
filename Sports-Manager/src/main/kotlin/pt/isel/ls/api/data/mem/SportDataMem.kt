package pt.isel.ls.api.data.mem

import pt.isel.ls.api.data.SportData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*

class SportDataMem : SportData {

    override fun addSport(sportRequest : SportsCreateRequest, uid: Int) : SportAddedResponse {
        val sport = Sport(sportId++,sportRequest.name,sportRequest.description,uid)
        sports.add(sport)
        return SportAddedResponse(sport.sid)
    }

    @Throws(NotFoundException::class)
    override fun getSport(sid: Int) : SportResponse {
        val sport = sports.find { it.sid == sid } ?:
        throw NotFoundException("sport not found")
        return SportResponse(sport.sid,sport.name,sport.description, sport.uid)
    }

    override fun getSports(params: SportGetParams, paging: Paging): List<SportsListResponse> { //test
        val list = mutableListOf<SportsListResponse>()
        val limitIndex = paging.skip+paging.limit-1

        sports.forEach {
            if(params.name==null || it.name.startsWith(params.name, true))
                list.add(SportsListResponse(it.sid, it.name))
        }

        val iterator = list.iterator()
        var index = 0
        iterator.forEach { _ ->
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return list
    }

    override fun updateSport(sid: Int, request: SportsUpdateRequest) : SportResponse { //test
        sports.forEach {
            if(it.sid==sid){
                it.description = request.description
                return SportResponse(it.sid, it.name, it.description, it.uid)
            }
        }
        return getSport(sid)
    }
}