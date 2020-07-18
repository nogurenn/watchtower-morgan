FROM hseeberger/scala-sbt:11.0.7_1.3.13_2.13.3

WORKDIR /home/sbtuser/code

COPY . .

USER sbtuser

EXPOSE 9000

CMD ["sbt", "flywayMigrate", "runProd"]
