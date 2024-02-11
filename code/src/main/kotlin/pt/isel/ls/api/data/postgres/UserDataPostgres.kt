package pt.isel.ls.api.data.postgres

import org.postgresql.ds.PGSimpleDataSource
import org.postgresql.util.PSQLException
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class UserDataPostgres(private val dataSource: PGSimpleDataSource): UserData {

    @Throws(BadRequestException::class)
    override fun addUser(userRequest: UserCreateRequest): UserAddedResponse {
        val uuid: UUID = UUID.randomUUID()
        val getGeneratedKeys: Int
        if(isEmailTaken(userRequest.email)) throw BadRequestException("Email is already taken")
        dataSource.connection.use {
            val query = it.prepareStatement(
                "INSERT INTO \"user\" VALUES" +
                        "(DEFAULT,'${userRequest.name}','${userRequest.email}','${uuid}', '${userRequest.password}')",
                Statement.RETURN_GENERATED_KEYS
            )
            query.executeUpdate()
            val rs = query.getGeneratedKeys()
            getGeneratedKeys = if (rs.next()) rs.getInt(1) else -1
            return UserAddedResponse(getGeneratedKeys, uuid.toString())
        }
    }

    @Throws(NotFoundException::class)
    override fun getUser(uid: Int): UserResponse {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT uid, name, email " +
                        " FROM \"user\" " +
                        " WHERE uid=${uid}"
            )
            result = query.executeQuery()
        }
        if (result.next()) {
            val getuid = result.getInt("uid")
            val getname = result.getString("name")
            val getemail = result.getString("email")
            return UserResponse(getuid, getname, getemail)
        }
        throw NotFoundException("user not found")
    }

    override fun getUsers(paging: Paging): List<UserListResponse> {
        val result: ResultSet
        val list = mutableListOf<UserListResponse>()
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT uid, name " +
                        "FROM \"user\" " +
                        "ORDER BY uid " +
                        "LIMIT ${paging.limit} OFFSET ${paging.skip}"
            )
            result = query.executeQuery()
        }
        while (result.next()) {
            val uid = result.getInt("uid")
            val name = result.getString("name")
            list.add(UserListResponse(uid, name))
        }
        return list
    }

    @Throws(AuthenticationException::class)
    override fun authenticateUser(uuid: String): Int {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT uid " +
                        " FROM \"user\" " +
                        " WHERE uuid='${uuid}'"
            )
            result = query.executeQuery()
        }
        if (result.next()) {
            return result.getInt("uid")
        }
        throw AuthenticationException("authentication error")
    }

    override fun loginUser(loginRequest: UserLoginRequest): UserLoginResponse {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(
                " SELECT uuid, hashedPassword " +
                        " FROM \"user\" " +
                        " WHERE email='${loginRequest.email}'"
            )
            result = query.executeQuery()
        }
        if (result.next()) {
            if(result.getString("hashedPassword")==loginRequest.password)
                return UserLoginResponse(result.getString("uuid"))
            throw AuthenticationException("Wrong password")
        }
        throw NotFoundException("User not found")
    }

    private fun isEmailTaken(email: String): Boolean {
        val result: ResultSet
        dataSource.connection.use {
            val query = it.prepareStatement(" SELECT email FROM \"user\" WHERE email='${email}'")
            result = query.executeQuery()
        }
        return result.next()
    }
}