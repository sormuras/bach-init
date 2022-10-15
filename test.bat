@ECHO ON

REM wget https://init.java.bach.run -x -O .bach/init.java

java init.java

PATH=%PATH%;.bach\bin

bach --verbose info
