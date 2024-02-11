/*
a - anchor element
p - paragraph
h - heading object
ol - ordered list
ul - unordered list
li - list item
div - A container tag that is used to define a division or a section in an HTML
*/

import utils from "./handler-utils.js"
const API_BASE_URL = "/" //PARA O HEROKU e LOCAL (aparentement tb funciona)  -> "/" , ALTERNATIVA -> http://localhost:9000/

let userLoginNameEmail = undefined

function getHome(mainContent){
    const headerDiv = utils.createHeader("Home")
    if(userLoginNameEmail){
        headerDiv.append(document.createElement("br"))
        headerDiv.append(utils.createHeader("Welcome "+userLoginNameEmail, "h3"))
    }  
    mainContent.replaceChildren(headerDiv)
}

function getUsers(mainContent, params) {
    const div = utils.createHeader("Users")
    if (params.query != undefined && Object.keys(params.query).length != 0) {
        params = utils.getFormattedQuery(params)
        fetch(API_BASE_URL + `users${params.query.formatted}`).then(res => {
            if (!res.ok) {
                res.json().then(messsage => {
                        alert(`Error ${res.status}, ${res.statusText}, ${messsage.message}`)
                    }
                )
            } else {
                res.json().then(UserListResponse => {
                    const div = utils.createHeader("User at the specified route and sport ordered by duration of activity")

                    UserListResponse.forEach(u => {
                        const content = utils.createElement("p", /*"uid"+u.uid+" with */"name: " + u.name, `#users/${u.uid}`)
                        div.appendChild(content)
                    })
            
                    div.appendChild(utils.createNextPreviousLink("p", "Previous", "users", params))
                    div.appendChild(utils.createNextPreviousLink("p", "Next", "users", params))
                    mainContent.replaceChildren(div)
                })
            }
        })
    } else if (!params.query !== {}) { //entao é modo UserRankingSearch TODO: fazer search na mesma pagina
        var delay = makeDelay(500)
        var name = utils.createInputElement("Sport name")
        name.onkeyup = () => {
            delay(() => {
                searchUsers(mainContent)
            })
        }

        var start = utils.createInputElement("StartLocation")
        start.onkeyup = () => {
            delay(() => {
                searchUsers(mainContent)
            })
        }

        var end = utils.createInputElement("EndLocation")
        end.onkeyup = () => {
            delay(() => {
                searchUsers(mainContent)
            })
        }

        div.appendChild(name)
        div.appendChild(start)
        div.appendChild(end)
        //div.appendChild(utils.createButton("Search", searchUsers, mainContent))
    }
    mainContent.replaceChildren(div)
}

function makeDelay(ms) {
    var timer = 0;
    return function(callback){
        clearTimeout (timer);
        timer = setTimeout(callback, ms);
    }
}

function searchUsers(mainContent){
    const div = utils.createHeader("Matching sports", undefined, undefined, "matches")
    const sport = document.getElementById("Sport name").value
    const startLocation = document.getElementById("StartLocation").value
    const endLocation = document.getElementById("EndLocation").value
    var sportQuery = ""
    if(sport) sportQuery = `?name=${sport}`
    let dropDown
    let found = true
    fetch(API_BASE_URL + "sports"+sportQuery).then(res => res.json()).then(sports => {
        const values = []
        sports.forEach(s => { values.push({text: s.name, sid: s.sid })})
        if(values.length==0) {
            //alert("Couldln't find matching sports")
            found = false
        }
        dropDown = utils.createDropDown("Sports", values)
        div.appendChild(dropDown)
        }).then(() => { //meter .then() assim evita com q os elementos nao fiquem mal ordenados
            let startQuery = ""
            let endQuery = ""
            if(startLocation) startQuery = `startLocation=${startLocation}`
            if(endLocation) endQuery = `endLocation=${endLocation}`
            if(endQuery!="" && startQuery!="") startQuery.concat("&")
            
            fetch(API_BASE_URL + "routes"+`?${startQuery}${endQuery}`).then(res => res.json()).then(routes => {
                const div3 = utils.createHeader("Matching routes")
                const values = []
                routes.forEach(r => { values.push({text: `startLocation: ${r.startLocation}, endLocation: ${r.endLocation}`, rid: r.rid})})
                if(values.length==0) {

                    //alert("Couldln't find matching routes")
                    found = false
                }
                div3.appendChild(utils.createCheckBoxes("Routes selection", values))
                div.appendChild(div3)
                }).then(() => { 
                    if(found) div.appendChild(utils.createButton("Show users", getUsersInSelectedRIDandSID, dropDown))
                })
            
            let res = document.getElementById("matches")
            if(!res) mainContent.append(div)
            else res.replaceWith(div)
        })
}

