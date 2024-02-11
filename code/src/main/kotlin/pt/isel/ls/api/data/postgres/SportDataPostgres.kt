package pt.isel.ls.api.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.api.data.SportData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.sql.ResultSet
import java.sql.Statement

class SportDataPostgres(private val dataSource: PGSimpleDataSource): SportData {
    override fun addSport(sportRequest: SportsCreateRequest, uid: Int): SportAddedResponse {
        val getGeneratedKeys: Int
        dataSource.connection.use {
            val query = it.prepareStatement(
                "INSERT INTO SPORT VALUES" +
                        "(DEFAULT,'${sportRequest.name}','${sportRequest.description}',${uid})",
                Statement.RETURN_GENERATED_KEYS
            )
            query.executeUpdate()
            val rs = query.getGeneratedKeys()
            getGeneratedKeys = if (rs.next()) rs.getInt(1) else -1
        }
        return SportAddedResponse(getGeneratedKeys)
    }

    override fun getSport(sid: Int): SportResponse {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT sid,name,description,uid\n" +
                        " FROM SPORT\n" +
                        " WHERE sid=${sid}"
            )
            result = query.executeQuery()
        }
        if (result.next()) {
            val getsid = result.getInt("sid")
            val getname = result.getString("name")
            val getdescription = result.getString("description")
            val getuid = result.getInt("uid")
            return SportResponse(getsid, getname, getdescription, getuid)
        }
        throw NotFoundException("sport not found")
    }

    override fun getSports(params: SportGetParams, paging: Paging) : List<SportsListResponse> {
        val result: ResultSet
        val list = mutableListOf<SportsListResponse>()
        val nameLike = if(params.name!=null) "WHERE name LIKE '${params.name}' " else ""
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT sid,name " +
                        "FROM SPORT " +
                        "$nameLike" +
                        "ORDER BY sid " +
                        "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) {
            val sid = result.getInt("sid")
            val name = result.getString("name")
            list.add(SportsListResponse(sid, name))
        }
        return list
    }

    override fun updateSport(sid: Int, request: SportsUpdateRequest) : SportResponse {
        //disableConstraints(dataSource)
        var descriptionSet = "description = '${request.description}'"
        dataSource.connection.use {
            it.prepareStatement("UPDATE SPORT SET $descriptionSet WHERE sid=$sid").executeUpdate()
        }
        return getSport(sid)
    }
}
