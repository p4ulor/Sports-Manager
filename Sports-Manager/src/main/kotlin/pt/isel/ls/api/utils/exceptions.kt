package pt.isel.ls.api.utils

import org.http4k.core.Status

abstract class HTTPException(message: String) : Exception(message){
    abstract val status: Status
    abstract val name: String
}

class BadRequestException(message: String) : HTTPException(message){
    override val status: Status
        get() = Status.BAD_REQUEST
    override val name: String
        get() = "Bad Request"
}

class NotFoundException(message: String) : HTTPException(message){
    override val status: Status
        get() = Status.NOT_FOUND
    override val name: String
        get() = "Not Found"
}

    class AuthenticationException(message: String) : HTTPException(message){
    override val status: Status
        get() = Status.UNAUTHORIZED
    override val name: String
        get() = "Unauthorized"
}

class ForbiddenException(message: String) : HTTPException(message){
    override val status: Status
        get() = Status.FORBIDDEN
    override val name: String
        get() = "Forbidden"
}