function getUsersInSelectedRIDandSID(dropDown, newParams){
    const sid = dropDown.childNodes[dropDown.selectedIndex].id
    const sportsList = document.getElementsByTagName("input")
    let rid
    Array.from(sportsList).forEach(input => {
        if(input.checked) rid = input.value
    })
    if(rid==undefined) { alert("Must select a route in order to search"); return }
    console.log("sid="+sid+"rid="+rid)
    let sidQuery = ""
    let ridQuery = ""
    if(sid) sidQuery = `sid=${sid}`
    if(rid) ridQuery = `rid=${rid}`
    if(ridQuery!="" && sidQuery!="") sidQuery = sidQuery.concat("&")
    //let params = `${sidQuery}${ridQuery}`
    let params = {query: {rid: rid, sid: sid, page: 0}}
    if(newParams) params = newParams
    params = utils.getFormattedQuery(params)
        fetch(API_BASE_URL + `users${params.query.formatted}`).then(res => {
            if (!res.ok) {
                res.json().then(messsage => {
                        alert(`Error ${res.status}, ${res.statusText}, ${messsage.message}`)
                    }
                )
            } else {
                res.json().then(UserListResponse => {
                    const div = utils.createHeader("Matches", undefined, undefined, "results")

                    UserListResponse.forEach(u => {
                        const content = utils.createElement("p", /*"uid"+u.uid+" with */"name: " + u.name, `#users/${u.uid}`)
                        div.appendChild(content)
                    })

                    div.appendChild(specialNextPreviousLink("p", "Previous", params, dropDown))
                    div.appendChild(specialNextPreviousLink("p", "Next", params, dropDown))

                    let res = document.getElementById("results")
                    if(!res) mainContent.append(div)
                    else res.replaceWith(div)
                })
            }
    })
}

function specialNextPreviousLink(elementType, name, params, dropDown){
    let pageNumber = params.query.page
    if(name=='Previous'){
        if(pageNumber!=0) pageNumber--
    }
    else pageNumber++
    var element = document.createElement(elementType)
    let text = document.createTextNode(name); 
    const anchor = document.createElement("a")
    anchor.appendChild(text)
    
    anchor.onclick = function(){
        params.query.page = pageNumber
        getUsersInSelectedRIDandSID(dropDown, params)
    }
    anchor.href = "#users" //highlights the text as blue (as clickable)
    element.append(anchor)
    return element 
}

function getUser(mainContent, params) {
    fetch(API_BASE_URL + `users/${params.path.id}`).then(res => res.json()).then(user => {
        const table = utils.createTable(['Name', 'Email'], user, ["name", "email"])
        const btn = utils.createButtonLink("Get list of activities associated to this user",`#users/${user.uid}/activities`)
        table.appendChild(btn)
        mainContent.replaceChildren(table)
    })
}

function getSports(mainContent, params){
    params = utils.getFormattedQuery(params)
    fetch(API_BASE_URL + "sports"+params.query.formatted).
    then(res => res.json()).
    then(sports => {
        const div = utils.createHeader("Sports")
        div.appendChild(utils.createButtonLink("New Sport","#sports/create"))
        
        /* sports.forEach(s => {
            const content = utils.createElement("p", "Link to sports with name "+s.name, `#sports/${s.sid}`)
            div.appendChild(content)
        }) */

        const table = utils.createTable(
            ['Sports', 'Name'], 
            sports, 
            [{name: "sid", reference: {path: `#sports/`, id: "sid"}, fieldContent: "Link"}, "name"])
        
        div.append(table)
        div.appendChild(utils.createNextPreviousLink("p", "Previous", "sports", params.query.page))
        div.appendChild(utils.createNextPreviousLink("p", "Next", "sports", params.query.page))

        mainContent.replaceChildren(div)
    })
}

