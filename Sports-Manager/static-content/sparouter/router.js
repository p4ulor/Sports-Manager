const routes = []

function getQueryMapper(query){
    const map = {}
    if(query){
        //query = query.split("?")[1]
        const querySplit = query.split("&")
        querySplit.forEach(param => {
            const [name,value] = param.split("=")
            map[name] = value
        })
    }
    return map
}

function addRouteHandler(path, handler) {
    routes.push({path, handler})
}

let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" }

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

function getRouteHandler(hash) {
    const [path, query] = hash.split("?")
    const queryMap = getQueryMapper(query)
    const pathSplits = path.split("/")
    var pathMap = {}
    var routeFound = false
    var handler = null
    routes.forEach(
        it => {
            if(routeFound) return
            const routeSplits = it.path.split("/")
            if(pathSplits.length === routeSplits.length){
                routeFound = true
                for (let i = 0; i < pathSplits.length; i++) {
                    if(routeSplits[i].charAt(0) === ":"){
                        pathMap[routeSplits[i].split(":")[1]] = pathSplits[i]
                    } else if(pathSplits[i] !== routeSplits[i]){
                        routeFound = false
                        break;
                    }
                }
                if(routeFound) handler = it.handler
                else pathMap = {}
            }
        }
    )
    return handler ? {params: {path: pathMap, query: queryMap} , handler: handler } : notFoundRouteHandler
}                            //exemplo: {oath: users} query: {limit: 1, skip: 2}        


const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

export default router