package pt.isel.ls.api.service

import pt.isel.ls.api.data.ActivityData
import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.utils.*
import java.util.regex.Pattern

open class UserService(private val userData: UserData, private val activityData: ActivityData) : UserServiceI {

    private val pwManagement = PasswordManagement()

    @Throws(BadRequestException::class)
    override fun addUser(userRequest: UserCreateRequest) : UserAddedResponse {
        if(!emailFormatCheck(userRequest.email))
            throw BadRequestException("email format error (e.g. example@sportsapi.com)")
        userRequest.password = pwManagement.encrypt(userRequest.password)
        return userData.addUser(userRequest)
    }

    @Throws(NotFoundException::class)
    override fun getUser(uid: Int) : UserResponse {
        return userData.getUser(uid)
    }

    override fun getUsers(params: UsersGetsParams, paging: Paging) : List<UserListResponse> {
        if(params.sid!=null && params.rid!=null) { //visto que este pedido tem de aceder à entidade activity, decidi fazer assim, em vez de em UserDataPostgres/Mem, estar a ter de aceder aos dados de Activity
            val usersIdsInThoseActivities = activityData.getActivitiesWithSportAndRoute(params.sid, params.rid, paging)
            var listsOfUsers = mutableListOf<UserListResponse>()
            usersIdsInThoseActivities.forEach {
                val user = getUser(it.uid) //faço isto pq preciso do name para poder instanciar UserListResponse
                listsOfUsers.add(UserListResponse(user.uid, user.name))
            }
            return listsOfUsers
        }
        return userData.getUsers(paging)
    }

    @Throws(NotFoundException::class, AuthenticationException::class, BadRequestException::class)
    override fun loginUser(loginRequest: UserLoginRequest) : UserLoginResponse {
        if(!emailFormatCheck(loginRequest.email))
            throw BadRequestException("email format error (e.g. example@sportsapi.com)")
        loginRequest.password = pwManagement.encrypt(loginRequest.password)
        return userData.loginUser(loginRequest)
    }

    private fun emailFormatCheck(emailAddress:String) : Boolean {
        val regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.compile(regexPattern).matcher(emailAddress).matches()
    }
}