function getSport(mainContent, params) {
    fetch(API_BASE_URL + `sports/${params.path.id}`).then(res => res.json()).then(sport => {
        let creatorName
        fetch(API_BASE_URL + `users/${sport.uid}`).then(res => res.json()).then(user => {
            creatorName = user.name
        }).then(() => {
            const table = utils.createTable(
                ['Name', 'Description', 'Creator'], 
                sport, 
                ["name", "description", {name: "uid", reference: `#users/${sport.uid}`, fieldContent: creatorName}]
            )
            const btn = utils.createButtonLink("Update this sport", `#sports/${params.path.id}/update`)
    
            table.appendChild(btn)
            mainContent.replaceChildren(table)
        })
    })
}

function updateSport(mainContent, params){
    if(utils.isNotLoggedIn()) return
    const idSport = params.path.id
    const div = utils.createHeader("Update a sport")

    div.appendChild(utils.createInputElement("Description"))

    const button = utils.createButton("Update", function() {
        const input2 = document.getElementById("Description")
        const options = utils.makeRequest("PUT", {description : input2.value})
        fetch(API_BASE_URL + `sports/${idSport}`, options).then(res => res.json()).then(sports => {
            console.log(sports)
            window.location.hash = `sports/${idSport}`
        })
    })

    div.append(button)
    mainContent.replaceChildren(div)
}

function getRoutes(mainContent, params){
    params = utils.getFormattedQuery(params)
    fetch(API_BASE_URL + "routes"+params.query.formatted).then(res => res.json()).then(routes => {
        const div = utils.createHeader("Routes")
        div.appendChild(utils.createButtonLink("New Route","#routes/create"))

        utils.createElement("li", "New Activity", "#activities/create")

        const table = utils.createTable(
            ['Routes', 'startLocation', 'endLocation'], 
            routes, 
            [{name: "rid", reference: {path: `#routes/`, id: "rid"}, fieldContent: "Link"}, "startLocation", "endLocation"])

        div.append(table)
        div.appendChild(utils.createNextPreviousLink("p", "Previous", "routes", params.query.page))
        div.appendChild(utils.createNextPreviousLink("p", "Next", "routes", params.query.page))
        mainContent.replaceChildren(div)
    })
}

function getRoute(mainContent, params) {
    fetch(API_BASE_URL + `routes/${params.path.id}`).then(res => res.json()).then(route => {
        let creatorName
        fetch(API_BASE_URL + `users/${route.uid}`).then(res => res.json()).then(user => {
            creatorName = user.name
        }).then(() => {
            const table = utils.createTable(
                ['Start location', 'End location', 'Distance','Uid'], 
                route, 
                ["startLocation", "endLocation", "distance", {name: "uid", reference: `#users/${route.uid}`, fieldContent: creatorName}])
    
            const btn = utils.createButtonLink("Update this route",`#routes/${params.path.id}/update`)
            table.append(btn)
            mainContent.replaceChildren(table)
        })
    })
}

function getActivitiesOfASport(mainContent, params) {
    fetch(API_BASE_URL + `sports/${params.path.id}/activities`).then(res => res.json()).then(activities => {
        const div = utils.createHeader("Activities")
        activities.forEach(act => {
            const content = utils.createElement("p", "Link to activities at date "+act.date+" and duration"+ act.duration +"of the indicaded sport", `#sports/${act.sid}`)
            div.appendChild(content)
        })
        mainContent.replaceChildren(div)
    })
}

function getActivitiesOfAUser(mainContent, params) {
    fetch(API_BASE_URL + `users/${params.path.id}/activities`).then(res => res.json()).then(activities => {
    //fetch(API_BASE_URL + `users/1/activities`).then(res => res.json()).then(activities => {
        const div = utils.createHeader("Activities")
        activities.forEach(act => {
            div.appendChild(utils.createButtonLink(`Activity at date ${act.date} and duration ${act.duration}`,`#activities/${act.aid}`))
        })
        mainContent.replaceChildren(div)
    })
}

