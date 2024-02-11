const ENTITIES_PER_PAGE = 2

function createElement() { //either receives an element tag(arg 0) with text (arg 1) and returns 1 element, or receives 1 element tag (arg 0) with successive parameters of HTML elements
    var element = document.createElement(arguments[0])
    if(typeof arguments[1] != 'string'){
        console.log("It's an HTMLElement")
        for (var i = 1; i < arguments.length; i++) {
            element.appendChild(arguments[i])
        }
        return element
    }

    const text = document.createTextNode(arguments[1])

    if(arguments[2]){ //it's a link
        const anchor = document.createElement("a")
        anchor.appendChild(text)
        anchor.href=arguments[2]
        element.append(anchor)
    } else element.appendChild(text)

    return element;
}

function createHeader(title, hSize, setCenter, id){
    const div = document.createElement("div")
    div.id = title!="" ? "title" : "list"
    if(id) div.id=id
    let h1 = document.createElement("h1")
    if(hSize) h1 = document.createElement(hSize)
    const text = document.createTextNode(title)
    if(setCenter) h1.style.textAlign = "center"
    h1.appendChild(text)
    div.appendChild(h1)
    return div
}

function createNextPreviousLink(elementType, name, entity, page){
    let pageNumber = 0
    if(page) pageNumber = page
    if(name=='Previous'){
        if(pageNumber!=0) pageNumber--
    }
    else pageNumber++
    var element = document.createElement(elementType)
    let text = document.createTextNode(name); 
    const anchor = document.createElement("a")
    anchor.appendChild(text)
    
    anchor.onclick = function(){
        anchor.href = "#".concat(entity).concat(`?page=${pageNumber}`) //TODO: fazer com q tb acrescente outros params
        return true
    }
    anchor.href = true //highlights the text as blue (as clickable)
    element.append(anchor)
    return element 
}

function getFormattedQuery(params){
    params.query.formatted = "/?"
    if(params.query.rid){
        if(params.query.formatted.length>2) params.query.formatted = params.query.formatted.concat("&")
        params.query.formatted = params.query.formatted.concat("rid="+params.query.rid)
    }
    if(params.query.sid){
        if(params.query.formatted.length>2) params.query.formatted = params.query.formatted.concat("&")
        params.query.formatted = params.query.formatted.concat("sid="+params.query.sid)
    }
    if(params.query.page!=undefined) {
        //let page = 0
        if(params.query.formatted.length>2) params.query.formatted = params.query.formatted.concat("&")
        const skip = ENTITIES_PER_PAGE * params.query.page
        params.query.formatted = params.query.formatted.concat("skip=" + skip + "&limit="+ENTITIES_PER_PAGE)
        console.log(params.query.formatted)
    }
    if(params.query.formatted=="/?") params.query.formatted=""
    return params
}

function createDropDown(name, values){
    let select = document.createElement("select")
    select.name = name;
    select.id = name
    select.onchange
    for (const val of values) {
        let option = document.createElement("option")
        option.text = val.text
        option.id = val.sid
        select.appendChild(option);
    }
    return select
}

function createCheckBoxes(name, values){
    const checkboxes = document.createElement("div")
    checkboxes.id = name
    for (const val of values) {
        const option = document.createElement("input")
        option.type = "checkbox"
        option.value = val.rid
        option.onclick = function() {
            checkboxes.childNodes.forEach((item) => {
                if (item !== option) item.checked = false
            })
        }
        checkboxes.appendChild(option);
        checkboxes.appendChild(document.createTextNode(val.text))
        checkboxes.appendChild(document.createElement("br"))
    }
    return checkboxes
}

function createInputElement(name, defaultValue, type, id, setCenter){ //o id do elemento é igual ao nome
    const div = document.createElement("div")
    if(setCenter) div.style = "text-align:center"
    
    const input = document.createElement("input")
    if(setCenter) input.style.textAlign = "center"
    
    if(id) input.id = id
    else input.id = name
    if(defaultValue) input.defaultValue = defaultValue
    if(type) input.type = type
    else input.type = "text"
    div.appendChild(input)
    div.append(document.createTextNode(name))
    return div
}

function createTokenHeader(token){
    let a = new Headers()
    a.append("Content-Type", "application/json")
    a.append("Authorization", `Bearer ${token}`)
    return a
}

