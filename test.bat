REM #!cmd.exe

@ECHO ON

mkdir example && cd example

echo Files.copy(URI.create("https://init.java.bach.run").toURL().openStream(), Files.createDirectories(Path.of(".bach")).resolve("init.java")) | jshell -

java .bach\init.java

call .bach\bin\bach --verbose info

cd ..

rmdir /Q /S example