function getActivities(mainContent) { // requires query like activities?rid=3&orderBy=DESC TODO
    const uri = API_BASE_URL + "activities"
    const div = utils.createHeader("Search for activities")
    div.appendChild(utils.createButtonLink("New Activity","#activities/create"))

    mainContent.replaceChildren(div) 

    mainContent.appendChild(utils.createInputElement("Limit", "3"))
    mainContent.appendChild(utils.createInputElement("Skip", "0"))
    mainContent.appendChild(utils.createInputElement("sid"))
    mainContent.appendChild(utils.createInputElement("orderBy"))
    mainContent.appendChild(utils.createInputElement("date?"))
    mainContent.appendChild(utils.createInputElement("rid?"))

    const button = utils.createButton("Search", function() {
        const limit = document.getElementById("Limit").value
        const skip = document.getElementById("Skip").value
        const sid = document.getElementById("sid").value
        const orderBy = document.getElementById("orderBy").value
        const date = document.getElementById("date?").value
        const rid = document.getElementById("rid?").value
        var queryString = `${uri}?sid=${sid}&orderBy=${orderBy}&skip=${skip}&limit=${limit}`
        if(date) queryString = queryString.concat(`&date=${date}`)
        if(rid) queryString = queryString.concat(`&rid=${rid}`)
        getQueryResultActivities(queryString)
    })
    mainContent.appendChild(button)
}

function getQueryResultActivities(uri){
    fetch(uri).then(res => {
        if(!res.ok) {
            res.json().then(messsage => {
                alert(`Error ${res.status}, ${res.statusText}, ${messsage.message}`)}
            )
        } else res.json().then(activities => {
            const div = utils.createHeader("Activities")
            const table = utils.createTable(["Duration"], activities, [{name: "duration", reference: {path: `#activities/`, id: "aid"}}])
            div.appendChild(table)
            mainContent.replaceChildren(div)
        })
    })
}

 function getActivity(mainContent, params) {
    const actID = params.path.id
    fetch(API_BASE_URL + `activities/${actID}`).then(res => res.json()).then(activity => {
        utils.getIDsNamesFromUserSportRoute(API_BASE_URL, activity.uid, activity.sid, activity.rid).then(names => {
            console.log(names)
            const table = utils.createTable(
                ['Date', 'Duration', 'uid', 'sid', 'rid'], 
                activity,
                [   {name: "date"}, 
                    {name: "duration"}, 
                    {name: "uid", reference: `#users/${activity.uid}`, fieldContent: names.uidName},
                    {name: "sid", reference: `#sports/${activity.sid}`, fieldContent: names.sidName}, 
                    {name: "rid", reference: `#routes/${activity.rid}`, fieldContent: names.ridName}
                ]
            )
    
            const btn = utils.createButtonLink("Update this activity",`#activities/${actID}/update`)
            const btn1 = utils.createButtonLink("Delete this activity",`#activities/${actID}/delete`)
    
            table.appendChild(btn)
            table.appendChild(btn1)
            mainContent.replaceChildren(table)
        })
    })
}

function createSport(mainContent){
    if(utils.isNotLoggedIn()) return
    const div = utils.createHeader("Create a sport")

    div.appendChild(utils.createInputElement("Name"))
    div.appendChild(utils.createInputElement("Description"))
    
    const button = utils.createButton("Create", function() {
        const input1 = document.getElementById("Name")
        const input2 = document.getElementById("Description")
        const options = utils.makeRequest("POST", {name : input1.value, description : input2.value})
        fetch(API_BASE_URL + "sports", options).then(res => res.json()).then(sports => {
            console.log(sports)
            window.location.hash = "sports"
        })
    })
    
    div.append(button)
    mainContent.replaceChildren(div)
}

