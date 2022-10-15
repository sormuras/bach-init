REM #!cmd.exe

@ECHO ON

REM wget --no-verbose --force-directories --output-document .bach/init.java https://init.java.bach.run

java init.java

PATH=%PATH%;.bach\bin

bach --verbose info
