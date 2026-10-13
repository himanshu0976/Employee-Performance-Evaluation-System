@echo off
set "MAVEN_HOME=C:\Users\hp\.m2\apache-maven-3.9.6"
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
) else (
    mvn %*
)
