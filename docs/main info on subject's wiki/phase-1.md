## Goals ##

The Software Laboratory project is comprised by the analysis, design and implementation of an information system with a web interface, to manage physical activities, like running or cycling. 
Its development is divided into 4 phases, with incremental requirements published before the beginning of each phase. On this first phase, the interaction with the information system is done via a Web API

## Domain

* A _user_ is characterized by:
  * a unique number (required)
  * a name (required)
  * an unique email (required).

* A _sport_ is characterized by:
  * a unique number (required)
  * a name (required)
  * a description (optional)
  * the user that created the sport (required).

* A _route_ is characterized by:
  * a unique number (required)
  * the start location (required)
  * the end location (required)
  * a distance (required)
  * the user that created the route (required).

* An _activity_ is characterized by:
  * a unique number (required)
  * a date (required)
  * a duration time (required)
  * each activity is also associated to a user, a sport, and optionally to a route. 


## Phase 1 requirements

For each functionality, the corresponding HTTP *endpoint* must be defined. The description of the application API (i.e all application endpoints) must appear on the repository in an [OpenAPI](https://oai.github.io/Documentation/specification.html) file, named `docs/sports-api-spec.json` (or .yml). The repository must also contain the Postman collection export withof the with requests that validate the API, in a file named  `docs/sports-api-test.json`.

In all create operations, a user token must be sent in the [Authorization header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization) using a Bearer Token. This token is generated at user creation, and consists of a UUID string (you can use UUID class from `java.util` package for ID generation).

For phase 1, the management application must support the following operations.

#### User Management

* Create a new user, given the following parameters

  * `name` -  the user's name   
  * `email` - the user's unique email.

  Returns the user's token and the user’s identifier.
 
* Get the details of a user. 
* Get the list of users.

#### Route Management

* Create a new route, given the following parameters

  * `startLocation` -  the route start location name   
  * `endLocation` - the route end location name.
  * `distance` - the route distance in km (e.g. 15.2).

  Returns the route unique identifier.
 
* Get the details of a route. 
* Get the list of routes.

#### Sports and Activities Management

* Create a new sport, given the following parameters
    * `name` - the sport's name.
    * `description` - the sport's description.  

  Returns the sport's unique identifier.

* Get a list with all sports.
* Get the detailed information of a sport.

* Creates a new activity for the sport identified by `sid`, given the following parameters
    * `duration` - duration time in the format `hh:mm:ss.fff`
    * `date` - activity date in the format `yyyy-mm-dd` 
    * `rid` - route identifier (optional)

  Returns the physical activity unique identifier.

* Get all the activities of a sport.
* Get the detailed information of an activity.
* Delete an activity.
* Get all the activities made from a user. 
* Get a list with the activities, given the following parameters
  * `sid` - sport identifier
  * `orderBy` - order by duration time, this parameter only has two possible values - ascending or descending   
  * `date` - activity date (optional)
  * `rid` - route identifier (optional)

## Non-functional requirements

The application must be developed with Kotlin technology. To handle/receive HTTP requests, the [HTTP4K](https://www.http4k.org) library must be used. The body serialization/deserialization should be done using the [kotlinx.serialization library](https://kotlinlang.org/docs/serialization.html)- You can see a functional usage example of both libraries [here](https://github.com/isel-leic-ls/2122-2-common/blob/main/src/main/kotlin/pt/isel/ls/http/HTTPServer.kt). For dates use [kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime) library.

The data that are specific to the application, which can be created, altered and deleted must be **stored in a Postgres database**. Tests run using data stored in memory, not the database. 
 
The following non-functional requirements will be highly valued. 
* Readability.
* Testability, including on machines not belonging to the development team.

## Guidelines about design and implementation

At the **beginning** of the development, the server application  should store the data in memory and the solution should start with 4 files:

* <code>SportsServer.kt</code> - file that constitutes the entry point to the server application
* <code>SportsWebApi.kt</code> - implementation of the HTTP routes that make up the REST API of the web application
* <code>SportsServices.kt</code> - implementation of the logic of each of the application's functionalities
* <code>SportsDataMem.kt</code> - access to data, stored in memory.

The dependencies between these are as follows:

<pre>
sports-server.kt -> sports-web-api.kt -> sports-services.kt -> sports-data-mem.kt                                                               
</pre>

During the development of this phase other files can (and should) be created, in order to structure the code in a more logical and readable form. Solutions with large code files are highly discouraged.

