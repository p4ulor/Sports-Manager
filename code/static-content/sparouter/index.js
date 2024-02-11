import router from "./router.js";
import handlers from "./handlers.js";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){

    //TODO com ou sem barra exemplo users ou users/

    router.addRouteHandler("home", handlers.getHome)
    router.addRouteHandler("users", handlers.getUsers)
    router.addRouteHandler("users/:id", handlers.getUser)
    router.addRouteHandler("users/:id/activities", handlers.getActivitiesOfAUser)
    router.addRouteHandler("routes", handlers.getRoutes)
    router.addRouteHandler("routes/create", handlers.createRoute)
    router.addRouteHandler("routes/:id", handlers.getRoute)
    router.addRouteHandler("routes/:id/update", handlers.updateRoute)
    router.addRouteHandler("sports", handlers.getSports)
    router.addRouteHandler("sports/create", handlers.createSport)
    router.addRouteHandler("sports/:id", handlers.getSport)
    router.addRouteHandler("sports/:id/update", handlers.updateSport)
    router.addRouteHandler("activities", handlers.getActivities)
    router.addRouteHandler("activities/create", handlers.createActivity)
    router.addRouteHandler("activities/:id", handlers.getActivity)
    router.addRouteHandler("activities/:id/update", handlers.updateActivity)
    router.addRouteHandler("activities/:id/delete", handlers.deleteActivity)
    router.addRouteHandler("login", handlers.loginUser)

    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
}

function hashChangeHandler(){

    const mainContent = document.getElementById("mainContent")
    const path =  window.location.hash.replace("#", "")

    const paramsAndHandler = router.getRouteHandler(path)
    const handler = paramsAndHandler.handler
    console.log(handler)
    //console.log(`params: ${idAndHandler.params} e handler: ${handler.name}`)
    if (handler)
        handler(mainContent, paramsAndHandler.params) //calls whatever function that was set
}