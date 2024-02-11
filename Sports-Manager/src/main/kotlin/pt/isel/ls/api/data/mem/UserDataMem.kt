package pt.isel.ls.api.data.mem

import pt.isel.ls.api.data.UserData
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import java.util.*

class UserDataMem : UserData {

    @Throws(BadRequestException::class)
    override fun addUser(userRequest: UserCreateRequest) : UserAddedResponse {
        val uuid: UUID = UUID.randomUUID()
        val email = userRequest.email
        if(users.find{it.email == email} != null) throw BadRequestException("email already in use")
        val user = User(userId++, userRequest.name, email, uuid.toString(), userRequest.password)
        users.add(user)
        return UserAddedResponse(user.uid,user.uuid)
    }

    @Throws(NotFoundException::class)
    override fun getUser(uid: Int) : UserResponse {
        val user = users.find { it.uid == uid } ?:
        throw NotFoundException("user not found")
        return UserResponse(user.uid,user.name,user.email)
    }

    override fun getUsers(paging: Paging) : List<UserListResponse>{
        val list = mutableListOf<UserListResponse>()
        val limitIndex = paging.skip+paging.limit-1
        users.forEachIndexed { index, it ->
            if(index in paging.skip..limitIndex)
                list.add(UserListResponse(it.uid,it.name))
        }
        return list
    }

    @Throws(AuthenticationException::class)
    override fun authenticateUser(uuid: String): Int { //Usado na adiçao e update de routes, sports e activities
        val user = users.find { it.uuid == uuid } ?: throw AuthenticationException("authentication error")
        return user.uid
    }

    @Throws(NotFoundException::class, AuthenticationException::class)
    override fun loginUser(loginRequest: UserLoginRequest) : UserLoginResponse {
        val user = users.find { it.email==loginRequest.email} ?: throw NotFoundException("User not found")
        if(user.hashedPassword==loginRequest.password) return UserLoginResponse(user.uuid)
        throw AuthenticationException("Wrong password")
    }
}
