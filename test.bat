@ECHO ON

jshell --show-version https://git.io/bach-init

PATH=%PATH%;.bach\bin

bach boot test.jshell

bach build
