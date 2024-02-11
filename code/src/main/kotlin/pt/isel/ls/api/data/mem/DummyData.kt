package pt.isel.ls.api.data.mem

import pt.isel.ls.api.utils.Activity
import pt.isel.ls.api.utils.Route
import pt.isel.ls.api.utils.Sport
import pt.isel.ls.api.utils.User

val users = mutableListOf(
    User(1,"Filipe","filipesporting@hotmail.com","8304c3c9-c4ca-4b1a-848c-74b5f415a62f", "11j/PVKLTn2ARnNnsfACIA=="), //pw = filipe123
    User(2,"Luis", "luisbenfica@hotmail.com","c9c49334-b452-11ec-b909-0242ac120002", "0ndsAdTKMeBWsbR5AtRn4Q=="), //pw = luis123
    User(3,"Daniel", "danielporto@gmail.com","d83a659c-b452-11ec-b909-0242ac120002", "WvMo4G/ZAYrENqPUXkdD9Q==") //pw = daniel123
)

val routes = mutableListOf(
    Route(1,"Chelas","Alameda",100f, 1),
    Route(2,"Olivais","Cabo Ruivo",150f, 3),
    Route(3,"Olaias","Aeroporto",130f, 2)
)

val sports = mutableListOf(
    Sport(1,"Soccer","Is a sport you play with your feet",1),
    Sport(2,"Basketball","Is a sport you play with hands and put ball in basket",3),
    Sport(3,"Pool","Pool is sports played on a table with six pockets, which balls are deposited.w",2)
)

val activities = mutableListOf(
    Activity(1, "2010-05-03", "13:10:10.200",3, 1, 2),
    Activity(2, "2010-05-04", "23:10:11.200", 1, 2, 3),
    Activity(3, "2010-05-05", "23:10:12.200", 2, 3, 1),
    Activity(4, "2010-05-06", "23:10:13.200", 2, 3, 1),
    Activity(5, "2010-05-07", "23:10:14.200", 2, 3, 1),
    Activity(6, "2010-05-08", "23:10:15.200", 1, 2, 2)
)
        // for SPA tests
.also {
    //it.add(Activity(7, "2010-05-08", "10:50:14.200", 3, 3, 1))
}

var userId = users.size+1
var sportId = sports.size+1
var routeId = routes.size+1
var activityId = activities.size+1