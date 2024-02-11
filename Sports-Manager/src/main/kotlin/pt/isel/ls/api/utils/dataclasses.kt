package pt.isel.ls.api.utils

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class User(
    val uid: Int,
    val name: String,
    val email: String,
    val uuid: String,
    val hashedPassword: String
)

@Serializable
data class Sport(
    val sid: Int,
    val name: String,
    var description: String?,
    val uid: Int
)

@Serializable
data class Route(
    val rid: Int,
    val startLocation: String,
    var endLocation: String,
    var distance: Float,
    val uid: Int
)

@Serializable
data class Activity(
    val aid: Int,
    var date: LocalDate,
    @Contextual var duration: LocalTime,
    val uid: Int,
    val sid: Int,
    var rid: Int?
) {
    constructor(aid: Int, date: String, duration: String, uid: Int, sid: Int, rid: Int?) :
            this(aid, LocalDate.parse(date), LocalTime.parse(duration), uid, sid, rid)

    constructor(aid: Int, date: String, duration: LocalTime, uid: Int, sid: Int, rid: Int?) :
            this(aid, LocalDate.parse(date), duration, uid, sid, rid)
}