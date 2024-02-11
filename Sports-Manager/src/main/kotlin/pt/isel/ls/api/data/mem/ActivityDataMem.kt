package pt.isel.ls.api.data.mem

import kotlinx.datetime.LocalDate
import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.service.ASC
import pt.isel.ls.api.service.DESC
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.time.LocalTime

/**
 * Usei iteradores para remover porque: https://www.baeldung.com/java-concurrentmodificationexception
 */

class ActivityDataMem : ActivityData {
    override fun addActivity(sid: Int, activityRequest: ActivityCreateRequest, uid: Int) : ActivityAddedResponse {
        val activity = Activity(
            activityId++,activityRequest.date,
            activityRequest.duration,
            uid, sid,activityRequest.rid)
        activities.add(activity)
        return ActivityAddedResponse(activity.aid)
    }

    override fun getActivitySport(sid: Int, paging: Paging) : List<ActivitiesListResponse>{
        val list = mutableListOf<ActivitiesListResponse>()
        activities.forEach {
            if(it.sid==sid)
                list.add(ActivitiesListResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid))
        }
        val limitIndex = paging.skip+paging.limit-1
        val iterator = list.iterator()
        var index = 0
        iterator.forEach { _ ->
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return list
    }

    override fun getActivitiesWithSportAndRoute(sid: Int, rid: Int, paging: Paging): List<ActivitiesListUsers> {
        var list = mutableListOf<Pair<ActivitiesListUsers, LocalTime>>()
        activities.forEach {
            if (it.sid==sid && it.rid==rid) {
                val found = Pair(ActivitiesListUsers(it.uid), it.duration)
                val exists = {
                    var e = false
                    list.forEach { it2 ->
                        if(it2.first.uid==found.first.uid) e = true
                    }
                    e
                }
                if(!exists()) list.add(found)
            }
        }
        list = list.sortedBy { it.second }.toMutableList() //confirmado q funciona
        var final = mutableListOf<ActivitiesListUsers>()
        list.forEach {
            final.add(it.first)
        }

        val limitIndex = paging.skip+paging.limit-1
        val iterator = final.iterator()
        var index = 0
        iterator.forEach { _ ->
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return final
    }

    @Throws(NotFoundException::class)
    override fun getActivity(aid: Int) : ActivityResponse {
        val a = activities.find { it.aid == aid } ?: throw NotFoundException("activity not found")
        return ActivityResponse(a.aid,a.date.toString(),a.duration.toString(), a.uid,a.sid, a.rid)
    }

    @Throws(NotFoundException::class)
    override fun deleteActivity(aid: Int) : ActivityResponse {
        val a = activities.find { it.aid == aid } ?: throw NotFoundException("activity not found")
        activities.remove(a)
        return ActivityResponse(a.aid,a.date.toString(), a.duration.toString(), a.uid, a.sid, a.rid)
    }

    override fun deleteActivities(aids: MutableList<Int>) : List<ActivitiesListResponse> {
        val listOfActivitiesRemoved = mutableListOf<ActivitiesListResponse>()
        aids.forEach {
            val a = deleteActivity(it)
            listOfActivitiesRemoved.add(ActivitiesListResponse(a.aid, a.date, a.duration, a.uid, a.sid))
        }
        return listOfActivitiesRemoved
    }

    override fun getActivityUser(uid: Int, paging: Paging) : List<ActivitiesListResponse> {
        val list = mutableListOf<ActivitiesListResponse>()
        activities.forEach {
            if(it.uid==uid)
                list.add(ActivitiesListResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid))
        }
        val limitIndex = paging.skip+paging.limit-1
        val iterator = list.iterator()
        var index = 0
        iterator.forEach { _ ->
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return list
    }

    override fun getActivities(params: GetAllActivitiesParams, paging: Paging): List<ActivitiesListResponse> { //test
        var list = mutableListOf<ActivitiesListResponse>()
        activities.forEach {
            if(it.sid==params.sid){
                val d = if(params.date==null) true else params.date==it.date.toString()
                val r = if(params.rid==null) true else params.rid==it.rid
                if(d && r)
                    list.add(ActivitiesListResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid))
            }
        }
        list = if (params.orderBy==ASC) list.sortedBy { it.duration }.toMutableList()
               else list.sortedByDescending { it.duration }.toMutableList()

        val limitIndex = paging.skip+paging.limit-1
        val iterator = list.iterator()
        var index = 0
        iterator.forEach { _ ->
            if(index !in paging.skip..limitIndex) iterator.remove()
            index++
        }
        return list
    }

    override fun updateActivity(aid: Int, request: ActivityUpdateRequest) : ActivityResponse { //test
        activities.forEach {
            if(it.aid==aid){
                if(request.date!=null) it.date = LocalDate.parse(request.date)
                if(request.duration!=null) it.duration = LocalTime.parse(request.duration)
                //if(request.route!=null) it.rid = request.route
                return ActivityResponse(it.aid, it.date.toString(), it.duration.toString(), it.uid, it.sid, it.rid)
            }
        }
        return getActivity(aid)
    }
}