package pt.isel.ls.api.webapi

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.api.service.UserService
import pt.isel.ls.api.utils.*

class UserWebApi(private val userService: UserService) : WebApi() {

    val usersRoutes = routes(
        "users" bind Method.POST to ::createUser, //requires requestBody
        "users" bind Method.GET to ::getUsers, //supports paging, and "Get the list of users that have an activity" and have a certain rid and sid (both or none requirement).
        "users/{uid}" bind Method.GET to ::getUser, //requires uid in path
        "login" bind Method.POST to ::loginUser
    )

    private fun createUser (request: Request) : Response {
        return routeMethodCall(request, Status.CREATED) {
            val requestBody = getRequestBody<UserCreateRequest>(request)
            userService.addUser(requestBody)
        }
    }

    private fun getUsers(request: Request) : Response { // GET /users?sid=1&rid=1&limit=5&skip=1
        return routeMethodCall(request, Status.OK) {
            val paging = getPaging(request)
            var sid = getIntParamInQuery(request, "sid", false)
            var rid = getIntParamInQuery(request, "rid", false)
            if(sid != null) {
                if (rid == null) throw BadRequestException("Must send both sid and rid or none")
            } else if (rid != null) throw BadRequestException("Must send both sid and rid or none")
            userService.getUsers(UsersGetsParams(sid, rid), paging)
        }
    }

    private fun getUser(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val uid = getID_InURI(request, "uid")
            userService.getUser(uid)
        }
    }

    private fun loginUser(request: Request) : Response {
        return routeMethodCall(request, Status.OK) {
            val requestBody = getRequestBody<UserLoginRequest>(request)
            userService.loginUser(requestBody)
        }
    }
}