function createRoute(){
    if(utils.isNotLoggedIn()) return
    const div = utils.createHeader("Create a route")

    div.appendChild(utils.createInputElement("startLocation"))
    div.appendChild(utils.createInputElement("endLocation"))
    div.appendChild(utils.createInputElement("distance"))
    
    const button = utils.createButton("Create", function() {
        const input1 = document.getElementById("startLocation")
        const input2 = document.getElementById("endLocation")
        const input3 = document.getElementById("distance")

        const options = utils.makeRequest("POST", {startLocation : input1.value, endLocation : input2.value, distance : input3.value})
        fetch(API_BASE_URL + "routes", options).then(res => res.json()).then(student => {
            console.log(student)
            window.location.hash = "routes"
        })
    })
    div.append(button)
    mainContent.replaceChildren(div)
}

function updateRoute(mainContent, params){
    if(utils.isNotLoggedIn()) return
    const idRoute = params.path.id
    const div = utils.createHeader("Update a route")

    div.appendChild(utils.createInputElement("endLocation"))
    div.appendChild(utils.createInputElement("distance"))

    const button = utils.createButton("Update", function() {
        const input2 = document.getElementById("endLocation")
        const input3 = document.getElementById("distance")

        const options = utils.makeRequest("PUT", {endLocation : input2.value, distance : input3.value})
        fetch(API_BASE_URL + `routes/${idRoute}`, options).then(res => res.json()).then(student => {
            console.log(student)
            window.location.hash = `routes/${idRoute}`
        })
    })

    div.append(button)
    mainContent.replaceChildren(div)
}

function createActivity(mainContent){
    if(utils.isNotLoggedIn()) return
    const div = utils.createHeader("Create a activity")

    div.appendChild(utils.createInputElement("date", "2010-05-03"))
    div.appendChild(utils.createInputElement("duration", "13:10:10.200"))

    let dropDownSport
    fetch(API_BASE_URL + "sports").then(res => res.json()).then(sports => {
        const values = []
        sports.forEach(s => { values.push({text: s.name, sid: s.sid })})
        dropDownSport = utils.createDropDown("Sports", values)
        div.appendChild(dropDownSport)
    }).then(() => {
        let dropDownRoute
    fetch(API_BASE_URL + "routes").then(res => res.json()).then(routes => {
        const values = []
        routes.forEach(r => { values.push({text: `start: ${r.startLocation}. end: ${r.endLocation}`, rid: r.rid })})
        values.push({text: "null", rid: -1 })
        dropDownRoute = utils.createDropDown("Routes", values)
        div.appendChild(document.createElement("br"))
        div.appendChild(dropDownRoute)
        }).then(() => {
            const button = utils.createButton("Create", function() {
                const input1 = document.getElementById("date").value
                const input2 = document.getElementById("duration").value
                const input3 = dropDownSport.childNodes[dropDownSport.selectedIndex].id
                const isRouteNull = dropDownRoute.childNodes[dropDownRoute.selectedIndex].text=="null"
                let input4 = null
                if(!isRouteNull) input4 = dropDownRoute.childNodes[dropDownRoute.selectedIndex].id
                
                const options = utils.makeRequest("POST", {date : input1, duration : input2, rid : input4})
                fetch(API_BASE_URL + `sports/${input3}/activities`, options).then(res => res.json()).tndhen(student => {
                    console.log(student)
                    window.location.hash = "activities"
                })
            })
            div.appendChild(document.createElement("br"))
            div.append(button)
            mainContent.replaceChildren(div)
        })
    })
}

function updateActivity(mainContent, params){
    if(utils.isNotLoggedIn()) return
    const idActivity = params.path.id
    const div = utils.createHeader("Update a activity")

    div.appendChild(utils.createInputElement("date", "2010-05-03"))
    div.appendChild(utils.createInputElement("duration", "13:10:10.200"))

    const button = utils.createButton("Update", function() {
        const input1 = document.getElementById("date")
        const input2 = document.getElementById("duration")
        
        fetch(API_BASE_URL + `activities/${idActivity}`, utils.makeRequest("PUT", {date : input1.value, duration : input2.value})).then(res => {
            if(!res.ok) {
                res.json().then(messsage => {
                    alert(`Error ${res.status}, ${res.statusText}, ${messsage.message}`)
                })
            } else {
                res.json().then(student => {
                    console.log(student)
                    window.location.hash = `activities/${idActivity}`
                })
            }
        })
    })

    div.append(button)
    mainContent.replaceChildren(div)
}

