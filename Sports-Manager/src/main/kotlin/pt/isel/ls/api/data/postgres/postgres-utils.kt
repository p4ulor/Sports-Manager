package pt.isel.ls.api.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import java.sql.ResultSet

fun getTuples(dataSource: PGSimpleDataSource, table: String, condition: String) : ResultSet {
    val result: ResultSet
    dataSource.connection.use {
        val query = it.prepareStatement("SELECT * FROM ${table.uppercase()} WHERE $condition")
        result = query.executeQuery()
    }
    return result
}

fun insertActivities(dataSource: PGSimpleDataSource, tuples: ResultSet){
    while (tuples.next()) {
        val aid = tuples.getInt("aid")
        val date = tuples.getString("date")
        val duration = tuples.getString("duration")
        val uid = tuples.getInt("uid")
        val sid = tuples.getInt("sid")
        val rid = tuples.getInt("rid")
        dataSource.connection.use {
            val query = it.prepareStatement(
                "INSERT INTO ACTIVITY VALUES ($aid,'${date}','${duration}',${uid},${sid},${rid})"
            )
            query.executeUpdate()
        }
    }
}

fun deleteFromWhere(dataSource: PGSimpleDataSource, table: String, condition: String){
    dataSource.connection.use {
        val query = it.prepareStatement("DELETE FROM ${table.uppercase()} VALUES WHERE $condition")
        query.executeUpdate()
    }
}

fun disableConstraints(dataSource: PGSimpleDataSource) {
    dataSource.connection.use {
        it.prepareStatement("ALTER TABLE ACTIVITY DROP CONSTRAINT IF EXISTS fk_rid").executeUpdate()
        it.prepareStatement("ALTER TABLE ACTIVITY DROP CONSTRAINT IF EXISTS fk_sid").executeUpdate()
    }
}
fun enableConstraints(dataSource: PGSimpleDataSource) { // A razao pela qual eu nao faço enable dps de fazer enable em certos sitios, é porque nao funcionava nao sei pq... se calhar pq ha um delay?
    disableConstraints(dataSource)
    dataSource.connection.use {
        it.prepareStatement("ALTER TABLE ACTIVITY " +
                "ADD CONSTRAINT fk_rid FOREIGN KEY(rid) " +
                "REFERENCES ROUTE(rid) ON UPDATE CASCADE ON DELETE CASCADE").executeUpdate()

        it.prepareStatement("ALTER TABLE ACTIVITY " +
                "ADD CONSTRAINT fk_sid FOREIGN KEY(sid) " +
                "REFERENCES SPORT(sid) ON UPDATE CASCADE ON DELETE CASCADE").executeUpdate()
    }
}

