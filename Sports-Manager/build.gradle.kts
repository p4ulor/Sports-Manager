plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.springframework.boot") version "2.6.3" //NECESSARIO PARA COMPILAR O JAR COM AS DEPENDENCIAS, mas os testes nao correm com isto se os executá-los no intellij às vezes? Documentaçao -> https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.http4k", name = "http4k-core", version = "4.20.2.0")
    implementation(group = "org.http4k", name = "http4k-server-jetty", version = "4.20.2.0")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.2")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.3.2")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha5")
    runtimeOnly(group = "org.slf4j", name = "slf4j-simple", version = "2.0.0-alpha5")
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    testImplementation(kotlin("test"))
}

//Build com o martelo verde -> compila para build\classes\kotlin\. Não executa as "tasks"! (Pelo menos no meu IntelliJ)

//This task can be ran in the gradle window: Tasks->other->copyRuntimeDependencies
tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

//This task can be ran in the gradle window: Tasks -> build -> bootJar
//Uses sprint-boot and puts everything in a .jar (except 'static-content' of course)
tasks.bootJar {
    this.archiveFileName.set("2122-2-LEIC41N-G01.jar")
}

//The task build->jar alone, without any modifications compiles our code to a .jar, without the libraries, and without the manifest referencing the libraries!

//The task build->jar task modified: Creates a .jar with our code and manifest that references the dependency libraries externally
/*tasks.named<Jar>("jar") {
    dependsOn("copyRuntimeDependencies")
    manifest {
        attributes["Main-Class"] = "pt.isel.ls.sports-serverKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
    archiveFileName.set("2122-2-LEIC41N-G01-plain-nolibs.jar")
}*/
