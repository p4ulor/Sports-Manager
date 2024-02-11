package pt.isel.ls.api.utils

import kotlinx.serialization.Serializable
import java.lang.reflect.Field

@Serializable
data class UserCreateRequest(
    val name: String,
    val email: String,
    var password: String //it's var so that it's hashed in Service
){ init { checkIsFilled(arrayOf("name", "email", "password"), this)}}

@Serializable
data class UserLoginRequest(
    val email: String,
    var password: String //it's var so that it's hashed in UserService
){ init { checkIsFilled(arrayOf("email", "password"), this)}}



@Serializable
data class RoutesCreateRequest(
    val startLocation: String,
    val endLocation: String,
    var distance: Float
){ init { checkIsFilled(arrayOf("startLocation", "endLocation"), this)}}

@Serializable
data class RouteUpdateRequest(
    val endLocation: String?,
    var distance: Float?
){ init { checkIsFilled(arrayOf("endLocation"), this)}}



@Serializable
data class SportsCreateRequest(
    val name: String,
    val description: String
){ init { checkIsFilled(arrayOf("name", "description"), this)}}

@Serializable
data class SportsUpdateRequest(
    val description: String
){ init { checkIsFilled(arrayOf("description"), this)}}



@Serializable
data class ActivityCreateRequest(
    val date: String,
    val duration: String,
    val rid: Int?
){ init { checkIsFilled(arrayOf("date", "duration"), this)}}

@Serializable
data class ActivititiesToDeleteRequest(val activities: Array<Aids>) {
    @Serializable
    data class Aids(val aid: Int)
    fun getAids() : MutableList<Int> {
        val arrayOfInts = mutableListOf<Int>()
        activities.forEach {
            arrayOfInts.add(it.aid)
        }
        return arrayOfInts
    }
}

@Serializable
data class ActivityUpdateRequest(
    val date: String?,
    val duration: String?,
    //val rid: Int? //mais tarde?
){ init { checkIsFilled(arrayOf("date", "duration"), this)}}

// NOTE: this method is executed AFTER "fun getRequestBody(request)"
// O que significa q ao receber um pedido como: {"startLocation":"aaa","endLocation":"bbb","distance":""}
// Nao vamos conseguir ver se 'distance' está vazio pq o parser em primeiro lugar nao vai conseguir converter "" para float. TODO se calhar era melhor rever isto (fazer com q estas classes so tivessem campos String, e converter para Floats e afins no fim). E acho q ver isto do lado cliente nao parece bem, pq as coisas ficariam assimetricas
private fun checkIsFilled(namesOfTheFields: Array<String>, obj: Any){
    val clss = obj::class.java //igual a: obj::class.java
    namesOfTheFields.forEach { v ->
        try {
            val field: Field = clss.getDeclaredField(v) //ele nao retorna null se nao encontra, dá exceçao
            field.isAccessible = true //goes over kotlin.reflect.full.IllegalCallableAccessException . field.trySetAccessible() -> only exists from java 9 forward
            val value = field.get(obj)
            if(value is String){
                if(value.isEmpty() || value.isBlank())
                    throw BadRequestException("The field '${field.name}' is empty or blank in the request body")
            }
        } catch(e: NoSuchFieldException){ println(e) }
    }
}