function deleteActivity(mainContent, params){
    if(utils.isNotLoggedIn()) return
    const idActivity = params.path.id
    const div = utils.createHeader("Delete a activity")
    
    const button = utils.createButton("Delete", function() {
        fetch(API_BASE_URL + `activities/${idActivity}`, utils.makeRequest("DELETE", {})).then(res => res.json()).then(student => {
            console.log(student)
            window.location.hash = "activities"
        })
    })

    div.append(button)
    mainContent.replaceChildren(div)
}

function loginUser(mainContent){
    const loginLink = document.getElementById("login")
    if(loginLink.text=="Logout"){
        sessionStorage.removeItem("token")
        userLoginNameEmail = undefined
        loginLink.text = "Login"
        window.location.hash = "home"
        alert("Logged out")
        return
    }
    const div = utils.createHeader("Username: ", "h3", true)
    const email = utils.createInputElement("", "filipesporting@hotmail.com", "text", "email", true) //TODO: na entrega deixar só @hotmail.com
    div.append(email)

    div.append(utils.createHeader("Password: ", "h3", true))
    const pass = utils.createInputElement("", "", "password", "password", true)
    div.append(pass)
    
    const buttons = document.createElement("div")
    buttons.style = "text-align:center"
    buttons.append(utils.createButton("Register", createUser, null, null, true, false))
    buttons.append(utils.createButton("Login", attemptLogin, null, null, true, false))
    pass.appendChild(buttons)
    mainContent.replaceChildren(div)
}

function createUser(){
    const a = getEmailAndPass()
    let name = undefined
    do {
        if(name!=undefined) alert("Please provide a non empty name")
        name = prompt("Please enter your name", "name");
    } while(name == "")
    
    getCredentials("users", utils.makeRequest("POST", {name: name, email: a.email, password: a.password}), a)
}

function attemptLogin(){
    const a = getEmailAndPass()
    getCredentials("login", utils.makeRequest("POST", {email: a.email, password: a.password}), a)
}

function getEmailAndPass() {
    return { email: document.getElementById("email").value, password: document.getElementById("password").value}
}

function getCredentials(createUser_OrLogin, requestBody, emailAndpass){
    if(createUser_OrLogin== "login" || createUser_OrLogin=="users"){
        fetch(API_BASE_URL + createUser_OrLogin, requestBody).then(res => {
            if(!res.ok) {
                res.json().then(msg => {
                    let message = "Request error"
                    if(msg.message.includes("email already in use")) message = "email already in use"
                    else if(msg.message.includes("email format error")) message = "email format error"
                    else if(res.status==404) message = "User not found"
                    else if(res.status==401) message = "Wrong passwod"
                    else if(res.status==400) message = "Credentails are empty or email is not well formatted"
                    alert(`Error ${res.status}, ${res.statusText}, ${message}`)
                })
            } else {
                res.json().then(UserLoginResponse => {
                    sessionStorage.setItem("token", UserLoginResponse.uuid)
                    console.log("obtained token: "+UserLoginResponse.uuid)
                    userLoginNameEmail = emailAndpass.email.split("@")[0]
                    const loginLink = document.getElementById("login")
                    loginLink.text = "Logout"
                    window.location.hash = "home"
                })
            }
        })
    }
}

export const handlers = {
    getHome,
    getUsers,               //Supports query parameters: limit, skip, sid & rid (requires both)
    getUser,                //Requires uid
    getSports,              //Supports query parameters: limit and skip
    getSport,               //Requires sid
    getRoutes,              //Supports query parameters: limit and skip
    getRoute,               //Requires rid
    getActivitiesOfASport,  //Requires sid. Supports query parameters: limit and skip
    getActivitiesOfAUser,   //Required uid. Supports query parameters: limit and skip
    getActivities,          //Supports query parameters: limit, skip, sid, orderBy, date, rid
    getActivity,            //Requires aid
    createRoute,
    createSport,
    createActivity,
    updateSport,
    updateRoute,
    updateActivity,
    deleteActivity,
    loginUser
}

export default handlers