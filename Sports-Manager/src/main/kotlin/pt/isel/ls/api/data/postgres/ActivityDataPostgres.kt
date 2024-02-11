package pt.isel.ls.api.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.sql.ResultSet
import java.sql.Statement

class ActivityDataPostgres(private val dataSource: PGSimpleDataSource): ActivityData {

    override fun addActivity(sid: Int, activityRequest: ActivityCreateRequest, uid: Int): ActivityAddedResponse {
        var aid = -1
        var rid = "null"
        if(activityRequest.rid!=null) rid = activityRequest.rid.toString()
        //enableConstraints(dataSource)
        dataSource.connection.use {
            val query = it.prepareStatement(
                "INSERT INTO ACTIVITY VALUES" +
                        "(DEFAULT,'${activityRequest.date}','${activityRequest.duration}',${uid},${sid},${rid})",
                Statement.RETURN_GENERATED_KEYS
            )
            query.executeUpdate()
            val key = query.generatedKeys

            if(key.next())
                aid = key.getInt("aid")
        }
        return ActivityAddedResponse(aid)
    }

    override fun getActivitySport(sid: Int, paging: Paging): List<ActivitiesListResponse> {
        val result: ResultSet
        val list = mutableListOf<ActivitiesListResponse>()
        dataSource.connection.use {
            val query = it.prepareStatement(
                "SELECT aid, date, duration, uid, sid " +
                    "FROM ACTIVITY " +
                    "WHERE sid=${sid} ORDER BY aid " +
                    "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) { list.add(getActivityListResponse(result)) }
        return list
    }

    override fun getActivitiesWithSportAndRoute(sid: Int, rid: Int, paging: Paging): List<ActivitiesListUsers> {
        val result: ResultSet
        val list = mutableListOf<ActivitiesListUsers>()
        dataSource.connection.use {
            val query = it.prepareStatement(
                "SELECT DISTINCT uid FROM (SELECT uid " +
                    "FROM ACTIVITY " +
                    "WHERE sid=${sid} AND rid=${rid}" +
                    "ORDER BY duration " +
                    "LIMIT ${paging.limit} OFFSET ${paging.skip}) AS sub"
            )
            result = query.executeQuery()
        }
        while (result.next()) {
            val uid = result.getInt("uid")
            list.add(ActivitiesListUsers(uid))
        }
        return list
    }

    @Throws(NotFoundException::class)
    override fun getActivity(aid: Int): ActivityResponse {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(
                "SELECT aid, date, duration, uid,sid, rid " +
                    "FROM ACTIVITY " +
                    "WHERE aid=${aid}"
            )
            result = query.executeQuery()
        }
        if (result.next()) {
            val date = result.getString("date")
            val duration = result.getString("duration")
            val uid = result.getInt("uid")
            val sid = result.getInt("sid")
            val rid = result.getInt("rid")
            return ActivityResponse(aid, date, duration, uid, sid, rid)
        }
        throw NotFoundException("activity not found")
    }

    @Throws(NotFoundException::class)
    override fun deleteActivity(aid: Int): ActivityResponse {
        val a = getActivity(aid)
        dataSource.connection.use {
            it.prepareStatement("DELETE FROM ACTIVITY WHERE aid=${aid}").executeUpdate()
        }
        return ActivityResponse(a.aid, a.date, a.duration, a.uid, a.sid, a.rid)
    }

    override fun deleteActivities(aids: MutableList<Int>) : List<ActivitiesListResponse> { //todo: otomizar para nao fazer connection successivas?
        val listOfActivitiesRemoved = mutableListOf<ActivitiesListResponse>()
        aids.forEach {
            val a = deleteActivity(it)
            listOfActivitiesRemoved.add(ActivitiesListResponse(a.aid, a.date, a.duration, a.uid, a.sid))
        }
        return listOfActivitiesRemoved
    }

    override fun getActivityUser(uid: Int, paging: Paging): List<ActivitiesListResponse> {
        val result: ResultSet
        val list = mutableListOf<ActivitiesListResponse>()
        dataSource.connection.use {
            val query = it.prepareStatement(
                "SELECT aid, date, duration, uid, sid " +
                    "FROM ACTIVITY " +
                    "WHERE uid=${uid} " +
                    "ORDER BY aid " +
                    "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) { list.add(getActivityListResponse(result)) }
        return list
    }

    override fun getActivities(params: GetAllActivitiesParams, paging: Paging): List<ActivitiesListResponse> {
        val result: ResultSet
        val list = mutableListOf<ActivitiesListResponse>()
        val dateQuery = if(params.date!=null) "AND date='${params.date}'"  else ""
        val ridQuery = if(params.rid!=null) "AND rid=${params.rid}"  else ""
        dataSource.connection.use {
            val query = it.prepareStatement(
                "SELECT * FROM ACTIVITY " +
                    "WHERE sid=${params.sid} $dateQuery $ridQuery " +
                    "ORDER BY duration ${params.orderBy} " +
                    "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) { list.add(getActivityListResponse(result)) }
        return list
    }

    override fun updateActivity(aid: Int, request: ActivityUpdateRequest) : ActivityResponse { //test
        if(request.date!=null || request.duration!=null /*|| request.rid!=null*/) {
            //enableConstraints(dataSource)
            var dateSet = if(request.date!=null) "date = '${request.date}'" else ""

            var durationSet = if(request.duration!=null) "duration = '${request.duration}'" else ""
            if(durationSet.isNotEmpty() && dateSet.isNotEmpty()) dateSet = "$dateSet,"

            //var routeSet = if(request.rid!=null) "route = ${request.rid}" else null
            //if(routeSet!=null) durationSet?.plus(",")

            dataSource.connection.use {
                val query = it.prepareStatement("UPDATE ACTIVITY SET $dateSet $durationSet WHERE aid=$aid")
                query.executeUpdate()
            }
        }
        return getActivity(aid)
    }

    private fun getActivityListResponse(r: ResultSet): ActivitiesListResponse { //isto popou mais 16 linhas
        return ActivitiesListResponse(r.getInt("aid"), r.getString("date"),
            r.getString("duration"), r.getInt("uid"), r.getInt("sid"))
    }
}
