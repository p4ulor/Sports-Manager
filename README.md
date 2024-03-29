# About
- A simple full-stack Web-App which manages (allows the creation, consulting (& searching & filtering) and editing) of sport activities by users. That's basically it.
- The back-end is good
- The front-end is kinda scuffed and the UI is raw-simple.

# Small demo

https://github.com/p4ulor/Sports-Manager/assets/32241574/b98dac19-6653-4752-ade6-bfa01610fa52

# Tech stack
More info [in report.md](./docs/report.md)
## Back-end
- It uses [http4k](https://github.com/http4k/http4k) as the toolkit to build the HTTP API
- The back-end doesn't create the Posgresql database, so you have to create it (using a DB management application like pgAdmin or Dbeaver per example) before running the server
- Manual dependency injection is performed

## Front-end
- Pure HTML, CSS and Javascript sent to the browser's client
- It's interesting to note how messy it can be to program the front-end without a framework (and thus how they make programming in front-end a lot easier)

# Note
Check that the .jar outputed by spring boot has the same name as the name indicated in the `Dockerfile` and in `RUN BAT.jar`

## Trivia
- This was a fundamental project in my programming learning journey, which was the start of the skyrocketing of my skill-set and motivation, taught me about creating API's, make good use of Kotlin, how to interact with databases, how to use gradle and docker
- It also thought me on how to think like a programmer, regarding the organisation, optimization and documentation of code.
- Not one of my greatest projects to showcase, but it is what is is. Currently not interested in refinining it

# Done by me and my gangstas
- [Raul](https://github.com/RaulJCS5)
- [Pedro](https://github.com/Gardete)
- Props to teacher [Filipe Freitas](https://github.com/fbfreitas), a real one
