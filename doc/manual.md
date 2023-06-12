
# Introduction

grumpyrest is a Java REST server framework that does not use annotations, automatic dependency injection or reactive
streams, and minimizes the use of reflection. Instead,
* it leverages the Java type system to indicate the meaning of classes, fields and methods
* it calls constructors to create dependency objects, and passes constructor parameters to inject them
* it uses threads to achieve parallelism, and in particular virtual threads for highly parallel I/O

# Using grumpyrest

grumpyrest is not yet available on Maven Central. To use it, run the following command in its main folder (the one
containing the `settings.gradle` file):

    ./gradlew publishToMavenLocal

This will build and publish the libraries to your local Maven repo in `~/.m2`

You can then refer to it as `name.martingeisse:grumpyrest:0.1`

# Quick Start

For now, the grumpyrest-demo subproject is the easiest way to get an example up and running.

