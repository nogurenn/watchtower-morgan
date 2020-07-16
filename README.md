# watchtower_morgan

## General

This is an small REST backend built on Scala Play Framework, Flyway db migrations, and Slick db ORM. Database connection pooling is powered by HikariCP, while logging by Logback -- both included as Play batteries.

Stripped down to the manageable minimum for our context, this project would only have the HTTP server, and db migrations and ORM.

For our purposes, it is not fully production-ready. Some engineering decisions in the project were deliberately not designed for production use and quality maintenance. This project was blowing up for a `GET` query.

## Setup
If just running the app, install `docker` and `docker-compose` for your OS, and proceed to _Running_ section.

If bootstrapping the project from scratch, install the following
* `jdk 11` - Java 11 (remember to set $JAVA_HOME accordingly)
* `sbt 1.3.x` - Scala Build Tool
* `scala 2.13` - Scala (optional, sbt will handle the project-specific scala binary)
* `postgresql 12.x` - PostgreSQL

For an IDE, use `vscode` or `intellij idea`. Both have mature plugins for Scala development.

If using `homebrew` on Mac OS X, here you go.
```bash
$ brew tap AdoptOpenJDK/openjdk
$ brew cask install adoptopenjdk11
# set your java_home to this when running sbt/scala (/usr/libexec/java_home)

$ brew install sbt scala postgresql

# To start/stop the db server
$ brew services start postgresql    #  ... stop postgresql
$ 

```

## Running

```bash
$ cd <go/to/project_root>

$ docker-compose up -d

# containers will start quickly, but running app will take a while.
# wait until http server is running (`Listening for HTTP on ...`)
$ docker logs -f watchtower-morgan_app_1

# when postgres server is ready for connections, start migrations and run task for data seeding
$ docker exec -it watchtower-morgan_app_1 sbt flywayMigrate
$ docker exec -it watchtower-morgan_app_1 sbt applyInitialData

# done. query api with a client.
```

## Notes

You may encounter two different warnings and one erratic shutdown error. All our just trivial quirks. No real impact on actual runtime and performance.
