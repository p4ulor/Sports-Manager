# Work Environment 

## Requirements

* [JDK 11](http://java.sun.com/javase/downloads/)
* [IntelliJ IDEA Community Edition](http://www.jetbrains.com/idea/download/)
* [Git](https://git-scm.com/)
  * GUI for Windows - [GitExtensions](https://code.google.com/p/gitextensions/)
    * Install version  "...SetupComplete..."
    * The tool also installs Git and KDiff3

## Gradle

[Gradle] (https://gradle.org/) is a tool for automating the processes associated with building software, such as: compiling, running tests, creating ".jar" files, and generating documentation from source files. This tool consists of a Java library and a script for execution on the command line ("gradlew.bat").

The tasks automated by Gradle are described in `.gradle` files, called _configuration scripts_, and typically with the name" build.gradle ".

The use of Gradle, or similar tools, has the following advantages:

* Simplification of tasks, such as compiling or executing tests, by automating them. This aspect is especially important in tasks that involve the execution of more than one command in a well-defined order.
* Process consistency and error reduction by minimizing human intervention in carrying out the task.
* Integration with external tools, such as in [continuous integration](http://martinfowler.com/articles/continuousIntegration.html) scenarios.
* Simplifying the deploy of the application on multiple computers, particularly when different from those where it is developed.
* In the context of Software Laboratory course, it is required that the developed project is completely built, installed, and tested through the execution of Gradle _tasks_.

## Files and Folders Structure

It is proposed to use a standardized structure for the project files and folders. The adoption of this standardized structure reflects good practices in the development of software projects (eg [Maven] (http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)) aiming to:

* Facilitate the understanding and finding of the produced files, by students and teachers.
* Simplify the automation of tasks, such as compiling, generating documentation or producing ".jar" files.
* Allow the mobility of project folders between different machines.

### root "/"

The root folder only contains the file "build.gradle", with a description of the tasks ("tasks") associated with the construction of the project artifacts. The rest of the project's content is present in internal folders.

### "src/" folder

The "src /" folder contains all source files, i.e. files created by the development team. This folder is divided into folders:

* "src / main /" with the files containing the features implemented in the project;
* "src / test /" with the files for testing the project.

Each of these folders is subdivided into the following folders:

* "... / kotlin" with Kotlin source files
* "... / sql" with files containing SQL scripts

The internal structure of the "... / kotlin" folders corresponds to the adopted "packages" structure.

### "build/" folder

The "build/" folder contains all files produced automatically from the source files, namely: ".class" and ".jar" files with Java classes; ".html" files with the documentation produced by JavaDoc.