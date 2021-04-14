@ECHO OFF

IF "%~1" == "boot" (
  jshell --module-path .bach\bin --add-modules com.github.sormuras.bach
  EXIT /B %ERRORLEVEL%
)

IF "%~1" == "init" (
  IF "%~2" == "" (
    ECHO "Usage: bach init VERSION"
    EXIT /B 1
  )
  jshell https://github.com/sormuras/bach-init/raw/main/versions/%2.jshell
  EXIT /B %ERRORLEVEL%
)

java --module-path .bach\bin --module com.github.sormuras.bach %*
EXIT /B %ERRORLEVEL%
