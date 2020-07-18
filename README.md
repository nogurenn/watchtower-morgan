# watchtower_morgan

## General

This is a small REST backend built on Scala Play Framework, Flyway db migrations, and Slick db ORM. Database connection pooling is powered by HikariCP, while logging by Logback -- both included as Play batteries.

Stripped down to the manageable minimum for our context, this project would only have the HTTP server, and db migrations and ORM.

For our purposes, it is not fully production-ready. Some engineering decisions in the project were deliberately not designed for production use and quality maintenance. This project was blowing up for a `GET` query.

Dataset has one buggy datetime pattern. See below. Is it `M/d` or `d/M`? I decided to interpret `2/1` as `month/day` to remain consistent with the other present date formats.
```
2/1/2020 1:52
```

## Setup
If just running the app, install `docker` and `docker-compose` for your OS, and proceed to [Running with Docker](#running-with-docker) section.

If bootstrapping the project from scratch, install the following
* `jdk 11` - Java 11 (remember to set $JAVA_HOME accordingly)
* `sbt 1.3.x` - Scala Build Tool
* `scala 2.13` - Scala (optional, sbt will handle the project-specific scala binaries)
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

# go to project root
$ cd watchtower-morgan

# apply db migrations
$ sbt flywayMigrate
```

## Running
Some notes when running the app for the first time.
```bash
# Logger will tell you once the app is ready to receive the relevant requests.
# Watch out for the following three log outputs (in any order).
#
# INFO  ApplicationStart  Awaiting first-time seeding of covid_observations table.
# ...
# INFO  play.core.server.AkkaHttpServer  Listening for HTTP on /0:0:0:0:0:0:0:0:9000
# ...
# INFO  ApplicationStart  Inserted 15131 rows.
#
# Do note that the app processes requests asynchronously. If you perform the test GET request
# while the bulk-insert of the seed is not yet done, you might get an empty response body.
```

### Running standalone
```bash
$ cd my/project_root/dir

$ sbt runProd   # or sbt run for dev mode (cold start)
```


### Running with Docker
```bash
$ cd my/project_root/dir

# spin up db first and wait for it to be ready
# no straightforward health-check available.
# docker-compose `depends_on` and `links` do not guard against application-level boot
$ docker-compose up -d db

# spin up app.
$ docker-compose up app

$ curl -i -H "Accept: application/json" "localhost:9000/top/confirmed?observation_date=2020-02-01&max_results=10" 
```
