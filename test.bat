REM #!cmd.exe

@ECHO ON

REM wget --directory-prefix .bach https://raw.githubusercontent.com/sormuras/bach-init/main/init.java

java init.java

PATH=%PATH%;.bach\bin

bach --verbose info
