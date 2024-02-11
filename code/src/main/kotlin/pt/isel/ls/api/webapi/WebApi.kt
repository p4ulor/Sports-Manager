package pt.isel.ls.api.webapi

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ls.api.service.Paging
import pt.isel.ls.api.utils.*
import kotlin.reflect.KClass

val logger: Logger = LoggerFactory.getLogger("pt.isel.ls.api.sports-web-api")
const val LIMIT = 20
const val UPPERBOUND_LIMIT = 200
const val SKIP = 0
abstract class WebApi {

    fun logRequest(request: Request) {
        logger.info(
            "incoming request: method={}, uri={}, content-type={} accept={}",
            request.method,
            request.uri,
            request.header("content-type"),
            request.header("accept")
        )
    }

    protected fun getID_InURI(request: Request, id: String) : Int { //get String in URI as located in { } according to the Route's definitions
        return request.path(id)?.toIntOrNull() ?:
        throw BadRequestException("$id is missing or it's of the wrong type in the request's URI")
    }

    protected fun getIntParamInQuery(request: Request, id: String, isRequired: Boolean = true) : Int? {
        val valueString = request.query(id)
        val value = valueString?.toIntOrNull()
        if(isRequired && valueString.isNullOrEmpty()) throw BadRequestException("$id is missing")
        else if(isRequired && value==null) throw BadRequestException("$id is of the wrong type in the query. Found $valueString")
        return value
    }

    protected fun getStringParamInQuery(request: Request, id: String, isRequired: Boolean = true) : String? {
        val value = request.query(id)
        if(isRequired && value==null)
            throw BadRequestException("$id is missing or it's of the wrong type in the query")
        return value
    }

    private fun getSkip(reqSkip: String?) : Int {
        if (reqSkip != null)
            return reqSkip.toIntOrNull() ?: throw BadRequestException("Skip is not an Integer")
        return SKIP
    }

    private fun getLimit(reqLimit: String?) : Int {
        if (reqLimit != null)
            return reqLimit.toIntOrNull() ?: throw BadRequestException("Limit is not an Integer")
        return LIMIT
    }

    protected fun getPaging(request: Request) : Paging {
        val reqLimit = request.query("limit")
        val reqSkip = request.query("skip")
        return Paging(getLimit(reqLimit), getSkip(reqSkip))
    }

    protected fun getAuthorization_uuid(request: Request) : String {
        return request.header("Authorization")?.split(" ")?.get(1) ?: //split " " and get(1) because it returns-> "Bearer 359fcc7e-3c63-4258-8840-30e408494ba0"
        throw BadRequestException("authorization header missing")
    }

    protected inline fun <reified T> getRequestBody(request: Request) : T {
        try { return Json.decodeFromString<T>(request.bodyString()) }
        catch (e: Exception){
            val fields = T::class.java.declaredFields.toList()
            val message = "request body doesn't follow the guidelines. Please provide a request body with $fields. Extra error message: $e"
            logger.info(message)
            throw BadRequestException(message) }
    }

    // Podia-se fazer com q T extendesse de algo q as nossas Response data classes implementassem para q este método fosse mais restrito, mas se calhar nao é preciso ir tao longe
    protected inline fun <reified T> routeMethodCall(request: Request, status: Status, response: () -> T) : Response {
        logRequest(request)
        return try {
            Response(status).header("content-type", "application/json")
                .body(Json.encodeToString(response()))
        } catch (e: HTTPException) {
            val appError = AppError(e.status.code, e.name, e.message.toString())
            Response(e.status).body(Json.encodeToString(appError))
        }
    }
}
