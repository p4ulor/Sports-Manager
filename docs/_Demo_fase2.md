## [Guia do demo](https://github.com/isel-leic-ls/2122-2-common/wiki/demonstration#steps-to-reproduce-in-the-phase-2-demonstration)

1. `git clone https://github.com/isel-leic-ls/2122-2-LEIC41N-G01`
2. Inside the cloned project folder: `git checkout 0.2.0`
3. Open cmd.exe and execute:
4. `gradle clean` and then `gradle build`
5. Repeat previous step
6. Executar: `java -jar build/libs/2122-2-LEIC41N-G01.jar postgres` 
## Com postman:
7. List all sports **--->** /sports
8. Create one user and one route    **--->** /users e /routes
9. Try to create another user with the same email **--->** /users
10. Create two sports, cycling and 10km run **--->** /sports
11. Show the details of the 10km run **--->** sports/5
12. Try to get the sport details for a non-existing sport **--->** /sports/9999
13. Add one activity to the "10km run" sport without a route **--->** /sports/5/activities
14. Show the details of the new activity **--->** /activities/7
15. Add three activities to the "cycling" sport with the same route **--->** sports/4/activities
16. Try to add an activity for a non-existing route **--->** sports/4/activities
17. List all cycling activities order by duration time, ascending and descending **--->** /activities?sid=4&orderBy=ASC e /activities?sid=4&orderBy=DESC
18. List all cycling activities format using a page of length 2, going through all the pages  **--->** /activities/?sid=4&orderBy=DESC&limit=2 e dps /activities/?sid=4&orderBy=DESC&limit=2&skip=2
19. Delete two cycling activities **--->** /activities/8 e /activities/9
20. List all cycling activities **--->** /activities/?sid=4&orderBy=ASC
21. Launch the browser
22. Go to the application root and follow all edges of the navigation graph
![](Navigation.png)

## If you don't have gradle:
- [Go to download page](https://gradle.org/install/#manually). [Download most recent version](https://gradle.org/releases/)
- Put contents of zip folder in a persistent location. [And add to your OS enviornment variables](https://www.360logica.com/blog/how-to-configure-gradle-on-windows-machine/)

## If you don't want to use spring boot
If you don't wanna use `id("org.springframework.boot") version "2.6.3"` in the plugins to allow a spring task to build the whole proeject into a jar, use the following commands
- gradle build
- gradlew.bat copyRuntimeDependencies
- java -classpath build/libs/classes/kotlin/main pt.isel.ls.api.Sports_serverKt postgres

classpath can be replaced by `-cp`