function createButton(name, onclick, functionParam1, functionParam2, setCenter){
    const button = document.createElement("button")
    button.style.margin = "10px"
    button.innerHTML = name
    button.onclick = function() {
        onclick(functionParam1, functionParam2);
        console.log("Button clicked")
    }
    return button
}

function createButtonLink(){
    const anchor = document.createElement("a")
    const createText = document.createTextNode(arguments[0]);
    anchor.appendChild(createText)
    anchor.href=arguments[1]
    anchor.appendChild(document.createElement("br"))
    return anchor
}

function isNotLoggedIn(){
    if(sessionStorage.getItem("token")==null){
        alert("You must login to perform this action")
        let hash = window.location.hash
        window.location.hash = hash.substring(0, hash.lastIndexOf("/"))
        return true
    }
    return false
}

function makeRequest(method, requestBody){
    return {
        method: method,
        headers: createTokenHeader(sessionStorage.getItem("token")),
        body: JSON.stringify(requestBody)
    }
}

function createTable(headersToHave, object, fieldsToShow, makeResponsive){
    let div = document.createElement('div')
    div.style='overflow-x:auto'
    
    let table = document.createElement('table')
    table.style.display = 'block'
    table.style.marginTop = '20px'
    let headerRow = document.createElement('tr')
    headersToHave.forEach(headerText => {
        let header = document.createElement('th')
        header.style.border = 'solid 1px black'
        header.style.padding = '5px'
        let textNode = document.createTextNode(headerText)
        header.appendChild(textNode)
        headerRow.appendChild(header)
    })
    table.appendChild(headerRow)

    let row
    if(Array.isArray(object)){
        object.forEach(obj => { //theres multiple rows
            row = fillUpTheTable(obj, fieldsToShow, true)
            table.appendChild(row)
        })
        
    } else { // there 1 row
        row = fillUpTheTable(object, fieldsToShow)
        table.appendChild(row)
    }
    if(makeResponsive) {
        div.append(table)
        return div
    }
    return table
}

function fillUpTheTable(object, fieldsToShow, multiple){
    let row = document.createElement('tr')
    Object.keys(object).forEach(field => {
        fieldsToShow.forEach(fieldInfo => {
            if(typeof(fieldInfo)=='string') fieldInfo = {name: fieldInfo}
            if(fieldInfo.name==field) {
                if(fieldInfo.reference==undefined) extracted(row, object[field])
                else if(multiple){
                    var text = object[field]
                    if(fieldInfo.fieldContent) text = fieldInfo.fieldContent
                    utils.extractedWithLink(row, text, fieldInfo.reference.path.concat(`${object[fieldInfo.reference.id]}`))
                }
                else {
                    if(fieldInfo.fieldContent) object[field] = fieldInfo.fieldContent
                    utils.extractedWithLink(row, object[field], fieldInfo.reference)
                }
            }
        })
    })
    return row
}

function extracted(row,text) {
    let cell1 = document.createElement('td');
    let textNode1 = document.createTextNode(text);
    cell1.style.border = 'solid 1px black';
    cell1.style.padding = '5px';
    cell1.appendChild(textNode1);
    row.appendChild(cell1);
}

function extractedWithLink(row,text,link) {
    const cell = document.createElement("td")
    const anchor = document.createElement("a")
    let textNode1 = document.createTextNode(text)
    cell.style.border = 'solid 1px black';
    cell.style.padding = '5px';
    anchor.appendChild(textNode1)
    anchor.href=link
    cell.appendChild(anchor)
    row.append(cell)
}

function getIDsNamesFromUserSportRoute(API_BASE_URL, uid, sid, rid){
    let uidName
    let sidName
    let ridName

    return fetch(API_BASE_URL + `users/${uid}`).then(res => res.json()).then(user => {
        uidName = user.name
        return fetch(API_BASE_URL + `sports/${sid}`).then(res => res.json()).then(sport => {
            sidName = sport.name
        }).then(() => {
            return fetch(API_BASE_URL + `routes/${rid}`).then(res => res.json()).then(route => {
                ridName = `${route.startLocation}-${route.endLocation}` 
                return {uidName: uidName, sidName: sidName, ridName: ridName}
            })
        })
    })
}

const utils = {
    createElement,
    createHeader,
    createNextPreviousLink,
    createDropDown,
    createCheckBoxes,
    createInputElement,
    createButton,
    createButtonLink,
    getFormattedQuery,
    isNotLoggedIn,
    makeRequest,
    extracted,
    extractedWithLink,
    createTable,
    getIDsNamesFromUserSportRoute
}

export default utils
