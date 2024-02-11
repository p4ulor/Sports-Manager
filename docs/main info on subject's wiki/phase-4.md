# Phase 4

## Introduction
This document describes the requirements for the fourth and final phase of the Software Laboratory project.

## Requirements

### Additional Operations
 
* Update the create user operation, to receive a password.
* Create a new operation that receives the user's email and password, and returns the user's token.  

### Single Page Application
* Produce views to create a new user (Sign In) and to obtain the user's token (Login). 
* The received token should be stored until the user closes the browser window/tab or explicitly executes logout. 

### Hosting on Heroku
Host the application on the [Heroku](https://www.heroku.com/).
* Create an [Heroku](https://www.heroku.com) free account. Heroku is an example of a Platform-as-a-Service (PaaS) provider.
* Install the [Heroku CLI (Command Line Interface)](https://devcenter.heroku.com/articles/heroku-cli) on all the development machines.
* On the Heroku web site, create a new [application](https://dashboard.heroku.com/apps)
  * The name should follow the following structure:`isel-ls-2122-2-<turma>-g<número-do-grupo>`.
  * Select the "Europe" region.
* On the application home page, provision the "Heroku Postgres" add-on using the "Hobby Dev - Free" plan.
  * Add a `JDBC_DATABASE_URL` environment variable to your heroku application, using the credentials available in heroku dashboard.
* Create the tables in the heroku postgres database, use psql, pgAdmin or a similar tool.
* On the command line, `gradlew build`
* Install docker desktop
* Add the Dockerfile to the the root of the group repository, this file should contain all the information to create the docker image, see our example [here](https://github.com/isel-leic-ls/2122-2-common/blob/main/Dockerfile)
* On the command line, do `heroku container:login`.
* Create the docker image and push the image to the Heroku Registry, on the command line, do `heroku container:push web -a isel-ls-2122-2-<turma>-g<número-do-grupo>`
* On the command line, do `heroku container:release web -a isel-ls-2122-2-<turma>-g<número-do-grupo>`

To launch application in developer machine

* `docker build -t <name:x>` .
* `docker run -d -p 9000:8080 --env PORT=8080 --env JDBC_DATABASE_URL="jdbc:postgresql://host.docker.internal/<database>?user=<username>&password=<password>" <name:x>`


### Refactoring code
This phase should be used to critically review all code baseline, and refactoring to improve code quality should be a goal. 
Also, more tests should be produced ir order to increase the overall coverage.   

### Report
The technical report should be updated and/or extended with the relevant technical information.
The sections developed in the previous phases can be improved or changed.
**There should not be a separate report for each phase.**

